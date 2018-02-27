[![Build Status](https://travis-ci.org/MrBW/spring-boot-chaos-monkey.svg?branch=master)](https://travis-ci.org/MrBW/spring-boot-chaos-monkey)
[![codecov](https://codecov.io/gh/MrBW/spring-boot-chaos-monkey/branch/master/graph/badge.svg)](https://codecov.io/gh/MrBW/spring-boot-chaos-monkey)
# Spring Boot Chaos Monkey
### inspired by Chaos Engineering at Netflix

<p align="center">
  <img src="images/sb-chaos-monkey-logo.png">
</p>

This project provides an Spring Boot Chaos Monkey and will try to attack your running Spring Boot App.

## What is the goal of Chaos Monkey?
Inspired by http://principlesofchaos.org/ and by my work in distributed system, with a focus on Spring Boot, I wanted to test the resulting applications better and especially during operation.

> The best place on earth is...production!<br>
> *Josh Long - Spring Developer Advocate*

After the writing of many unit and integration tests, this unpleasant feeling remains...how our baby behaves in production?<br><br>
Many questions remain unanswered....
- Will our fallbacks work?
- How does the application behave with network latency?
- What if one of our services breaks down?
- Service Discovery works, but is our Client-Side-Load-Balancing also working?

As yo can see, there are many more questions and open topics you have to deal with.

That was my start to take a deep dive into Chaos Engineering and I started this little project to share my thoughts and experience.

## What does the Spring Boot Chaos Monkey do?
Spring Boot Chaos Monkey is a small library which you can integrate as a dependency into your existing application. As long as you don't use your application with the profile "<b>chaos-monkey</b>", nothing will happen.

As you can see, you don't have to change the source code!

> I´m still working on this page and the documentation!
