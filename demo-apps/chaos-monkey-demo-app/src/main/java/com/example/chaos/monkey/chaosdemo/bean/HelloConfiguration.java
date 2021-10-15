package com.example.chaos.monkey.chaosdemo.bean;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HelloConfiguration {

  @Bean
  public HelloBean helloBean() {
    return new HelloBean();
  }
}
