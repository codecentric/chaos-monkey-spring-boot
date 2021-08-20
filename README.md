[![Apache License 2](https://img.shields.io/github/license/codecentric/chaos-monkey-spring-boot)](https://www.apache.org/licenses/LICENSE-2.0.txt)
[![Build Status](https://github.com/codecentric/chaos-monkey-spring-boot/workflows/Chaos%20Monkey%20Build/badge.svg)](https://github.com/codecentric/chaos-monkey-spring-boot/actions?query=workflow%3A%22Chaos+Monkey+Build%22)
[![codecov](https://codecov.io/gh/codecentric/chaos-monkey-spring-boot/branch/main/graph/badge.svg)](https://codecov.io/gh/codecentric/chaos-monkey-spring-boot)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/de.codecentric/chaos-monkey-spring-boot/badge.svg)](https://maven-badges.herokuapp.com/maven-central/de.codecentric/chaos-monkey-spring-boot/)
[![Contributor Covenant](https://img.shields.io/badge/Contributor%20Covenant-v2.0%20adopted-ff69b4.svg)](CODE_OF_CONDUCT.adoc)  

# Chaos Monkey for Spring Boot
### inspired by Chaos Engineering at Netflix

<p align="center">
  <img src="docs/images/sb-chaos-monkey-logo.png">
</p>

This project provides a Chaos Monkey for Spring Boot applications and will try to attack your running Spring Boot App.

>Everything from getting started to advanced usage is explained in the [Documentation for Chaos Monkey for Spring Boot](https://codecentric.github.io/chaos-monkey-spring-boot/latest/) 

## Introduction
If you're not familiar with the principles of chaos engineering yet, check out this blog post and enter the world of chaos engineering.

<a href="https://blog.codecentric.de/en/2018/07/chaos-engineering/" target="_blank"><img src="https://pbs.twimg.com/media/DhaRNO7XUAAi00i.jpg" 
alt="Chaos Engineering – withstanding turbulent conditions in production" width="260" height="155" border="10" /></a><br>

Get familiar with the Chaos Monkey for Spring Boot in the following video, <a href="https://goo.gl/r2Tmig" target="_blank">available on YouTube</a>:

<a href="https://goo.gl/r2Tmig" target="_blank"><img src="https://i.ytimg.com/vi/7sQiIR9qCdA/maxresdefault.jpg" 
alt="Chaos Monkey for Spring Boot" width="260" height="155" border="10" /></a><br>

## What is the goal of Chaos Monkey?

Inspired by [PRINCIPLES OF CHAOS ENGINEERING](https://principlesofchaos.org/), with a focus on [Spring Boot](https://projects.spring.io/spring-boot/), Chaos Monkey wants to test applications better and especially during operation.

After writing many unit and integration tests, a code coverage from 70% to 80%, this unpleasant feeling remains, how our baby behaves in production?

Many questions remain unanswered:

- Will our fallbacks work?
- How does the application behave with network latency?
- What if one of our services breaks down?
- Service Discovery works, but is our Client-Side-Load-Balancing also working?

As you can see, there are many more questions and open topics you have to deal with.

That was the start of Chaos Monkey for Spring Boot.

### How does it work?
If Spring Boot Chaos Monkey is on your classpath and activated with profile name `chaos-monkey`, it will automatically hook into your application. 

Now you can activate [watchers](https://codecentric.github.io/chaos-monkey-spring-boot/latest/#watchers), which look for classes to [assault](https://codecentric.github.io/chaos-monkey-spring-boot/latest/#assaults). There are also [runtime assaults](https://codecentric.github.io/chaos-monkey-spring-boot/latest/#runtime-assaults), which attack your whole application.

<p align="center">
  <img class="imgborder s1" width="90%" src="docs/images/sb-chaos-monkey-architecture.png">
</p>

## Be social and communicative!
If you start to implement Chaos Engineering at your company, then you must be a very social and communicative person. Why? Because you will get to know many of your colleagues personally in a very short time when your chaos experiments strike.

### Check your resilience
Are your services already resilient and can handle failures? Don´t start a chaos experiment if not!

### Implement active application monitoring
Check your monitoring and check if you can see the overall state of your system. There are many great tools out there to get a pleasant feeling about your entire system.

### Define steady states
Define a metric to check a steady state of your service and of course your entire system. Start small with a service that is not critical.

### Do not start in production
Of course, you can start in production, but keep in mind...

> The best place on earth is...production!<br>
> *Josh Long*

...so let's keep production as the best place on earth and look for our first experiences on another stage. If all goes well, and you're confident, run it in production.

## Documentation
[Documentation](https://codecentric.github.io/chaos-monkey-spring-boot/latest/)

## Help

We are using GitHub issues to track bugs, improvements and new features (for more information see [Contributions](#contributions)). If you have a general question on how to use Chaos Monkey for Spring Boot, please ask on Stack Overflow using the tag [`#spring-boot-chaos-monkey`](https://stackoverflow.com/questions/tagged/spring-boot-chaos-monkey).

## Contributions 
Chaos Monkey is open source and welcomes contributions from everyone. The [contribution guideline](https://github.com/codecentric/chaos-monkey-spring-boot/blob/main/CONTRIBUTING.adoc) is where you should begin in order to best understand how to contribute to this project. 



## Releases
[Releases](https://github.com/codecentric/chaos-monkey-spring-boot/releases)
