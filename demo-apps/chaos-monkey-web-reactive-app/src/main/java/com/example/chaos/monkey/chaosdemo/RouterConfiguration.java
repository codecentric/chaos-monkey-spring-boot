package com.example.chaos.monkey.chaosdemo;

import com.example.chaos.monkey.chaosdemo.component.HelloComponent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

/** @author Benjamin Wilms */
@Configuration
public class RouterConfiguration {

  @Bean
  public RouterFunction<ServerResponse> route(HelloComponent greetingHandler) {

    return RouterFunctions.route(
        RequestPredicates.GET("/hello").and(RequestPredicates.accept(MediaType.TEXT_PLAIN)),
        greetingHandler::hello);
  }
}
