package com.example.chaos.monkey.chaosdemo.service;

import com.example.chaos.monkey.chaosdemo.repo.Hello;
import com.example.chaos.monkey.chaosdemo.repo.HelloRepo;
import org.springframework.stereotype.Service;

/**
 * @author Benjamin Wilms
 */
@Service
public class GreetingService {

    private final HelloRepo helloRepo;

    public GreetingService(HelloRepo helloRepo) {
        this.helloRepo = helloRepo;
    }

    public String greet() {
        return "Greetings from the server side!";
    }

    public String greetFromRepo() {
        Hello databaseSide = helloRepo.save(new Hello(0, "Greetings from the database side"));
        return databaseSide.getMessage();
    }
}
