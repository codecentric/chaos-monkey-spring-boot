package de.mrbwilms.spring.boot.chaos.monkey.demo.service;

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
