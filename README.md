[![Apache License 2](https://img.shields.io/badge/license-ASF2-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0.txt)
[![Build Status](https://travis-ci.org/codecentric/chaos-monkey-spring-boot.svg?branch=master)](https://travis-ci.org/codecentric/chaos-monkey-spring-boot)
[![codecov](https://codecov.io/gh/codecentric/chaos-monkey-spring-boot/branch/master/graph/badge.svg)](https://codecov.io/gh/codecentric/chaos-monkey-spring-boot)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/de.codecentric/chaos-monkey-spring-boot/badge.svg)](https://maven-badges.herokuapp.com/maven-central/de.codecentric/chaos-monkey-spring-boot/)
# Chaos Monkey for Spring Boot
### inspired by Chaos Engineering at Netflix

<p align="center">
  <img src="images/sb-chaos-monkey-logo.png">
</p>

This project provides a Chaos Monkey for Spring Boot and will try to attack your running [Spring Boot](https://projects.spring.io/spring-boot/) App.

>Chaos Engineering is the discipline of experimenting on a distributed system
in order to build confidence in the system’s capability
to withstand turbulent conditions in production.
> *principlesofchaos.org*

## Content
- [Goal of Chaos Monkey](#goal)
- [Social and communicative](#social)
- [What does Chaos Monkey for Spring Boot do?](#dochaos)
- [How does it work?](#howitworks)
  - [Watcher](#watcher)
  - [Assault](#assaults)
- [Supported Spring Boot versions](#support)
- [Documentation](#docs)
- [Releases](#releases)
- [Snapshots](#snapshots)

## Introduction
Get familiar with the Chaos Monkey for Spring Boot in the following video, <a href="https://goo.gl/r2Tmig" target="_blank">available on YouTube</a>:

<a href="https://goo.gl/r2Tmig" target="_blank"><img src="https://i.ytimg.com/vi/7sQiIR9qCdA/maxresdefault.jpg"
alt="Chaos Monkey for Spring Boot" width="50%" border="10" /></a><br>
**Chaos Monkey for Spring Boot - new features**

<a name="goal"></a>
## What is the goal of Chaos Monkey?
Inspired by [PRINCIPLES OF CHAOS ENGINEERING](http://principlesofchaos.org/) and by my work in distributed systems, with a focus on Spring Boot, I
wanted to test the resulting applications in more detail and especially during operation.

After writing many unit and integration tests, a code coverage from 70% to 80%, this unpleasant feeling remains, how our baby behaves in production?<br><br>
Many questions remain unanswered:
- Will our fallbacks work?
- How does the application behave with network latency?
- What if one of our services breaks down?
- Service Discovery works, but is our Client-Side-Load-Balancing also working?

As you can see, there are many more questions and open topics you have to deal with.

That was my start to take a deep dive into Chaos Engineering and I started this little project to share my thoughts and experience.

<a name="social"></a>
## Be social and communicative!
If you start to implement Chaos Engineering at your company, then you must be a very social and communicative person. Why, because you will get to know many of your colleagues personally in a very short time when your chaos experiments strike.

### Check your resilience
Are your services already resilient and can handle failures? Don´t start a chaos experiment if not!
### Implement active application monitoring
Check your monitoring and check if you can see the overall state of your system. There are many great tools out there to get a pleasant feeling about your entire system.
### Define steady states
Define a metric to check a steady state about your service and of course your entire system. Start small with a service that is not so critical.
### Do not start in production
Of course, you can start in production, but keep in mind...

> The best place on earth is...production!<br>
> *Josh Long*

...so let's keep production as the best place on earth and look for our first experiences on another stage. If all goes well and your company is further on to you, run it in production.
<a name="dochaos"></a>
## What does the Chaos Monkey for Spring Boot do?
Spring Boot Chaos Monkey is a small library (30kb) and you have the following options to integrate the Chaos Monkey into your Spring Boot App.
### Dependency in your POM

```xml
<dependency>
    <groupId>de.codecentric</groupId>
    <artifactId>chaos-monkey-spring-boot</artifactId>
    <version>X.X.X</version>
</dependency>
```

Start your application with the Spring Profile **chaos-monkey** to initialize the Chaos Monkey. Since version 1.5.0 and 2.0.0 it will **not** start
attacks on your application yet,
 you can activate this dynamically at runtime.

```
java -jar your-app.jar --spring.profiles.active=chaos-monkey
```
As long as you don't set the property  "<b>chaos.monkey.enabled</b>" to "<b>true</b>", nothing will happen!

As you can see, you don't have to change the source code!

## Dynamic Configuration at runtime
Since version 1.5.0 and 2.0.0 the Chaos Monkey for Spring Boot can be configured at runtime via a Spring Boot Actuator Endpoint.

> Take a look at the current [snapshots](#snapshots) and [documentation](#releases).

<a name="howitworks"></a>
### How does it work?
If Spring Boot Chaos Monkey is on your classpath and activated with profile name "chaos-monkey", it will automatically scan your application for all classes annotated with any of the following Spring annotations:

- @Controller
- @RestController
- @Service
- @Repository
- @Component

By configuration you define which assaults and watcher are activated, per default only the @Service watcher and the latency assault are activated.

<p align="center">
  <img class="imgborder s1" width="90%" src="images/sb-chaos-monkey-architecture.png">
</p>

#### Example - single Spring Boot application
> Following example is based on Chaos Monkey for Spring Boot 1.0.1

Let's say you built a standalone Spring Boot application. For example, there is a service annotated with Spring @Service annotation and some other components. Now we want to attack our service component.

<p align="center">
  <img class="imgborder s1" width="40%" src="images/cases/case_single_boot_app.png">
</p>
Let´s activate Chaos Monkey for Spring Boot, only 2 steps are required.

1. Added Chaos Monkey for Spring Boot to your dependencies.
{% highlight xml %}
<dependency>
    <groupId>de.codecentric</groupId>
    <artifactId>chaos-monkey-spring-boot</artifactId>
    <version>2.0.1</version>
</dependency>
{% endhighlight %}
2. Start your app enabling the service using the property below:
{% highlight shell %}
java -jar your-app.jar --spring.profiles.active=chaos-monkey
{% endhighlight %}

Chaos Monkey for Spring Boot will attack your @Service classes and will randomly add some latency to all <b>public</b> methods.

There are some more assaults and watcher, that can attack your app.
<a name="watcher"></a>
## Watcher
A watcher is a Chaos Monkey for Spring Boot component, that will scan your app for a specific type of annotation.

Following Spring annotation are supported:
- @Controller
- @RestController
- @Service
- @Repository
- @Component

With the help of [Spring AOP](https://docs.spring.io/spring/docs/current/spring-framework-reference/core.html), Chaos Monkey recognizes the execution of a public method and will either not execute any action or start one of its assaults. You can customize the behave by configuration.

<a name="assaults"></a>
## Assaults

Following assaults are actual provided:
- Latency Assault
- Exception Assault
- AppKiller Assault

You can customize the behave by configuration, please take a look at the documentation.

<a name="support"></a>
## Support for Spring Boot 1.5.x, 2.0.x and 2.1.x

Chaos Monkey for Spring Boot versions will follow the versions of Spring Boot. The first number of the Chaos Monkey release will show you which is the right one for your Spring Boot version.

|                                   | Chaos Monkey 1.0.1 | Chaos Monkey 1.5.0 | Chaos Monkey 2.0.0 | Chaos Monkey 2.0.1 | Chaos Monkey 2.0.2 | Chaos Monkey 2.1.0-SNAPSHOT |
| --------------------------------- | :----------------: | :----------------: | :----------------: | :----------------: | :----------------: | :-------------------------: |
| <b>Spring Boot 1.5.0 - 1.5.19</b> |         no         |     <b>yes</b>     |         no         |         no         |         no         |             no              |
| <b>Spring Boot 2.0.0 - 2.0.8</b>  |     <b>yes</b>     |         no         |     <b>yes</b>     |     <b>yes</b>     |     <b>yes</b>     |         <b>yes</b>          |
| <b>Spring Boot 2.1.0 - 2.1.2</b>  |         no         |         no         |         no         |         no         |     <b>yes</b>     |         <b>yes</b>          |

<a name="docs"></a>
## Documentation
A detailed documentation about the configuration of the Chaos Monkey for Spring Boot can be found here.
### Chaos Monkey for Spring Boot 1.5.x
- [Version 1.5.0](https://codecentric.github.io/chaos-monkey-spring-boot/1.5.0)

### Chaos Monkey for Spring Boot 2.0.x
- [Version 1.0.1](https://codecentric.github.io/chaos-monkey-spring-boot/1.0.1/)
- [Version 2.0.0](https://codecentric.github.io/chaos-monkey-spring-boot/2.0.0)
- [Version 2.0.1](https://codecentric.github.io/chaos-monkey-spring-boot/2.0.1)
- [Version 2.0.2](https://codecentric.github.io/chaos-monkey-spring-boot/2.0.2)

### Chaos Monkey for Spring Boot SNAPSHOT
- [Version 2.1.0-SNAPSHOT](https://codecentric.github.io/chaos-monkey-spring-boot/2.1.0-SNAPSHOT)

## Releases
- Version 1.0.1 (depends on Spring Boot 2.0.1)
- Version 1.5.0 (supports Spring Boot 1.5.x)
- Version 2.0.0 (supports Spring Boot 2.0.x)
- Version 2.0.1 (supports Spring Boot 2.0.x)
- Version 2.0.2 (supports Spring Boot 2.0.x & 2.1.x)

<a name="snapshots"></a>
## Snapshots
You can access snapshot builds from the sonatype snapshot repository by adding the following to your `repositories`:
> **NOTE**: The [Maven documentation](https://maven.apache.org/guides/mini/guide-multiple-repositories.html) explains how to add a repository to your Maven configuration.
```xml
<repository>
	<id>sonatype-nexus-snapshots</id>
	<name>Sonatype Nexus Snapshots</name>
	<url>https://oss.sonatype.org/content/repositories/snapshots/</url>
	<snapshots>
		<enabled>true</enabled>
	</snapshots>
	<releases>
		<enabled>false</enabled>
	</releases>
</repository>
```

### Latest snapshots
#### Chaos Monkey for Spring Boot 1.5.x
```xml
<dependency>
  <groupId>de.codecentric</groupId>
  <artifactId>chaos-monkey-spring-boot</artifactId>
  <version>1.5.1-SNAPSHOT</version>
</dependency>
```

#### Chaos Monkey for Spring Boot 2.1.x
```xml
<dependency>
  <groupId>de.codecentric</groupId>
  <artifactId>chaos-monkey-spring-boot</artifactId>
  <version>2.1.0-SNAPSHOT</version>
</dependency>
```
