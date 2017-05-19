# Woofer: logging in (best) practice(s)

This projects is a showcase web app that demonstrates both **logging best practices** (based on our 
[orange-mathoms-logging](https://github.com/Orange-OpenSource/orange-mathoms-logging) library), as well as providing a 
quite advanced **Spring Boot-based microservices project sample**.

---

## What is Woofer ?

Woofer is meant to be a basic clone of [Tweeter](https://twitter.com/).

It allows anyone to create an account, post short messages (aka **woofs**), subscribe and unsubscribe to other
users woofs...


## Run it on your computer

You may clone the project from GitHub, then as any Spring Boot application, each Woofer service may be launched on your 
computer with:

```
mvn spring-boot:run
```

You'll then be able to access Woofer's pages on [your computer](http://localhost:8080/)...

> You may either [create your own account](http://localhost:8080/signup), or use any of the [existing accounts](http://localhost:8080/users)
> (each one having the `user` password).

## License

Woofer's code is under [Apache-2.0 License](LICENSE.txt)


## Try it !

Notice that even if your app is not in microservices architecture, the Woofer demo has plenty of great stuff to discover !
