package de.codecentric.spring.boot.chaos.monkey.watcher.outgoing;

import de.codecentric.spring.boot.chaos.monkey.component.ChaosMonkeyRequestScope;
import de.codecentric.spring.boot.chaos.monkey.component.ChaosTarget;
import de.codecentric.spring.boot.chaos.monkey.configuration.AssaultProperties;
import de.codecentric.spring.boot.chaos.monkey.configuration.WatcherProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

/** @author Marcel Becker */
public class ChaosMonkeyWebClientWatcher implements ExchangeFilterFunction {

  private final ChaosMonkeyRequestScope chaosMonkeyRequestScope;
  private final WatcherProperties watcherProperties;
  private final AssaultProperties assaultProperties;

  private static final String ALREADY_FILTERED_SUFFIX = ".FILTERED";

  public ChaosMonkeyWebClientWatcher(
      final ChaosMonkeyRequestScope chaosMonkeyRequestScope,
      final WatcherProperties watcherProperties,
      AssaultProperties assaultProperties) {
    this.chaosMonkeyRequestScope = chaosMonkeyRequestScope;
    this.watcherProperties = watcherProperties;
    this.assaultProperties = assaultProperties;
  }

  @Override
  public Mono<ClientResponse> filter(
      ClientRequest clientRequest, ExchangeFunction exchangeFunction) {
    final RequestFilterWrapper requestFilterWrapper = handleOncePerRequest(clientRequest);
    Mono<ClientResponse> response = exchangeFunction.exchange(requestFilterWrapper.clientRequest);
    if (requestFilterWrapper.filter) {
      if (watcherProperties.isWebClient()) {
        try {
          chaosMonkeyRequestScope.callChaosMonkey(
              ChaosTarget.WEB_CLIENT, clientRequest.url().toString());
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
    }
    return response;
  }

  private RequestFilterWrapper handleOncePerRequest(final ClientRequest clientRequest) {
    final String filterName = this.getClass().getName() + ALREADY_FILTERED_SUFFIX;
    final Boolean filter;
    final ClientRequest request;
    if (clientRequest.attribute(filterName).isPresent()) {
      filter = Boolean.FALSE;
      request = clientRequest;
    } else {
      filter = Boolean.TRUE;
      request = ClientRequest.from(clientRequest).attribute(filterName, Boolean.TRUE).build();
    }
    return new RequestFilterWrapper(request, filter);
  }

  @Data
  @AllArgsConstructor
  private static class RequestFilterWrapper {

    private final ClientRequest clientRequest;
    private final Boolean filter;
  }

  static class ErrorClientResponse {

    static final String ERROR_BODY =
        "{\"error\": \"This is a Chaos Monkey for Spring Boot generated failure\"}";

    private static ClientResponse getResponse() {
      return ClientResponse.create(HttpStatus.INTERNAL_SERVER_ERROR).body(ERROR_BODY).build();
    }
  }
}
