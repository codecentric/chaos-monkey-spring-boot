package com.example.chaos.monkey.chaosdemo.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.example.chaos.monkey.chaosdemo.repo.Hello;
import com.example.chaos.monkey.chaosdemo.repo.HelloRepo;
import com.example.chaos.monkey.chaosdemo.repo.HelloRepoAnnotation;
import com.example.chaos.monkey.chaosdemo.repo.HelloRepoJpa;
import com.example.chaos.monkey.chaosdemo.repo.HelloRepoSearchAndSorting;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/** @author Benjamin Wilms */
@RunWith(MockitoJUnitRunner.class)
public class GreetingServiceTest {

  @Mock private HelloRepo helloRepoMock;

  @Mock private HelloRepoSearchAndSorting helloRepoSearchAndSortingMock;

  @Mock private HelloRepoJpa helloRepoJpaMock;

  @Mock private HelloRepoAnnotation helloRepoAnnotationMock;

  private GreetingService greetingService;

  @Before
  public void setUp() {
    greetingService =
        new GreetingService(
            helloRepoMock,
            helloRepoSearchAndSortingMock,
            helloRepoJpaMock,
            helloRepoAnnotationMock);
  }

  @Test
  public void greet() {

    assertThat(greetingService.greet(), is("Greetings from the server side!"));
  }

  @Test
  public void greetFromRepo() {
    String message = "message";
    Hello saveObject = new Hello(0, message);
    when(helloRepoMock.save(any(Hello.class))).thenReturn(saveObject);

    assertThat(greetingService.greetFromRepo(), is(message));

    verify(helloRepoMock, times(1)).save(any(Hello.class));
    verifyNoMoreInteractions(
        helloRepoMock, helloRepoSearchAndSortingMock, helloRepoJpaMock, helloRepoAnnotationMock);
  }

  @Test
  public void testPagingAndSorting() {
    String message = "message";
    Hello saveObject = new Hello(0, message);
    when(helloRepoSearchAndSortingMock.save(any(Hello.class))).thenReturn(saveObject);
    when(helloRepoSearchAndSortingMock.findById(saveObject.getId()))
        .thenReturn(Optional.of(saveObject));

    assertThat(greetingService.greetFromRepoPagingSorting(), is(message));

    verify(helloRepoSearchAndSortingMock, times(1)).save(any(Hello.class));
    verify(helloRepoSearchAndSortingMock, times(1)).findById(saveObject.getId());
    verifyNoMoreInteractions(
        helloRepoMock, helloRepoSearchAndSortingMock, helloRepoJpaMock, helloRepoAnnotationMock);
  }

  @Test
  public void testJpaRepo() {
    String message = "message";
    Hello saveObject = new Hello(0, message);
    when(helloRepoJpaMock.save(any(Hello.class))).thenReturn(saveObject);
    when(helloRepoJpaMock.findById(saveObject.getId())).thenReturn(Optional.of(saveObject));

    assertThat(greetingService.greetFromRepoJpa(), is(message));
    verify(helloRepoJpaMock, times(1)).save(any(Hello.class));
    verify(helloRepoJpaMock, times(1)).findById(saveObject.getId());
    verifyNoMoreInteractions(
        helloRepoMock, helloRepoSearchAndSortingMock, helloRepoJpaMock, helloRepoAnnotationMock);
  }

  @Test
  public void testAnnotationRepo() {
    String message = "message";
    Hello saveObject = new Hello(0, message);
    when(helloRepoAnnotationMock.save(any(Hello.class))).thenReturn(saveObject);
    when(helloRepoAnnotationMock.findById(saveObject.getId())).thenReturn(Optional.of(saveObject));

    assertThat(greetingService.greetFromRepoAnnotation(), is(message));
    verify(helloRepoAnnotationMock, times(1)).save(any(Hello.class));
    verify(helloRepoAnnotationMock, times(1)).findById(saveObject.getId());
    verifyNoMoreInteractions(
        helloRepoMock, helloRepoSearchAndSortingMock, helloRepoJpaMock, helloRepoAnnotationMock);
  }
}
