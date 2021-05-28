package de.codecentric.spring.boot.chaos.monkey.watcher.outgoing;

import de.codecentric.spring.boot.chaos.monkey.component.ChaosMonkeyRequestScope;
import de.codecentric.spring.boot.chaos.monkey.configuration.AssaultProperties;
import de.codecentric.spring.boot.chaos.monkey.configuration.WatcherProperties;
import java.util.Random;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

/**
 * @author Marcel Becker
 */
public class ChaosMonkeyWebClientWatcher implements ExchangeFilterFunction {

  private final ChaosMonkeyRequestScope chaosMonkeyRequestScope;
  private final WatcherProperties watcherProperties;
  private final AssaultProperties assaultProperties;

  public ChaosMonkeyWebClientWatcher(
      final ChaosMonkeyRequestScope chaosMonkeyRequestScope,
      final WatcherProperties watcherProperties,
      AssaultProperties assaultProperties) {
    this.chaosMonkeyRequestScope = chaosMonkeyRequestScope;
    this.watcherProperties = watcherProperties;
    this.assaultProperties = assaultProperties;
  }

  @Override
  public Mono<ClientResponse> filter(ClientRequest clientRequest,
      ExchangeFunction exchangeFunction) {
    Mono<ClientResponse> response;
    response = exchangeFunction.exchange(clientRequest);
    if (watcherProperties.isWebClient()) {
      try {
        chaosMonkeyRequestScope.callChaosMonkey(this.getClass().getSimpleName());
      } catch (final Exception exception) {
        try {
          if (exception.getClass().equals(assaultProperties.getException().getExceptionClass())) {
            response = Mono.just(ErrorClientResponse.getResponse());
          } else {
            throw exception;
          }
        } catch (ClassNotFoundException e) {
          throw new RuntimeException(e);
        }
      }
    }
    return response;
  }

  static class ErrorClientResponse {

    static final HttpStatus[] ERROR_STATUS_CODES = {
        HttpStatus.INTERNAL_SERVER_ERROR,
        HttpStatus.BAD_REQUEST,
        HttpStatus.FORBIDDEN,
        HttpStatus.UNAUTHORIZED,
        HttpStatus.NOT_FOUND,
    };

    static final String ERROR_BODY =
        "{\"error\": \"This is a Chaos Monkey for Spring Boot generated failure\"}";

    private static ClientResponse getResponse() {
      return ClientResponse
          .create(ERROR_STATUS_CODES[new Random().nextInt(ERROR_STATUS_CODES.length)])
          .body(ERROR_BODY)
          .build();
    }
  }


}
