import ch.qos.logback.classic.AsyncAppender
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.classic.net.SyslogAppender
import ch.qos.logback.core.ConsoleAppender
import ch.qos.logback.core.rolling.RollingFileAppender
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy
import ch.qos.logback.core.status.OnConsoleStatusListener

import static ch.qos.logback.classic.Level.ERROR
import static ch.qos.logback.classic.Level.INFO

statusListener(OnConsoleStatusListener)
scan("60 seconds")

def CURRENT_ENV = System.getProperty("currentEnv") ?: "local"
//Default path for all environments except localhost (dev)
def logDirectory = System.getenv("APP_LOGS_DIR")
def sysLogHostConfig = "localhost"
def sysLogFacilityConfig = "LOCAL5"

printf "Configuring for environment %s \\n", CURRENT_ENV

if(logDirectory == null) {
    logDirectory = "log"
}

//Common config for access and application logging
def accessLogPattern = "%date{ISO8601} %-5level %thread - %msg %X{log.ctx}%n"
def applicationLogPattern = "%date{ISO8601} %-5level %thread [%logger{5}] - %msg %X{log.ctx}%n"
def accessLogFile = (CURRENT_ENV.equals("local")) ? "target/service-access.log" : "${logDirectory}/service-access.log"
def applicationLogFile = (CURRENT_ENV.equals("local")) ? "target/service-application.log" : "${logDirectory}/service-application.log"
def accessLogFileNamePattern = (CURRENT_ENV.equals("local")) ? "target/service-access.%d{yyyy-MM-dd}.log.gz" : "${logDirectory}/service-access.%d{yyyy-MM-dd}.log.gz"
def applicationLogFileNamePattern = (CURRENT_ENV.equals("local")) ? "target/service-application.%d{yyyy-MM-dd}.log.gz" : "${logDirectory}/service-application.%d{yyyy-MM-dd}.log.gz"

//Common loggers
appender("FILE", RollingFileAppender) {
    file = applicationLogFile
    rollingPolicy(TimeBasedRollingPolicy) {
        fileNamePattern = applicationLogFileNamePattern
        maxHistory = 14
    }
    encoder(PatternLayoutEncoder) {
        pattern = applicationLogPattern
    }
}

//Access log configuration
appender("ACCESS-LOG", RollingFileAppender) {
    file = accessLogFile
    rollingPolicy(TimeBasedRollingPolicy) {
        fileNamePattern = accessLogFileNamePattern
        maxHistory = 7
    }
    encoder(PatternLayoutEncoder) {
        pattern = accessLogPattern
    }
}

appender("ASYNC-ACCESS-LOG", AsyncAppender) {

    maxFlushTime = 5000

    //Guesstimate increase in queue size for the moment. Want to prevent the event loop blocking under heavy load should
    //the underlying AsyncAppender blocking queue be full
    //See http://logback.qos.ch/manual/appenders.html#AsyncAppender
    queueSize = 1024
    appenderRef(["ACCESS-LOG"])
}

//    Note: the last arg is additivity=false. This means any lines from the AccessLogBodyEndHandler will only be logged
//    to the appenders specified for this logger (e.g. ASYNC-ACCESS-LOG) and NOT in any other log file.
logger("io.helixservice.feature.accesslog.AccessLogHandler", INFO, ["ASYNC-ACCESS-LOG"], false)
logger("io.helixservice.feature.configuration", INFO)
logger("io.helixservice.core.server", INFO)
logger("com.helix", INFO)
logger("AccessLog", INFO)
//logger("io.helixservice.helix.feature.jpa", INFO)
//logger("io.helixservice.helix.feature.worker", INFO)

// Send C3P0 logs to SlF4J
System.setProperty("com.mchange.v2.log.MLog", "com.mchange.v2.log.slf4j.Slf4jMLog");

//Environment specific config
if(CURRENT_ENV.equals("local")) {
    println("Configuring additional items for local")

    appender("STDOUT", ConsoleAppender) {
        encoder(PatternLayoutEncoder) {
            pattern = "%d{HH:mm:ss.SSS} [%10.10thread] %-5level %20.20logger - %msg %X{log.ctx}%n"
        }
    }

    root(ERROR, ["STDOUT", "FILE"])
} else {
    println("Configuring additional items for ${CURRENT_ENV}")

    appender("SYSLOG", SyslogAppender) {
        syslogHost = sysLogHostConfig
        facility = sysLogFacilityConfig
        suffixPattern = "%date{ISO8601} %-5level %thread [%logger{5}] - %msg %X{log.ctx}%n"
    }
    appender("ASYNC-SYSLOG", AsyncAppender) {
        maxFlushTime = 5000
        appenderRef(["SYSLOG"])
    }

    appender("ASYNC-FILE", AsyncAppender) {
        maxFlushTime = 5000
        appenderRef(["FILE"])
    }

    root(INFO, ["ASYNC-SYSLOG", "ASYNC-FILE"])
}
