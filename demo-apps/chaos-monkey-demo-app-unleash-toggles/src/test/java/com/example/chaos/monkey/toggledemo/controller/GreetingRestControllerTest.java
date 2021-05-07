package com.example.chaos.monkey.toggledemo.controller;

import static org.hamcrest.core.Is.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.codecentric.spring.boot.chaos.monkey.configuration.ChaosMonkeyProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

/** @author Benjamin Wilms */
@WebMvcTest(GreetingRestController.class)
public class GreetingRestControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private ChaosMonkeyProperties chaosMonkeyProperties;

  /** @see GreetingController#sayHello() */
  @Test
  public void shouldReturnHello() throws Exception {

    this.mockMvc
        .perform(get("/rest/hello").with(user("user1")))
        .andExpect(status().isOk())
        .andExpect(content().string(is("REST hello!")));
  }
}
