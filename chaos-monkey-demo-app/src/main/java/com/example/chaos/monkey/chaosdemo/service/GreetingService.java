package com.example.chaos.monkey.chaosdemo.service;

import org.springframework.stereotype.Service;

/**
 * @author Benjamin Wilms
 */
@Service
public class GreetingService {

    public String greet() {
        return "Greetings from the server side!";
    }
}
