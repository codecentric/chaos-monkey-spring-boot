package com.example.chaos.monkey.chaosdemo.component;

import org.springframework.stereotype.Component;

/** @author Benjamin Wilms */
@Component
public class HelloComponent {

  public String sayHello() {

    return "Hello from Component";
  }
}
