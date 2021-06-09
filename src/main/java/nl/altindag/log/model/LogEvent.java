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

package nl.altindag.log.model;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.apache.logging.log4j.message.Message;

/**
 * @author Hakan Altindag - Sébastien Vicard
 */
public final class LogEvent {

    private final Message message;
    private final String formattedMessage;
    private final String level;
    private final String loggerName;
    private final ZonedDateTime timeStamp;
    private final List<Object> arguments;
    private final Throwable throwable;
    private final Map<String, String> diagnosticContext;

    public LogEvent(Message message,
                    String formattedMessage,
                    String level,
                    String loggerName,
                    ZonedDateTime timeStamp,
                    List<Object> arguments,
                    Throwable throwable,
                    Map<String, String> diagnosticContext) {

        this.message = Objects.requireNonNull(message);
        this.formattedMessage = Objects.requireNonNull(formattedMessage);
        this.level = Objects.requireNonNull(level);
        this.loggerName = loggerName;
        this.timeStamp = Objects.requireNonNull(timeStamp);
        this.throwable = throwable;
        this.arguments = arguments;
        this.diagnosticContext = diagnosticContext;
    }

    public String getMessage() {
        return message.getFormat();
    }

    public String getFormattedMessage() {
        return formattedMessage;
    }

    public String getLevel() {
        return level;
    }

    public String getLoggerName() {
        return loggerName;
    }

    public ZonedDateTime getTimeStamp() {
        return timeStamp;
    }

    public List<Object> getArguments() {
        return arguments;
    }

    public Optional<Throwable> getThrowable() {
        return Optional.ofNullable(throwable);
    }

    public Map<String, String> getDiagnosticContext() {
        return diagnosticContext;
    }

}
