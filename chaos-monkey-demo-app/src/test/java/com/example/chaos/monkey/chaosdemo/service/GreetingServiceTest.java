package com.example.chaos.monkey.chaosdemo.service;

import com.example.chaos.monkey.chaosdemo.repo.Hello;
import com.example.chaos.monkey.chaosdemo.repo.HelloRepo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * @author Benjamin Wilms
 */
@RunWith(MockitoJUnitRunner.class)
public class GreetingServiceTest {

    @Mock
    private HelloRepo helloRepoMock;

    private GreetingService greetingService;


    @Before
    public void setUp() throws Exception {
        greetingService = new GreetingService(helloRepoMock);
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
    }
}