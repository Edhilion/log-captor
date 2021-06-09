/*
 * Copyright 2019-2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nl.altindag.log;

import static java.util.stream.Collectors.toList;
import static nl.altindag.log.util.Mappers.toLogEvent;
import static org.apache.logging.log4j.LogManager.ROOT_LOGGER_NAME;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.test.appender.ListAppender;
import org.apache.logging.slf4j.Log4jLogger;
import org.slf4j.LoggerFactory;

import nl.altindag.log.model.LogEvent;
import nl.altindag.log.util.JavaUtilLoggingLoggerUtils;

/**
 * @author Sébastien Vicard (based on the work of Hakan Altindag)
 */
public final class LogCaptor implements AutoCloseable {

    private static final Map<String, Level> LOG_LEVEL_CONTAINER = new HashMap<>();
    public static final String APPENDER_NAME = "log-captor";

    private final Logger logger;
    private final ListAppender listAppender;

    private LogCaptor(String loggerName) {
        org.slf4j.Logger slf4jLogger = LoggerFactory.getLogger(loggerName);
        if (!(slf4jLogger instanceof Logger) && !(slf4jLogger instanceof Log4jLogger)) {
            throw new IllegalArgumentException(
                    String.format("SLF4J Logger implementation should be of the type [%s] but found [%s]. " +
                                    "Please remove any other SLF4J implementations during the test phase from your classpath of your project. " +
                                    "See here for an example configurations: https://github.com/Hakky54/log-captor#using-log-captor-alongside-with-other-logging-libraries",
                            Logger.class.getName(), slf4jLogger.getClass().getName()
                    )
            );
        }

        LoggerContext context = LoggerContext.getContext(false);
        logger = context.getLogger(loggerName);

        listAppender = ListAppender.newBuilder()
                .setName(APPENDER_NAME)
                .build();

        if (!LOG_LEVEL_CONTAINER.containsKey(logger.getName())) {
            LOG_LEVEL_CONTAINER.put(logger.getName(), logger.getLevel());
        }

        if (logger.getAppenders().containsKey(APPENDER_NAME)) {
            logger.removeAppender(logger.getAppenders().get(APPENDER_NAME));
        }

        logger.addAppender(listAppender);
        listAppender.start();

        JavaUtilLoggingLoggerUtils.redirectToSlf4j(loggerName);
        LOG_LEVEL_CONTAINER.putIfAbsent(logger.getName(), logger.getLevel());
    }

    /**
     * Captures all log messages
     */
    public static LogCaptor forRoot() {
        return new LogCaptor(ROOT_LOGGER_NAME);
    }

    /**
     * Captures log messages for the provided class
     */
    public static <T> LogCaptor forClass(Class<T> clazz) {
        return new LogCaptor(clazz.getName());
    }

    /**
     * Captures log messages for the provided logger name
     */
    public static LogCaptor forName(String name) {
        return new LogCaptor(name);
    }

    public List<String> getLogs() {
        return listAppender.getEvents().stream()
                .map(org.apache.logging.log4j.core.LogEvent::getMessage)
                .map(Message::getFormattedMessage)
                .collect(toList());
    }

    public List<String> getInfoLogs() {
        return getLogs(Level.INFO);
    }

    public List<String> getDebugLogs() {
        return getLogs(Level.DEBUG);
    }

    public List<String> getWarnLogs() {
        return getLogs(Level.WARN);
    }

    public List<String> getErrorLogs() {
        return getLogs(Level.ERROR);
    }

    public List<String> getTraceLogs() {
        return getLogs(Level.TRACE);
    }

    private List<String> getLogs(Level level) {
        return listAppender.getEvents().stream()
                .filter(logEvent -> logEvent.getLevel() == level)
                .map(org.apache.logging.log4j.core.LogEvent::getMessage)
                .map(Message::getFormattedMessage)
                .collect(toList());
    }

    public List<LogEvent> getLogEvents() {
        return listAppender.getEvents().stream()
                .map(toLogEvent())
                .collect(toList());
    }

    public void addFilter(Filter filter) {
        this.listAppender.addFilter(filter);
        filter.start();
    }

    /**
     * Overrides the log level property of the target logger. This may result that the overridden property
     * of the target logger is still active even though a new instance of {@link LogCaptor} has been created.
     * To roll-back to the initial state use: {@link LogCaptor#resetLogLevel()}
     * <p>
     * This option will implicitly include the following log levels: WARN and ERROR
     */
    public void setLogLevelToInfo() {
        setLogLevelTo(Level.INFO);
    }

    /**
     * Overrides the log level property of the target logger. This may result that the overridden property
     * of the target logger is still active even though a new instance of {@link LogCaptor} has been created.
     * To roll-back to the initial state use: {@link LogCaptor#resetLogLevel()}
     * <p>
     * This option will implicitly include the following log levels: INFO, WARN and ERROR
     */
    public void setLogLevelToDebug() {
        setLogLevelTo(Level.DEBUG);
    }

    /**
     * Overrides the log level property of the target logger. This may result that the overridden property
     * of the target logger is still active even though a new instance of {@link LogCaptor} has been created.
     * To roll-back to the initial state use: {@link LogCaptor#resetLogLevel()}
     * <p>
     * This option will implicitly include the following log levels: INFO, DEBUG, WARN and ERROR
     */
    public void setLogLevelToTrace() {
        setLogLevelTo(Level.TRACE);
    }

    /**
     * Overrides the log level property of the target logger. This may result that the overridden property
     * of the target logger is still active even though a new instance of {@link LogCaptor} has been created.
     * To roll-back to the initial state use: {@link LogCaptor#resetLogLevel()}
     */
    public void disableLogs() {
        setLogLevelTo(Level.OFF);
    }

    /**
     * Resets the log level of the target logger to the initial value which was available before
     * changing it with {@link LogCaptor#setLogLevelToInfo()}, {@link LogCaptor#setLogLevelToDebug()} or with {@link LogCaptor#setLogLevelToTrace()}
     */
    public void resetLogLevel() {
        Optional.ofNullable(LOG_LEVEL_CONTAINER.get(logger.getName()))
                .ifPresent(this::setLogLevelTo);
    }

    public void clearLogs() {
        listAppender.clear();
    }

    /**
     * Overrides the log level property of the target logger. This may result that the overridden property
     * of the target logger is still active even though a new instance of {@link LogCaptor} has been created.
     * To roll-back to the initial state use: {@link LogCaptor#resetLogLevel()}
     * <p>
     */
    private void setLogLevelTo(Level level) {
        LoggerContext context = logger.getContext();
        Configuration configuration = context.getConfiguration();

        LoggerConfig rootLoggerConfig = logger.get();
        rootLoggerConfig.removeAppender("Console");
        rootLoggerConfig.addAppender(configuration.getAppender("Console"), level, null);
        rootLoggerConfig.setLevel(level);
        context.updateLoggers();
    }

    @Override
    public void close() {
        listAppender.stop();
        logger.removeAppender(listAppender);
    }

}
