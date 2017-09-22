This folder contains common generic logback configuration files:

* logback-base.xml: import Spring Boot defaults and defines a common Stack Trace Exclusion pattern used with (see https://github.com/logstash/logstash-logback-encoder/blob/master/stack-hash.md)
* logback.xml: basic logback conf for Java logs used in dev (console output)
* logback-logstash.xml: logback conf for Java logs that ships logs directly to Logstash
* logback-rtlog.xml: logback conf for Java logs that ships logs directly to RT-Log (a proprietary solution we have at Orange)
* logback-multi.xml: logback conf for Java logs that ships logs to 3 different systems in parallel

