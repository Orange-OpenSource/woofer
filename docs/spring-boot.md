# Spring & Spring Boot stuff

Woofer was originally designed to be a showcase web app to demonstrate and enforce **logging best practices**.

But actually it is also a quite advanced **Spring Boot-based microservices project sample** that could be used as a
bootstrap to create a microservices application.

This page highlights Spring & Spring Boot modules used in Woofer.

---

## Woofer's microservices

Woofer heavily relies on [Spring Boot](https://projects.spring.io/spring-boot/spring-boot.md), and [Spring Cloud](http://projects.spring.io/spring-cloud/spring-boot.md) to 
implement a microservices architecture.

It involves the following components:

* `service-registry`: the service registry server, basically implemented by Eureka Server.
* `woofer-webfront`: this is the web front-end application, that the end-user will use to connect, read and post woofs.
* `woofer-backend`: this is the backend service, a pure JSON/Rest API service, that is consumed by the `woofer-webfront` and possibly mobile apps (not part of the demo).
* `woofer-notifier`: this is a notification service, that is asynchronously triggered when domain events occur (a user posts a woof, or subscribes/unsubscribes to a mate).
  It is called asynchronously by `woofer-backend`.
* database: a basic [MariaDB](https://mariadb.org/) instance
* logs centralization system: an [ELK](https://www.elastic.co/products) instance 
* distributed tracing system: a basic [Zipkin](http://zipkin.io/) server.

---

## Woofer's profiles

By default, when launching Woofer services locally with no profile, it will 
use a memory database ([h2](http://www.h2database.com/)),
output Java and Http logs to the console.

But Woofer has several Spring Boot [profiles](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-profiles.html)
to adapt to several environments.

| Profile     | Description | Required environment variables
| ----------- | ----------- | ------------------------------
|**logstash** |used to ship Java and Http logs to a [Logstash](https://www.elastic.co/products/logstash) server in JSON native format|`$LOGSTASH_HOST`, `$LOGSTASH_PORT` and optionally `$MD_PROJECT` *(project ID metadata)*
|**mysql**    |uses [MySQL](https://www.mysql.com/) or [MariaDB](https://mariadb.org/) as database |`$H2_DATASOURCE_URL` (default `mem`), `$H2_USER` (default `sa`) and `$H2_PASSWORD` (default none)
|**h2**       |uses [h2](http://www.h2database.com/) as database (mem or file) |`$MYSQL_DATASOURCE_URL` (default on `localhost`), `$MYSQL_USER` (default `root`) and `$MYSQL_PASSWORD` (default none)
|**zipkin**   |activates distributed tracing with a [Zipkin](http://zipkin.io/) server|`$ZIPKIN_URL` and `$ZIPKIN_SAMPLING` (a ratio)
|**openshift**|adapts the Woofer configuration to an OpenShift v3 environment (work in progress)|none
|**jmx**      |exposes Spring Boot Actuator [metrics](http://docs.spring.io/spring-boot/docs/current/reference/html/production-ready-metrics.html) through JMX|none

---

## Spring Data JPA

[Spring Data JPA](http://projects.spring.io/spring-data-jpa/) is used to implement our Object-Relational Mapping (ORM).

It allows creating Data Access Objects (called `Respositories` in Spring terminology) simply by annotations and interfaces.

See:

* [UserRepository.java](https://github.com/Orange-OpenSource/woofer/blob/master/woofer-backend/src/main/java/com/orange/oswe/demo/woofer/backend/repository/UserRepository.java)
* [WoofRepository.java](https://github.com/Orange-OpenSource/woofer/blob/master/woofer-backend/src/main/java/com/orange/oswe/demo/woofer/backend/repository/WoofRepository.java)

---

## Spring Data Rest

[Spring Data Rest](http://projects.spring.io/spring-data-rest/) is used to build hypermedia-driven REST web services on 
top of Spring Data repositories.

Woofer also uses the [Projections](http://docs.spring.io/spring-data/rest/docs/current/reference/html/#projections-excerpts.projections)
feature.

See:

* [User.java](https://github.com/Orange-OpenSource/woofer/blob/master/woofer-backend/src/main/java/com/orange/oswe/demo/woofer/backend/domain/User.java)
* [Woof.java](https://github.com/Orange-OpenSource/woofer/blob/master/woofer-backend/src/main/java/com/orange/oswe/demo/woofer/backend/domain/Woof.java)

---

## Spring Cloud Netflix

[Spring Cloud Netflix](https://cloud.spring.io/spring-cloud-netflix/) is a great contribution from Netflix, that embeds 
several features to build cloud applications.

### Eureka 

Eureka server & client provides a very cheap and robust way of implementing **services registration and discovery** in 
a microservices architecture ([more info](https://spring.io/blog/2015/01/20/microservice-registration-and-discovery-with-spring-cloud-and-netflix-s-eureka)).

Its use is quite straightforward as it only requires adding the right dependency to your `pom.xml`, and a basic
annotation to your Sprint Boot application (either  `@EnableEurekaClient` or `@EnableEurekaServer`).

### Feign

[Feign](https://github.com/OpenFeign/feign) allows implementing **declarative REST clients** (i.e. through interfaces & annotations).

It is used in `woofer-webfront` to implement the `woofer-backend` REST client.

See:

* [UsersClient.java](https://github.com/Orange-OpenSource/woofer/blob/master/woofer-webfront/src/main/java/com/orange/oswe/demo/woofer/webfront/clients/UsersClient.java)
* [WoofsClient.java](https://github.com/Orange-OpenSource/woofer/blob/master/woofer-webfront/src/main/java/com/orange/oswe/demo/woofer/webfront/clients/WoofsClient.java)

### Ribbon

Ribbon provides **client side load balancer**.

Its use is quite straightforward as it only requires adding the right dependency to your `pom.xml`.

---

## Spring Boot Actuator

[Spring Boot Actuator](http://docs.spring.io/spring-boot/docs/current/reference/html/production-ready.html) provides a set of tools
to monitor and manage your application.

Apart from default management endpoints, `woofer-webfront` and `woofer-backend` publish some business metrics to count active
sessions, woofs and subscriptions.

See:

* [HomeController.java](https://github.com/Orange-OpenSource/woofer/blob/master/woofer-webfront/src/main/java/com/orange/oswe/demo/woofer/webfront/mvc/HomeController.java)
* [LoggedInUsersService.java](https://github.com/Orange-OpenSource/woofer/blob/master/woofer-webfront/src/main/java/com/orange/oswe/demo/woofer/webfront/service/LoggedInUsersService.java)

---

## Spring REST Docs

[Spring REST Docs](https://projects.spring.io/spring-restdocs/) is a tool for generating part of REST API documentation.

It is used in `woofer-backend` project to generate the reference API documentation.

See:

* [api-guide.adoc](https://github.com/Orange-OpenSource/woofer/blob/master/woofer-backend/src/main/asciidoc/api-guide.adoc)
* [ApiDocumentation.java](https://github.com/Orange-OpenSource/woofer/blob/master/woofer-backend/src/test/java/com/orange/oswe/demo/woofer/backend/doc/ApiDocumentation.java)

---

## Spring Cloud Sleuth

[Spring Cloud Sleuth](https://cloud.spring.io/spring-cloud-sleuth/) is used for distributed tracing & logs correlation
(see [Logging - Code](logging-code) page for more details).

