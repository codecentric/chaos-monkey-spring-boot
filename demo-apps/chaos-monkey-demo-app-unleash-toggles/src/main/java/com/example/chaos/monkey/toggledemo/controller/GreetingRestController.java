package com.example.chaos.monkey.toggledemo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Simple REST Controller example
 *
 * @author Benjamin Wilms
 */
@RestController
public class GreetingRestController {

  @GetMapping("/rest/hello")
  public ResponseEntity<String> sayHello() {
    return ResponseEntity.ok("REST hello!");
  }
}
