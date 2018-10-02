package com.example.chaos.monkey.chaosdemo.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Benjamin Wilms
 */
@RunWith(SpringRunner.class)
@WebMvcTest(GreetingRestController.class)
public class GreetingRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldReturnHello() throws Exception {


        this.mockMvc.perform(get("/rest/hello")).andExpect(status().isOk())
                .andExpect(content().string(is("REST hello!")));
    }
}