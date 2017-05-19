# Logging in Java

This article has been written by Orange Software Expert members
(see [authors](#about-the-authors)), and is targeting Java (and JVM-based languages)
developers, and more specifically server-side developers (JEE or else).

It first gives a clear answer to "which logging library should I use", then gives
several technical tips and tools to take the most benefit from your application logs.

Our only goal is to share and spread best practices about logs.

---

## Which logging library(ies)?

In Java, there are lots of logging libraries:

| Library | Comment | Our advise
| ------- | ------- | ----------
[utils.logging (aka JUL)](https://docs.oracle.com/javase/7/docs/api/java/util/logging/package-summary.html) | _Functionally and technically, it lags behind competitors_ |  <span style="color: red;">do not use it</span>
[Log4J 1](https://logging.apache.org/log4j/1.2/download.html) |  _Too old, not maintained anymore_ | <span style="color: red;">do not use it</span>
[Apache commons-logging](https://commons.apache.org/proper/commons-logging/) | _Too old, not maintained anymore_ | <span style="color: red;">do not use it</span>
[Log4J 2](http://logging.apache.org/log4j/2.x/) | _Seems okay, but clearly not very popular_ | <span style="color: #F50;">no concrete feedback for now</span>
[SLF4J](http://www.slf4j.org/) + [logback](http://logback.qos.ch/) | _See reasons below_ | <span style="color: darkgreen;">this is our choice !</span>

### Two libraries?!

[SLF4J](http://www.slf4j.org/) is a **logging facade** API (abstract interface):
this is the logging API you will use in your code.
This component ensures the interoperability with all your dependencies (that might rely on other logging libraries).

[Logback](http://logback.qos.ch/) is the **backend implementation**: this is the underlying library that implements 
the facade and processes your logs (pushes them to stdout, flat file, with rolling policies, forward them to a 
centralized system, work asynchronously, ...).
It shall never be used directly from your application code. By the way [SLF4J](http://www.slf4j.org/) is also able to 
work with [Log4J 2](http://logging.apache.org/log4j/2.x/) as the backend implementation (instead of 
[Logback](http://logback.qos.ch/)).

All logback [documentation here](http://logback.qos.ch/manual/index.html).

### Why this choice?

1. **Interoperability**: [SLF4J](http://www.slf4j.org/) has bridges for most other
  libraries (JUL, commons-logging, Log4J, ...)
2. **Reliability**: both projects are still actively developed, tested, documented
  and supported, with a large user community
3. **Performance**: [SLF4J](http://www.slf4j.org/) + [logbackhttp://logback.qos.ch/)
  support a feature called « parameterized logging », that significantly boosts
  logging performance for disabled logging statement, by not concatenating
  logging messages (which IS costly)
4. **DevOps**: logback supports externalized configuration, and automatic reload
  (changing logging levels without restarting your application)
5. ... and others; see: http://logback.qos.ch/reasonsToSwitch.html

By the way, this choice is now the most popular choice in Java development, and
is even the default setup when using full-stack web development frameworks such
as [Dropwizard](http://www.dropwizard.io/) or [Spring Boot](http://projects.spring.io/spring-boot/).

Also notice that this choice is perfectly compatible with 
[12 factor apps recommendations](http://12factor.net/logs) about logs; effective
logs routing will be implemented with logback configuration (apart from application logic).

For more info about the great history of logging libraries in Java, please read:

* [The Java Logging Mess](https://www.javacodegeeks.com/2011/09/java-logging-mess.html)
* [Benchmarking Java Logging Frameworks](https://www.loggly.com/blog/benchmarking-java-logging-frameworks/)
* [Comparison of Java Logging Frameworks and Libraries](http://www.java-logging.com/comparison/)

Note: your application is using [Log4J 1](https://logging.apache.org/log4j/1.2/download.html) ?

* Check out this comprehensive article that explains in details [Migration from log4j](https://logback.qos.ch/manual/migrationFromLog4j.html).
* Nevertheless, you should take the opportunity of this migration to use [great advantages of Logback features](http://logback.qos.ch/reasonsToSwitch.html).

---

## Top recommendations

### Never implement your own logging abstraction layer

Directly use the provided logging facade API: [SLF4J](http://www.slf4j.org/).

### Be aware of the parameters evaluation constraint

Logging as follows:

```java
logger.debug("Entry number: " + i + " is " + String.valueOf(entry[i]));
```

incurs the cost of constructing the message parameter, that is converting both
integer `i` and `entry[i]` to a `String`, and concatenating intermediate strings.
This is regardless of whether the message will be logged or not.

SLF4J introduces a better pattern for addressing this issue:

```java
Object entry = new SomeObject();
logger.debug("The entry is {}.", entry);
```

Only after evaluating whether to log or not, and only if the decision is positive,
will the logger implementation format the message and replace the `{}` pair with
the string value of entry.

In other words, this form does not incur the cost of parameter construction when
the log statement is disabled.

**Caution**: in some cases, even this pattern may cause parameters evaluation.

Example:

```java
logger.debug("My JSON structure is {}.", data.toJson());
```

will always cause the `toJson()` method to be evaluated...

In such a case, the recommended pattern would be to protect the logging
instruction with level checking:

```java
if(logger.isDebugEnabled()) {
  logger.debug("My JSON structure is {}.", data.toJson());
}
```

For more info, please read the [parameterized logging](http://logback.qos.ch/manual/architecture.html#parametrized) documentation.


### Take care of the performance cost of log caller context

Read carefully the [Conversion Word](http://logback.qos.ch/manual/layouts.html#conversionWord) from layouts documentation,
and avoid directives with the warning *"Generating the xxx information is not particularly fast. Thus, its use should be avoided unless execution speed is not an issue."* 
(i.e. [class](http://logback.qos.ch/manual/layouts.html#class), [file](http://logback.qos.ch/manual/layouts.html#file), 
[line](http://logback.qos.ch/manual/layouts.html#line), [method](http://logback.qos.ch/manual/layouts.html#method)).

The same recommendation applies to any other kind of logback outputs, for example
avoid the use of ["callerData" LoggingEvents provider](https://github.com/logstash/logstash-logback-encoder#providers-for-loggingevents) in the `logstash-logback-encoder` module.

---

## Enrich your logs!

### Why

Quoting splunk "[logging best practices](http://dev.splunk.com/view/logging-best-practices/SP-CAAADP6)":

> Unique identifiers such as transaction IDs, user IDs, session IDs, request IDs are tremendously helpful when debugging, and even more helpful when you are gathering analytics.
> Unique IDs can point you to the exact transaction, [isolate logs from a single request or logs related to a given user]. Without them, you might only have a time range to use.
> When possible, carry these IDs through multiple touch points and avoid changing the format of these IDs between modules. That way, you can track transactions through the system and follow them across machines, networks, and services.

With SLF4J, the standard way of enriching your logging context with IDs is using
[Mapped Diagnostic Context](http://logback.qos.ch/manual/mdc.html).

### Tag logs with (unique) request IDs

For instance, wouldn't it be cool to mark every log produced during the processing
of a request with a (unique) request ID ? Then, with a tool such as Kibana, it would
become so easy to isolate logs from a single request (ElasticSearch query 
"requestId: 123456789")...

Technically there are several options to implement this:

* With [Spring Cloud Sleuth](https://cloud.spring.io/spring-cloud-sleuth/),
* [A simpler implementation](https://github.com/Orange-OpenSource/orange-mathoms-logging#requestIds) with our `orange-mathoms-logging` library.


### Tag logs with user IDs

Similarly, it's also very helpful to mark every log produced during the processing of an 
authenticated request with the user ID.

With Kibana you will then be able to filter out in one click all logs related to
a single user !

**Warning**: for security/privacy reasons, it is highly recommended that the
user ID is a technical ID and not a personal information (such as login or email address).

You'll find a [simple implementation](https://github.com/Orange-OpenSource/orange-mathoms-logging#userIds) in our `orange-mathoms-logging` library.


### How to enrich your stack traces with signatures

Warning, this idea is pretty cool...

When your system will be in production, you'll have issues. And issues are (generally) ERROR logs with stack traces. But...

* How do you track the error from the client (UI and/or API) to your logs ?
* How will you compare stack traces ?
* Count their frequency ?
* Find quickly when a problem occurred for the first time ?
* Make sure a problem has been fixed for good ?
* ...

... the idea we propose is to generate a short, unique ID that identifies your stack trace.

Thus, the same error occurring twice will have the same ID, allowing you to count, compare, track history of this issue.

You could even join this signature to your client error messages for traceability.

Imagine the situation...

> **The user** (_quite upset_): I just had this « Internal error [#B23F6545] occurred while calling catalog » error... your app sucks !  
> **You**: no prob’, I simply type-in this « #B23F6545 » thing in my Kibana and... bing ! I get the exact stack trace, call flow, occurrences count, ... you’ll have a fix in instants !  
> **Audience**: (applause)

... well, guess what ? You'll also find a [simple implementation](https://github.com/Orange-OpenSource/orange-mathoms-logging#stackTraceSign) in our `orange-mathoms-logging` library.


---

## How to ship logs to Logstash?

You’re using ELK in your project ? Lucky you !

With the [logstash-logback-encoder](https://github.com/logstash/logstash-logback-encoder)
library, you’ll be able to:

* produce logs in the native logstash JSON format (no file parsing),
* all your MDC fields are automatically added to the log entry,
* forward logs directly to a remote logstash server (over UDP or TCP), 
  without needing any shipper (such as
  [Filebeat](https://www.elastic.co/products/beats/filebeat), syslog and whatever)
* take benefit of a [LMAX Disruptor RingBuffer](https://lmax-exchange.github.io/disruptor/)
  based appender, with insane performances !!
* ... and lots more

---

## About the authors

* [David Crosson](https://github.com/dacr)
* [David Guyomarch](https://github.com/davidguyomarch)
* [Shahnawaz Khan](https://github.com/shahnawaz-khan)
* [Remy Sanlaville](https://github.com/sanlaville)
* [Pierre Smeyers](https://github.com/pismy)
