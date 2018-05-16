# TL;DR;

This project contains is a proxy for granting access to a micro-service via JWT token authentication (**incomplete code on purpose**): You would need to hook your own logic for checking the token and claims, etc. The program is meant to provide a different perspective for authentication strategies within a container orchestration system.

- It is intended to be deployed as a sidecar [POD]() on [Kubernetes]() as the facing [SERVICE](https://kubernetes.io/docs/concepts/services-networking/service/) of a micro-service app. However, there are no dependencies against Kubernetes.
- The approach is lightweight ([Mitre Proxy Servlet](https://github.com/mitre/HTTP-Proxy-Servlet)) in comparison to alternatives such as [Zuul](https://github.com/Netflix/zuul) and others: No spring or whatsoever.
- Java packages are organized around the [Entity-Control-Boundary](http://www.cs.sjsu.edu/faculty/pearce/modules/patterns/enterprise/ecb/ecb.htm) pattern.

# Motivation

There are multiple ways of approaching authentication in Kubernetes, for those who can't use [istio](https://istio.io/) and/or other complicated stuff:

- Code authentication directly in a micro-service or by delegating to a library
- Authentication at the [INGRESS](https://kubernetes.io/docs/concepts/services-networking/ingress/) level via delegation to an external micro-service (cluster or elsewhere)
- Use a side-car sitting next to the micro-service at the POD level (preferred per next section)

# Benefits of a sidecar

- In comparison to employing a central proxy, each micro-service has its own authentication component (scalability, reduced liability)
- The authentication component is very light (small code base, basic abstractions, no frameworks)
- Minimal complexity around configuration, caching and other related concerns

# Architecture documentation

Please look at the [architecture.md](docs/architecture.md) file in the *docs* folder.

# Requirements

[Apache Maven](https://maven.apache.org/) and JDK 1.8+ are required. This is only tested with JDK 1.8.

# Building

In a console or command prompt, type "`mvn package`". This builds a "fat-jar" of the application with all dependencies embedded.

# Testing

In a console or command prompt, type "`mvn test`".

# TODO

Write JWT authentication logic, add Docker and Kubernetes files.

# License

The license is Apache 2.0, honestly, do whatever you want with the code...
