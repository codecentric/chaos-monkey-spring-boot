package com.example.chaos.monkey.chaosdemo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/** @author Benjamin Wilms */
@Controller
public class GreetingController {

  @GetMapping("/helloagain")
  public ResponseEntity<String> sayHello() {
    return ResponseEntity.ok("Again hello!");
  }
}
