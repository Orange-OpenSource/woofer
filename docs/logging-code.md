# A deeper look into the code

This chapter details all specific stuff coded or configured in Woofer related to logging.

---

## Featured logging tools

Woofer uses the following libraries:

* [SLF4J](https://www.slf4j.org/) and [Logback](https://logback.qos.ch/) as the logging facade and implementation (the default with Spring Boot).
* [logstash-logback-encoder](https://github.com/logstash/logstash-logback-encoder), a great Logback add-on that enables 
shipping logs directly to Logstash in JSON format over TCP or UDP.
* [Spring Cloud Sleuth](https://cloud.spring.io/spring-cloud-sleuth/) for distributed tracing & logs correlation.
* our [orange-mathoms-logging](https://github.com/Orange-OpenSource/orange-mathoms-logging) library that brings additional useful stuff.

---

## Distributed tracing & logs correlation

Especially in a microservices architecture, a request may go through several tiers (apache, j2ee server, bdd...) and it's not a trivial task to
understand and follow the callflow and spot the root cause of an error.

That's why it is so important to have a way of correlating logs produced from different tiers, but related to the same _treatment_.

This is demonstrated in our [Incident Analysis](incident-analysis) section.

Distributed tracing feature in Woofer is brought by [Spring Cloud Sleuth](https://cloud.spring.io/spring-cloud-sleuth/).

It only requires to be installed (follow quick start), and - _here we go_:

1. logs are automatically enriched with tracing context:
    * `X-B3-TraceId`: unique ID for an overall _treatment_ (across several services),
    * `X-B3-SpanId`: unique ID for a basic unit of work (one thread on one server),
    * `X-Span-Export`: whether or not the span is exported in the current span.
2. tracing context is automatically propagated upon calling other services (through standard HTTP headers).

---

## Enrich logs with user IDs

Logs from the `woofer-webfront` component are enriched with a `userId` field, that is the user's login.

This is done using the `PrincipalFilter` from the `orange-mathoms-logging` library ([see doc](https://github.com/Orange-OpenSource/orange-mathoms-logging#userIds)).

---

## Enrich logs with session IDs

Logs from the `woofer-webfront` component are enriched with a `sessionId` field, that is the current user's session ID.

This is done using the `SessionIdFilter` from the `orange-mathoms-logging` library ([see doc](https://github.com/Orange-OpenSource/orange-mathoms-logging#sessionIds)).

---

## Enrich stack traces with unique signatures

The idea is to generate a short, unique ID that identifies your stack trace.

It is an easy way to track the error from the client (UI and/or API) to your logs, count their frequency, make sure a problem has been fixed for good...

This is done using the `CustomThrowableConverterWithHash` & `StackHashJsonProvider` components from the `orange-mathoms-logging` library ([see doc](https://github.com/Orange-OpenSource/orange-mathoms-logging#stackTraceSign)).

---

## Error handling

Error handling in a web application is undoubtedly a complex task. It covers several topics:

<dl>
    <dt><strong>error mapping</strong></dt>
    <dd>
        How Java errors should be mapped to HTTP errors? Which status code? Which (human readable) message?
        Generally speaking, you'll have to deal both with your own Java exceptions, and also errors from the underlying framework (Spring or else).
    </dd>
    <dl>
    <dt><strong>content negotiation</strong></dt>
    <dd>
        When an error occurs, depending on the requesting client, you may have to render a human readable web page, 
        a JSON response, an XML response or else.
    </dd>
    <dl>
    <dt><strong>integrate to your framework</strong></dt>
    <dd>
        The way you will implement this logic will heavily depend on the framework you're using.
    </dd>
</dl>


### Error mapping

In Woofer, error mapping is handled by the [ErrorTranslator](https://github.com/Orange-OpenSource/woofer/blob/master/woofer-commons/src/main/java/com/orange/oswe/demo/woofer/commons/error/ErrorTranslator.java) class.

It is in charge of translating any internal Java error into a [DisplayableError](https://github.com/Orange-OpenSource/woofer/blob/master/woofer-commons/src/main/java/com/orange/oswe/demo/woofer/commons/error/DisplayableError.java), a structure with:

* an error code (compatible with [common Orange Partner error codes](https://developer.orange.com/apis/authentication-fr/api-reference#errors))),
* an HTTP status,
* a human friendly message.

The [ErrorTranslator](https://github.com/Orange-OpenSource/woofer/blob/master/woofer-commons/src/main/java/com/orange/oswe/demo/woofer/commons/error/ErrorTranslator.java) manages most of internal Spring exceptions: you may perfectly reuse it in your own projects.


### The error handler

In Woofer, the error handling is implemented by the [WooferErrorController](https://github.com/Orange-OpenSource/woofer/blob/master/woofer-commons/src/main/java/com/orange/oswe/demo/woofer/commons/error/WooferErrorController.java) controller.

As a `org.springframework.boot.autoconfigure.web.ErrorController` implementation, it is automatically installed by Spring as the main error handler.

It manages **content negotiation** by defining several `@RequestMapping` handling different content types. I.e. handling an 
error upon a web browser navigation will display a user-readable error page, while handling an error occurring in a 
JSON/REST API will return a JSON error description.

It also behaves slightly differently if the error is a client error or an internal (server) error.

A client error (HTTP `4XX`) is supposed to be due to a wrong usage of the API or application. The error handler 
simply displays some hints about the error (you may try this by navigating on this [link with missing parameter](http://localhost:8080/misc/err/missingParam)
of [path that does not exist](http://localhost:8080/misc/err/noSuchUri)).

Any server error (HTTP `5XX`) - on the contrary - probably need to raise an alert, get analyzed and fixed as fast as possible.

They might be due - for instance - to a bloody `NullPointerException` or any technical stuff going wrong in your code.

Server errors display an error message that includes a generated unique ID for traceability purpose.

The message will look like this:

```
Internal error [#d7506d00-99f2c6eb90682] occurred in request 'GET /misc/err/500'
```

The stack error unique ID can be used to trace and analyze the error in Kibana.

---

## Shipping logs directly to Logstash

Using the **logstash** [profile](http://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-profiles.html),
logback is configured to ship directly application and access logs to [Logstash](https://www.elastic.co/products/logstash) over TCP,
using the amazing [logstash-logback-encoder](https://github.com/logstash/logstash-logback-encoder) library.

### Configuration for Java logs

Logback configuration for Java logs also uses `CustomThrowableConverterWithHash` & `StackHashJsonProvider` components
from the `orange-mathoms-logging` library ([see doc](https://github.com/Orange-OpenSource/orange-mathoms-logging#stackTraceSign))
to enrich stack traces with unique signatures.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!-- application logging configuration to ship logs directly to Logstash -->
<configuration debug="true" scan="true" scanPeriod="30 seconds">
  <!-- retrieve some conf from Spring -->
  <springProperty name="collector_host" source="custom.logging.collector.host"/>
  <springProperty name="collector_port" source="custom.logging.collector.port"/>
  <springProperty name="appname" source="spring.application.name"/>
  <springProperty name="project_key" source="custom.logging.project_key"/>

  <appender name="TCP" class="net.logstash.logback.appender.LogstashTcpSocketAppender">
    <!-- remote Logstash server -->
    <remoteHost>${collector_host}</remoteHost>
    <port>${collector_port}</port>
    <encoder class="net.logstash.logback.encoder.LogstashEncoder">
      <!-- computes and adds a 'stack_hash' field on errors -->
      <provider class="com.orange.experts.utils.logging.logback.StackHashJsonProvider"/>
      <!-- enriches the stack trace with unique hash -->
      <throwableConverter class="com.orange.experts.utils.logging.logback.CustomThrowableConverterWithHash" />
      <customFields>{"@project":"${project_key}","@app":"${appname}","@type":"java"}</customFields>
    </encoder>
  </appender>
  
  <logger name="com.orange" level="DEBUG" />
  
  <root level="INFO">
    <appender-ref ref="TCP" />
  </root>
</configuration>
```

NOTE: The [Logstash](https://www.elastic.co/products/logstash) server address is configured with non-standard Spring configuration `custom.logging.collector.host` and `custom.logging.collector.port`.

With this configuration, a single Java log in Elasticsearch will look like this:

```json
{
  "@version": 1,
  "@timestamp": "2017-03-17T14:21:25.643Z",
  "host": "10.100.0.216",
  "port": 46070,
  "HOSTNAME": "oswewooffront",
  "@app": "woofer-webfront",
  "@type": "java",
  "logger_name": "com.orange.oswe.demo.woofer.commons.error.WooferErrorController",
  "level": "ERROR",
  "level_value": 40000,
  "message": "Internal error [#fe8ad9ce-317d85359a8531] occurred in request 'POST /woofs'",
  "thread_name": "http-nio-8080-exec-6",
  "stack_trace": "#fe8ad9ce> com.netflix.hystrix.exception.HystrixRuntimeException: ...",
  "stack_hash": "fe8ad9ce",
  "userId": "bpitt",
  "sessionId": "B3FD051F22C031B5A813B29D32EFF383",
  "X-B3-TraceId": "8210b57467b85195",
  "X-B3-SpanId": "8210b57467b85195",
  "X-Span-Export": "false"
}
```

| Field | Description
| ----- | -----------
|`@version`|standard Elasticsearch field set by Logstash
|`@timestamp`|standard Elasticsearch field set by Logback
|`host`|set by Logstash
|`port`|set by Logstash
|`HOSTNAME`|set by Logback
|`@app`|custom field set by configuration (the name of the origin microservice)
|`@type`|custom field set by configuration (type of the log)
|`logger_name`|set by Logback
|`level`|set by Logback
|`level_value`|set by Logback
|`message`|set by Logback
|`thread_name`|set by Logback
|`stack_trace`|set by Logback, content valuated by the `CustomThrowableConverterWithHash` component
|`stack_hash`|custom field added by the `StackHashJsonProvider` component
|`userId`|custom field added by the `PrincipalFilter` component
|`sessionId`|custom field added by the `SessionIdFilter` component
|`X-B3-TraceId`|set by Spring Cloud Sleuth for logs correlation
|`X-B3-SpanId`|set by Spring Cloud Sleuth for logs correlation
|`X-Span-Export`|set by Spring Cloud Sleuth for logs correlation


### Configuration for access logs (embedded tomcat)

Generally speaking, Logback integration to Spring Boot is quite seamless with respect to Java logs, but it is
far from the case with embedded Tomcat access logs. Spring Boot will probably improve on this aspect in the future.

In Woofer, access logs are configured by the
[TomcatCustomizerForLogback](https://github.com/Orange-OpenSource/woofer/blob/master/woofer-commons/src/main/java/com/orange/oswe/demo/woofer/commons/tomcat/TomcatCustomizerForLogback.java)
class by adding the [LogbackValve](http://logback.qos.ch/apidocs/ch/qos/logback/access/tomcat/LogbackValve.html) to Tomcat's context.

Here is the logback xml configuration for access log using the **logstash**
[profile](http://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-profiles.html):

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!-- access log configuration to ship logs directly to Logstash -->
<configuration debug="true" scan="false" scanPeriod="30 seconds">
  <statusListener class="ch.qos.logback.core.status.OnConsoleStatusListener" />

  <!-- TCP -->
  <appender name="TCP" class="net.logstash.logback.appender.LogstashAccessTcpSocketAppender">
    <!-- remote Logstash server -->
    <remoteHost>${collector_host}</remoteHost>
    <port>${collector_port}</port>
    <encoder class="net.logstash.logback.encoder.AccessEventCompositeJsonEncoder">
      <providers>
        <timestamp/>
        <version/>
        <pattern>
          <pattern>
              {
                "@project":"${project_key}",
                "@app":"${appname}",
                "@type":"http",
                "X-B3-TraceId":"#nullNA{%header{X-B3-TraceId}}",
                "userId":"#nullNA{%requestAttribute{track.userId}}",
                "sessionId":"#nullNA{%sessionID}",
                "req": {
                  "host": "%clientHost",
                  "meth": "%requestMethod",
                  "url": "%requestURL",
                  "uri": "%requestURI",
                  "headers": {
                    "ctype": "#nullNA{%header{Content-Type}}",
                    "ua": "#nullNA{%header{User-Agent}}"
                  }
                },
                "resp": {
                  "status": "#asLong{%statusCode}",
                  "size": "#asLong{%bytesSent}",
                  "headers": {
                    "ctype": "#nullNA{%responseHeader{Content-Type}}"
                  }
                },
                "elapsed": "#asLong{%elapsedTime}"
              }
          </pattern>
        </pattern>
      </providers>
    </encoder>
  </appender>

  <appender-ref ref="TCP" />
</configuration>
```

NOTE: You can see that this configuration allows us to control exactly the JSON structure of an access log sent to [Logstash](https://www.elastic.co/products/elasticsearch[Elasticsearch] via link:https://www.elastic.co/products/logstash). +
Notice that the access logs also use the same custom fields as Java logs (`@app`, `@type`, `@project`, with `@type` equals to `http`).

For more info about Logback & access logs, have a look at:

* [Logback access](http://logback.qos.ch/access.html)
* Logback [PatternLayout](http://logback.qos.ch/xref/ch/qos/logback/access/PatternLayout.html)
* logstash-logback-encoder [AccessEvent patterns](https://github.com/logstash/logstash-logback-encoder#accessevent-patterns)

### Logstash configuration

In order to be able to send directly logs in JSON format to Logstash, you will simply have to setup a Logstash `tcp`
input with `json` codec as follows:

```yaml
input {
  tcp {
    port => 4321
    codec => json
  }
}
```
