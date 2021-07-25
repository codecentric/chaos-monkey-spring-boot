package com.example.chaos.monkey.toggledemo.service;

import com.example.chaos.monkey.toggledemo.repo.SimpleRepository;
import org.springframework.stereotype.Service;

/** @author Benjamin Wilms */
@Service
public class GreetingService {

  private final SimpleRepository simpleRepository;

  public GreetingService(SimpleRepository simpleRepository) {
    this.simpleRepository = simpleRepository;
  }

  public String greet() {
    return "Greetings from the server side!";
  }

  public String greetFromRepo() {
    return simpleRepository.getGreeting();
  }
}
