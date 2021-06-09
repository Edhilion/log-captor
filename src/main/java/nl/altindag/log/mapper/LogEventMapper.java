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

package nl.altindag.log.mapper;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.apache.logging.log4j.message.Message;

import nl.altindag.log.model.LogEvent;

/**
 * @author Hakan Altindag - Sébastien Vicard
 */
public final class LogEventMapper implements Function<org.apache.logging.log4j.core.LogEvent, LogEvent> {

    private static final LogEventMapper INSTANCE = new LogEventMapper();

    private LogEventMapper() {}

    @Override
    public LogEvent apply(org.apache.logging.log4j.core.LogEvent logEvent) {

        Message message = logEvent.getMessage();
        String formattedMessage = message.getFormattedMessage();
        String level = logEvent.getLevel().toString();
        String loggerName = logEvent.getLoggerName();
        ZonedDateTime timeStamp = ZonedDateTime.ofInstant(Instant.ofEpochMilli(logEvent.getTimeMillis()), ZoneOffset.UTC);
        Map<String, String> diagnosticContext = Collections.unmodifiableMap(logEvent.getContextData().toMap());

        List<Object> arguments = Optional.ofNullable(message.getParameters())
                .map(Arrays::asList)
                .map(Collections::unmodifiableList)
                .orElseGet(Collections::emptyList);

        Throwable throwable = Optional.ofNullable(logEvent.getThrown())
                .orElse(null);

        return new LogEvent(
                message,
                formattedMessage,
                level,
                loggerName,
                timeStamp,
                arguments,
                throwable,
                diagnosticContext
        );
    }

    public static LogEventMapper getInstance() {
        return INSTANCE;
    }

}
