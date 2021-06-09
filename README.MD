[![Actions Status](https://github.com/Edhilion/log-captor/workflows/Build/badge.svg)](https://github.com/Edhilion/log-captor/actions)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Edhilion_log-captor&metric=alert_status)](https://sonarcloud.io/dashboard?id=Edhilion_log-captor)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=Edhilion_log-captor&metric=coverage)](https://sonarcloud.io/dashboard?id=Edhilion_log-captor)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=Edhilion_log-captor&metric=reliability_rating)](https://sonarcloud.io/dashboard?id=Edhilion_log-captor)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=Edhilion_log-captor&metric=security_rating)](https://sonarcloud.io/dashboard?id=Edhilion_log-captor)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=Edhilion_log-captor&metric=vulnerabilities)](https://sonarcloud.io/dashboard?id=Edhilion_log-captor)
[![Apache2 license](https://img.shields.io/badge/license-Apache2.0-blue.svg)](https://github.com/Edhilion/log-captor/blob/master/LICENSE)
[![Join the chat at https://gitter.im/edhilion/logcaptor](https://badges.gitter.im/edhilion/logcaptor.svg)](https://gitter.im/edhilion/logcaptor?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

[![SonarCloud](https://sonarcloud.io/images/project_badges/sonarcloud-white.svg)](https://sonarcloud.io/dashboard?id=Edhilion_log-captor)

# LogCaptor (log4j2 version) [![Tweet](https://img.shields.io/twitter/url/http/shields.io.svg?style=social)](https://twitter.com/intent/tweet?text=With%20LogCaptor%20it%20is%20now%20very%20easy%20to%20captor%20and%20test%20your%20log%20message&url=https://github.com/Edhilion/log-captor/&via=edhilion1&hashtags=logging,testing,log4j,slf4j,log4j2,jul,lombok,developer,java,scala,kotlin,logcaptor)

# Install library with:
### Install with [maven](https://mvnrepository.com/artifact/io.github.edhilion/logcaptor)
_Not available for now_
```xml
<dependency>
    <groupId>io.github.edhilion</groupId>
    <artifactId>logcaptor</artifactId>
    <version>2.6.1</version>
    <scope>test</scope>
</dependency>
```
### Install with Gradle
_Not available for now_
```groovy
testImplementation 'io.github.edhilion:logcaptor:2.6.1'
```
### Install with Scala SBT
_Not available for now_
```
libraryDependencies += "io.github.edhilion" % "logcaptor" % "2.6.1" % Test
```
### Install with Apache Ivy
_Not available for now_
```xml
<dependency org="io.github.edhilion" name="logcaptor" rev="2.6.1" />
```

## Table of contents
1. [Introduction](#introduction)
   - [Advantages](#advantages)
   - [Supported Java versions](#supported-java-versions)
   - [Tested Logging libraries](#tested-logging-libraries)
2. [Usage](#usage)
   - [Capture logs](#capture-logs)
   - [Reuse LogCaptor for multiple tests](#initialize-logcaptor-once-and-reuse-it-during-multiple-tests-with-clearlogs-method-within-the-aftereach-method)
   - [Capture logs for enabled logs only](#class-which-will-log-events-if-specific-log-level-has-been-set)
   - [Capture exceptions within logs](#class-which-will-also-log-an-exception)
   - [Capture Managed Diagnostic Context (MDC)](#capture-managed-diagnostic-context-mdc)  
   - [Disable any logs for specific class](#disable-any-logs-for-a-specific-class)   
   - [Disable all logs](#disable-all-logs)
3. [Known issues](#known-issues)
   - [Using Log Captor alongside with other logging libraries](#using-log-captor-alongside-with-other-logging-libraries)
4. [Contributing](#contributing)
5. [License](#license)
   

# Introduction
This version is a fork of Logcaptor by Hakky54 (https://github.com/Hakky54/log-captor).

LogCaptor is a library which will enable you to easily capture logging entries for unit testing purposes.
This version of the library uses Log4j2 as the logging framework, when the original https://github.com/Hakky54/log-captor version uses logback.

For now, Hakky54's version and this version are meant to evolve together, so version numbers shows any difference between them. 

### Advantages
- No mocking required
- No custom JUnit extension required
- Plug & play

### Supported Java versions
- Java 8
- Java 11+

### Tested Logging libraries
 - SLFJ4
 - Logback
 - Java Util Logging  
 - Apache Log4j
 - Apache Log4j2  
 - Log4j with Lombok
 - Log4j2 with Lombok
 - SLFJ4 with Lombok
 - Java Util Logging with Lombok

See the unit test [LogCaptorShould](src/test/java/nl/altindag/log/LogCaptorShould.java) for all the scenario's or checkout this project [Java Tutorials](https://github.com/Edhilion/java-tutorials) which contains more isolated examples of the individual logging frameworks
 
# Usage
##### Capture logs
```java
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FooService {

    private static final Logger LOGGER = LogManager.getLogger(FooService.class);

    public void sayHello() {
        LOGGER.info("Keyboard not responding. Press any key to continue...");
        LOGGER.warn("Congratulations, you are pregnant!");
    }

}
```
###### Unit test:
```java
import static org.assertj.core.api.Assertions.assertThat;

import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.Test;

public class FooServiceShould {

    @Test
    public void logInfoAndWarnMessages() {
        String expectedInfoMessage = "Keyboard not responding. Press any key to continue...";
        String expectedWarnMessage = "Congratulations, you are pregnant!";

        LogCaptor logCaptor = LogCaptor.forClass(FooService.class);

        FooService fooService = new FooService();
        fooService.sayHello();

        // Option 1 to assert logging entries
        assertThat(logCaptor.getInfoLogs()).containsExactly(expectedInfoMessage);
        assertThat(logCaptor.getWarnLogs()).containsExactly(expectedWarnMessage);

        // Option 2 to assert logging entries
        assertThat(logCaptor.getLogs())
                .hasSize(2)
                .containsExactly(expectedInfoMessage, expectedWarnMessage);
    }
}
```

##### Initialize LogCaptor once and reuse it during multiple tests with clearLogs method within the afterEach method:
```java
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;

public class FooServiceShould {

    private static LogCaptor logCaptor;
    private static final String EXPECTED_INFO_MESSAGE = "Keyboard not responding. Press any key to continue...";
    private static final String EXPECTED_WARN_MESSAGE = "Congratulations, you are pregnant!";
    
    @BeforeAll
    public static setupLogCaptor() {
        logCaptor = LogCaptor.forClass(FooService.class);
    }

    @AfterEach
    public void clearLogs() {
        logCaptor.clearLogs();
    }
    
    @AfterAll
    public static void tearDown() {
        logCaptor.close();
    }

    @Test
    public void logInfoAndWarnMessagesAndGetWithEnum() {
        FooService service = new FooService();
        service.sayHello();

        assertThat(logCaptor.getInfoLogs()).containsExactly(EXPECTED_INFO_MESSAGE);
        assertThat(logCaptor.getWarnLogs()).containsExactly(EXPECTED_WARN_MESSAGE);

        assertThat(logCaptor.getLogs()).hasSize(2);
    }

    @Test
    public void logInfoAndWarnMessagesAndGetWithString() {
        FooService service = new FooService();
        service.sayHello();

        assertThat(logCaptor.getInfoLogs()).containsExactly(EXPECTED_INFO_MESSAGE);
        assertThat(logCaptor.getWarnLogs()).containsExactly(EXPECTED_WARN_MESSAGE);

        assertThat(logCaptor.getLogs()).hasSize(2);
    }

}
```

##### Class which will log events if specific log level has been set
```java
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FooService {

    private static final Logger LOGGER = LogManager.getLogger(FooService.class);

    public void sayHello() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Keyboard not responding. Press any key to continue...");
        }
        LOGGER.info("Congratulations, you are pregnant!");
    }

}
```
###### Unit test:
```java
import static org.assertj.core.api.Assertions.assertThat;

import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.Test;

public class FooServiceShould {

    @Test
    public void logInfoAndWarnMessages() {
        String expectedInfoMessage = "Congratulations, you are pregnant!";
        String expectedDebugMessage = "Keyboard not responding. Press any key to continue...";

        LogCaptor logCaptor = LogCaptor.forClass(FooService.class);
        logCaptor.setLogLevelToInfo();

        FooService fooService = new FooService();
        fooService.sayHello();

        assertThat(logCaptor.getInfoLogs()).containsExactly(expectedInfoMessage);
        assertThat(logCaptor.getDebugLogs())
            .doesNotContain(expectedDebugMessage)
            .isEmpty();
    }
}
```

##### Class which will also log an exception
```java
import nl.altindag.log.service.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class FooService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZooService.class);

    @Override
    public void sayHello() {
        try {
            tryToSpeak();
        } catch (IOException e) {
            LOGGER.error("Caught unexpected exception", e);
        }
    }

    private void tryToSpeak() throws IOException {
        throw new IOException("KABOOM!");
    }
}
```
###### Unit test:
```java
import static org.assertj.core.api.Assertions.assertThat;

import nl.altindag.log.LogCaptor;
import nl.altindag.log.model.LogEvent;
import org.junit.jupiter.api.Test;

public class FooServiceShould {

    @Test
    void captureLoggingEventsContainingException() {
        LogCaptor logCaptor = LogCaptor.forClass(ZooService.class);

        FooService service = new FooService();
        service.sayHello();

        List<LogEvent> logEvents = logCaptor.getLogEvents();
        assertThat(logEvents).hasSize(1);

        LogEvent logEvent = logEvents.get(0);
        assertThat(logEvent.getMessage()).isEqualTo("Caught unexpected exception");
        assertThat(logEvent.getLevel()).isEqualTo("ERROR");
        assertThat(logEvent.getThrowable()).isPresent();

        assertThat(logEvent.getThrowable().get())
                .hasMessage("KABOOM!")
                .isInstanceOf(IOException.class);
    }
}
```
##### Capture Managed Diagnostic Context (MDC)
```java
import nl.altindag.log.service.LogMessage;
import nl.altindag.log.service.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class FooService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceWithSlf4jAndMdcHeaders.class);

    public void sayHello() {
        try {
            MDC.put("my-mdc-key", "my-mdc-value");
            LOGGER.info(LogMessage.INFO.getMessage());
        } finally {
            MDC.clear();
        }

        LOGGER.info("Hello there!");
    }

}
```
###### Unit test:
```java
import static org.assertj.core.api.Assertions.assertThat;

import nl.altindag.log.LogCaptor;
import nl.altindag.log.model.LogEvent;
import org.junit.jupiter.api.Test;

public class FooServiceShould {

   @Test
   void captureLoggingEventsContainingException() {
      LogCaptor logCaptor = LogCaptor.forClass(FooService.class);

      FooService service = new FooService();
      service.sayHello();

      List<LogEvent> logEvents = logCaptor.getLogEvents();

      assertThat(logEvents).hasSize(2);

      assertThat(logEvents.get(0).getDiagnosticContext())
              .hasSize(1)
              .extractingByKey("my-mdc-key")
              .isEqualTo("my-mdc-value");

      assertThat(logEvents.get(1).getDiagnosticContext()).isEmpty();
   }
}
```

##### Disable any logs for a specific class
In some use cases a unit test can generate too many logs by another class. This could be annoying as it will cause noise in your build logs. LogCaptor can disable those log messages with the following snippet:

```java
import static org.assertj.core.api.Assertions.assertThat;

import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class FooServiceShould {

    private static LogCaptor logCaptorForSomeOtherService = LogCaptor.forClass(SomeService.class);

    @BeforeAll
    static void disableLogs() {
        logCaptorForSomeOtherService.disableLogs();
    }

    @AfterAll
    static void resetLogLevel() {
        logCaptorForSomeOtherService.resetLogLevel();
    }

    @Test
    void captureLoggingEventsContainingException() {
        String expectedInfoMessage = "Keyboard not responding. Press any key to continue...";
        String expectedWarnMessage = "Congratulations, you are pregnant!";

        LogCaptor logCaptor = LogCaptor.forClass(FooService.class);

       FooService service = new FooService();
        service.sayHello();

        assertThat(logCaptor.getLogs())
                .hasSize(2)
                .containsExactly(expectedInfoMessage, expectedWarnMessage);
    }
}
```

##### Disable all logs
Add `log4j2-test.xml` to your test resources with the following content:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="WARN">
   <Appenders>
      <Null name="nop_appender" />
   </Appenders>
   <Loggers>
      <Root level="trace">
         <AppenderRef ref="nop_appender"/>
      </Root>
   </Loggers>
</Configuration>
```

# Known issues
## Using Log Captor alongside with other logging libraries
When building your maven or gradle project it can complain that you are using multiple SLF4J implementations. Log Captor is using logback as SLF4J implementation and SLF4J doesn't allow you to use multiple implementations, therefore you need to explicitly specify which to use during which build phase.
You can fix that by excluding your main logging framework during the unit/integration test phase. Below is an example for Maven Failsafe and Maven Surefire:
```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-surefire-plugin</artifactId>
            <configuration>
                <classpathDependencyExcludes>
                   <classpathDependencyExclude>ch.qos.logback:logback-classic</classpathDependencyExclude>
                </classpathDependencyExcludes>
            </configuration>
        </plugin>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-failsafe-plugin</artifactId>
            <configuration>
                <classpathDependencyExcludes>
                   <classpathDependencyExclude>ch.qos.logback:logback-classic</classpathDependencyExclude>
                </classpathDependencyExcludes>
            </configuration>
        </plugin>
    </plugins>
</build>
``` 
And for gradle:
```groovy
configurations {
    testImplementation {
        exclude group: 'ch.qos.logback', module: 'logback-classic'
    }
}
```

# Contributing

There are plenty of ways to contribute to this project:

* Give it a star
* Share it with a [![Tweet](https://img.shields.io/twitter/url/http/shields.io.svg?style=social)](https://twitter.com/intent/tweet?text=With%20LogCaptor%20it%20is%20now%20very%20easy%20to%20captor%20and%20test%20your%20log%20message&url=https://github.com/Edhilion/log-captor/&via=edhilion1&hashtags=logging,testing,log4j,slf4j,log4j2,jul,lombok,developer,java,scala,kotlin,logcaptor)
* Join the [Gitter room](https://gitter.im/edhilion/logcaptor) and leave a feedback or help with answering users questions
* Contribute to https://github.com/Hakky54/log-captor and let me know the evolution

## License
[![FOSSA Status](https://app.fossa.io/api/projects/git%2Bgithub.com%2FEdhilion%2Flog-captor.svg?type=large)](https://app.fossa.io/projects/git%2Bgithub.com%2FEdhilion%2Flog-captor?ref=badge_large)
