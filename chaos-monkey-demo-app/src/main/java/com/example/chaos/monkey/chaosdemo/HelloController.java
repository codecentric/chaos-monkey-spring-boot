package com.example.chaos.monkey.chaosdemo;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author Benjamin Wilms
 */
@Controller
public class HelloController {

    @GetMapping("/hello")
    public ResponseEntity<String> sayHello() {
        return ResponseEntity.ok(sayHelloPlease());
    }

    private String sayHelloPlease() {
        return "Hello!";
    }
}
