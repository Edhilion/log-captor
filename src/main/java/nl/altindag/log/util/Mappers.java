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

package nl.altindag.log.util;

import nl.altindag.log.mapper.LogEventMapper;
import nl.altindag.log.model.LogEvent;

import java.util.function.Function;

/**
 * @author Hakan Altindag - Sébastien Vicard
 */
public final class Mappers {

    private Mappers() {}

    public static Function<org.apache.logging.log4j.core.LogEvent, LogEvent> toLogEvent() {
        return LogEventMapper.getInstance();
    }

}
