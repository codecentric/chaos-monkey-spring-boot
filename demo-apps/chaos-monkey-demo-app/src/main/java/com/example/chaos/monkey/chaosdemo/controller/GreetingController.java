package com.example.chaos.monkey.chaosdemo.controller;

import com.example.chaos.monkey.chaosdemo.bean.HelloBean;
import com.example.chaos.monkey.chaosdemo.component.HelloComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/** @author Benjamin Wilms */
@Controller
public class GreetingController {

  @Autowired private HelloComponent helloComponent;

  @Autowired private HelloBean helloBean;

  @GetMapping("/helloagain")
  public ResponseEntity<String> sayHello() {
    return ResponseEntity.ok("Again hello!");
  }

  @GetMapping("/hellocomponent")
  public ResponseEntity<String> sayHelloFromComponent() {
    return ResponseEntity.ok(helloComponent.sayHello());
  }

  @GetMapping("/hellobean")
  public ResponseEntity<String> sayHelloFromBean() {
    return ResponseEntity.ok(helloBean.sayHello());
  }
}
