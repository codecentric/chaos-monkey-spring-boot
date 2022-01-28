package com.example.chaos.monkey.toggledemo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.example.chaos.monkey.toggledemo.repo.SimpleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** @author Clint Checketts */
@ExtendWith(MockitoExtension.class)
public class GreetingServiceTest {

  @Mock private SimpleRepository helloRepoMock;

  private GreetingService greetingService;

  @BeforeEach
  public void setUp() {
    greetingService = new GreetingService(helloRepoMock);
  }

  @Test
  public void greet() {
    assertThat(greetingService.greet()).isEqualTo("Greetings from the server side!");
  }

  @Test
  public void greetFromRepo() {
    String message = "message";
    when(helloRepoMock.getGreeting()).thenReturn(message);

    assertThat(greetingService.greetFromRepo()).isEqualTo(message);

    verify(helloRepoMock, times(1)).getGreeting();
    verifyNoMoreInteractions(helloRepoMock);
  }
}
