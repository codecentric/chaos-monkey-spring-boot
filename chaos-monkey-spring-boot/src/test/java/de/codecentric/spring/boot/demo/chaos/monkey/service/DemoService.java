package de.codecentric.spring.boot.demo.chaos.monkey.service;

import org.springframework.stereotype.Service;

/**
 * @author Benjamin Wilms
 */
@Service
public class DemoService {

    public String sayHelloService() {
        return "Hello from Service!";
    }
}
