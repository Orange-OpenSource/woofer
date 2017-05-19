# Woofer demo application

This projects is a showcase web app that demonstrates both **logging best practices** (based on our 
[orange-mathoms-logging](https://github.com/Orange-OpenSource/orange-mathoms-logging) library), as well as providing a 
quite advanced **Spring Boot-based microservices project sample**.


## License

This code is under [Apache-2.0 License](LICENSE.txt)


## What is Woofer ?

Woofer is meant to be a basic clone of [Tweeter](https://twitter.com/).

It allows anyone to create an account, post short messages (aka *woofs*), subscribe and unsubscribe to other
users woofs...


## Microservices

Woofer heavily relies on [Spring Boot](https://projects.spring.io/spring-boot/), and [Spring Cloud](http://projects.spring.io/spring-cloud/) to 
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


## Featured logging tools

Woofer is primarily a showcase web app that demonstrates logging best practices.

It uses the following libraries:

* [SLF4J](https://www.slf4j.org/) and [Logback](https://logback.qos.ch/) as the logging facade and implementation (the default with Spring Boot).
* [logstash-logback-encoder](https://github.com/logstash/logstash-logback-encoder), a great Logback add-on that enables 
shipping logs directly to Logstash in JSON format over TCP or UDP.
* [Spring Cloud Sleuth](https://cloud.spring.io/spring-cloud-sleuth/) for distributed tracing & logs correlation.
* our [orange-mathoms-logging](https://github.com/Orange-OpenSource/orange-mathoms-logging) library that brings additional useful stuff.


## Featured Spring Boot modules

Woofer also makes use of many powerful Spring modules and features:

* [Spring Data JPA](http://projects.spring.io/spring-data-jpa/) implements our Object-Relational Mapping (ORM),
* [Spring Data Rest](http://projects.spring.io/spring-data-rest/) to build hypermedia-driven REST web services on top of Spring Data repositories,
* [Feign](https://github.com/OpenFeign/feign) to implement declarative REST clients (part of [Spring Cloud Netflix](https://cloud.spring.io/spring-cloud-netflix/))
* Eureka server & client (part of [Spring Cloud Netflix](https://cloud.spring.io/spring-cloud-netflix/)); [More info](https://spring.io/blog/2015/01/20/microservice-registration-and-discovery-with-spring-cloud-and-netflix-s-eureka).
* Ribbon to implement client side load balancer (part of [Spring Cloud Netflix](https://cloud.spring.io/spring-cloud-netflix/))
* [Asciidoctor](http://asciidoctor.org/) to generate `woofer-backend` reference documentation


## Run the project on your computer

You may clone the project, then as any Spring Boot application, each Woofer service may be launched on your 
computer with:

```
mvn spring-boot:run
```

Then you'll be able to access Woofer's pages on [localhost](http://localhost:8080/)...

> You may either [create your own account](http://localhost:8080/signup), or use any of the [existing accounts](http://localhost:8080/users)
> (each one having the `user` password).


## Try it !

Notice that even if your app is not in microservices architecture, the Woofer demo has plenty of great stuff to discover !
