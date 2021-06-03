package com.example.chaos.monkey.toggledemo.repo;

import org.springframework.stereotype.Repository;

@Repository
public class SimpleRepository {

  public String getGreeting() {
    return "Hello";
  }
}
