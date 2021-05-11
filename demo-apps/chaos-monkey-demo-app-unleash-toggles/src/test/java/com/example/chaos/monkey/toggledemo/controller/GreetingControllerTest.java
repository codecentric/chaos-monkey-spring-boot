package com.example.chaos.monkey.toggledemo.controller;

import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.chaos.monkey.toggledemo.component.HelloComponent;
import de.codecentric.spring.boot.chaos.monkey.configuration.ChaosMonkeyProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

/** @author Benjamin Wilms */
@WebMvcTest(GreetingController.class)
public class GreetingControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private ChaosMonkeyProperties chaosMonkeyProperties;

  @MockBean private HelloComponent helloComponent;

  /** @see GreetingController#sayHello() */
  @Test
  public void shouldReturnHello() throws Exception {

    this.mockMvc
        .perform(get("/helloagain").with(user("user")))
        .andExpect(status().isOk())
        .andExpect(content().string(is("Again hello!")));
  }

  /** @see GreetingController#sayHello() */
  @Test
  public void shouldFailForTestUser() throws Exception {

    this.mockMvc
        .perform(get("/helloagain").with(user("chaosuser")))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk())
        .andExpect(content().string(is("Again hello!")));
  }

  /** @see GreetingController#sayHelloFromComponent() */
  @Test
  public void shouldReturnHelloFromComponent() throws Exception {
    when(helloComponent.sayHello()).thenReturn("Hello from Component");

    this.mockMvc
        .perform(get("/hellocomponent").with(user("user")))
        .andExpect(status().isOk())
        .andExpect(content().string(is("Hello from Component")));
  }
}
