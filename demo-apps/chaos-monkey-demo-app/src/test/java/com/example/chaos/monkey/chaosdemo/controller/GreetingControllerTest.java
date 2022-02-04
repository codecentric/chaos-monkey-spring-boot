package com.example.chaos.monkey.chaosdemo.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.chaos.monkey.chaosdemo.bean.HelloConfiguration;
import com.example.chaos.monkey.chaosdemo.component.HelloComponent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

/** @author Benjamin Wilms */
@WebMvcTest({GreetingController.class, HelloComponent.class, HelloConfiguration.class})
public class GreetingControllerTest {

  @Autowired private MockMvc mockMvc;

  @Test
  public void shouldReturnHello() throws Exception {

    this.mockMvc
        .perform(get("/helloagain"))
        .andExpect(status().isOk())
        .andExpect(content().string("Again hello!"));
  }

  @Test
  public void shouldReturnHelloFromComponent() throws Exception {

    this.mockMvc
        .perform(get("/hellocomponent"))
        .andExpect(status().isOk())
        .andExpect(content().string("Hello from Component"));
  }

  @Test
  public void shouldReturnHelloFromBean() throws Exception {

    this.mockMvc
        .perform(get("/hellobean"))
        .andExpect(status().isOk())
        .andExpect(content().string("Hello from Bean"));
  }
}
