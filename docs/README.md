[![Build Status](https://travis-ci.org/MrBW/spring-boot-chaos-monkey.svg?branch=master)](https://travis-ci.org/MrBW/spring-boot-chaos-monkey)
[![codecov](https://codecov.io/gh/MrBW/spring-boot-chaos-monkey/branch/master/graph/badge.svg)](https://codecov.io/gh/MrBW/spring-boot-chaos-monkey)
# Spring Boot Chaos Monkey
### inspired by Chaos Engineering at Netflix

<p align="center">
  <img src="images/sb-chaos-monkey-logo.png">
</p>

This project provides a Spring Boot Chaos Monkey and will try to attack your running Spring Boot App.

>Chaos Engineering is the discipline of experimenting on a distributed system
in order to build confidence in the system’s capability
to withstand turbulent conditions in production.
> *principlesofchaos.org*

## Content
- [Goal of Chaos Monkey](#goal)
- [Social and communicative](#social)
- [What does Spring Boot Chaos Monkey do?](#dochaos)
- [How does it work?](#howitworks)

<a name="goal"></a>
## What is the goal of Chaos Monkey?
Inspired by [PRINCIPLES OF CHAOS ENGINEERING](http://principlesofchaos.org/) and by my work in distributed system, with a focus on Spring Boot, I wanted to test the resulting applications better and especially during operation.

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
## What does the Spring Boot Chaos Monkey do?
Spring Boot Chaos Monkey is a small library which you can integrate as a dependency into your existing application. As long as you don't use your application with the profile "<b>chaos-monkey</b>", nothing will happen.

As you can see, you don't have to change the source code!
<a name="howitworks"></a>
### How does it work?
If Spring Boot Chaos Monkey is on your classpath and activated with profile name "chaos-monkey", it will automatically scan your application for all classes annotated with any of the following Spring annotations:

- @Controller
- @RestController
- @service
- @Repository

By configuration you define which assaults and watcher are activated, per default only the @Service watcher and the latency assault are activated.

#### Example - single Spring Boot application
Let's say you built a standalone Spring Boot application. For example, there is a service annotated with Spring @Service annotation.

<p align="center">
  <img width="50%" src="images/cases/case_single_boot_app.png">
</p>
Let´s activate Spring Boot Chaos Monkey, only 2 steps are required.

1. Added Spring Boot Chaos Monkey to your dependencies.
```
<dependency>
  <groupId>de.mrbwilms.spring.boot.chaos.monkey</groupId>
  <artifactId>spring-boot-chaos-monkey</artifactId>
  <version>1.2</version>
</dependency>
```
2. Start your app with profile "chaos-monkey"
java -jar your-app.jar --spring.profiles.active=chaos-monkey

Spring Boot Chaos Monkey will attack your @Service classes and will randomly add some latency to all <b>public</b> methods.

There are some more assaults and watcher, that can attack your app.

> I´m still working on this page and the documentation!
