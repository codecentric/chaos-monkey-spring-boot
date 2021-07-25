package com.example.chaos.monkey.toggledemo.controller;

import com.example.chaos.monkey.toggledemo.component.HelloComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/** @author Benjamin Wilms */
@Controller
public class GreetingController {

  @Autowired private HelloComponent helloComponent;

  @GetMapping("/helloagain")
  public ResponseEntity<String> sayHello() {
    return ResponseEntity.ok("Again hello!");
  }

  @GetMapping("/hellocomponent")
  public ResponseEntity<String> sayHelloFromComponent() {
    return ResponseEntity.ok(helloComponent.sayHello());
  }
}
