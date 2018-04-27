package de.codecentric.spring.boot.demo.chaos.monkey.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author Benjamin Wilms
 */
@Controller
public class DemoController {

    @GetMapping("/hello")
    public String sayHello() {
        return "Hello!";
    }
}
