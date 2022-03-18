package com.example.chaos.monkey.chaosdemo.repo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/** @author Benjamin Wilms */
public class HelloTest {

  @Test
  public void checkConstructorGetter() {
    String expectedValueMessage = "test";
    long id = 0;
    Hello helloToTest = new Hello(id, expectedValueMessage);

    assertThat(helloToTest.getMessage()).isEqualTo(expectedValueMessage);
    assertThat(helloToTest.getId()).isEqualTo(id);
  }

  @Test
  public void checkSetterGetter() {
    String expectedValueMessage = "test";
    long id = 0;
    Hello helloToTest = new Hello(99, "empty");
    helloToTest.setId(id);
    helloToTest.setMessage(expectedValueMessage);

    assertThat(helloToTest.getMessage()).isEqualTo(expectedValueMessage);
    assertThat(helloToTest.getId()).isEqualTo(id);
  }
}
