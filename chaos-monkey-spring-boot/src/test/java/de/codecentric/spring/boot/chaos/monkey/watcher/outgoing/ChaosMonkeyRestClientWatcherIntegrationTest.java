package de.codecentric.spring.boot.chaos.monkey.watcher.outgoing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import de.codecentric.spring.boot.demo.chaos.monkey.ChaosDemoApplication;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;
import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpServer;

class ChaosMonkeyRestClientWatcherIntegrationTest {

    private static DisposableServer startServer() {
        return HttpServer.create().host("127.0.0.1").port(0).handle((request, response) -> response.sendString(Mono.just("OK"))).bindNow();
    }

    @SpringBootTest(properties = {"chaos.monkey.enabled=true", "chaos.monkey.watcher.rest-client=true",
            "chaos.monkey.assaults.exceptions-active=true",
            "chaos.monkey.assaults.exception.type=org.springframework.web.client.HttpServerErrorException",
            "chaos.monkey.assaults.exception.arguments[0].type=org.springframework.http.HttpStatusCode",
            "chaos.monkey.assaults.exception.arguments[0].value=500"}, classes = {ChaosDemoApplication.class})
    @ActiveProfiles("chaos-monkey")
    @Nested
    class ExceptionAssaultIntegrationTest {

        @Autowired
        private RestClient restClient;

        @Test
        public void testRestClientExceptionAssault() {
            DisposableServer server = startServer();
            try {
                assertThatThrownBy(() -> restClient.get().uri("http://127.0.0.1:" + server.port()).retrieve().toEntity(String.class))
                        .isInstanceOf(HttpServerErrorException.class).hasMessage("500 INTERNAL_SERVER_ERROR");
            } finally {
                server.disposeNow();
            }
        }
    }

    @SpringBootTest(properties = {"chaos.monkey.enabled=true", "chaos.monkey.watcher.rest-client=true",
            "chaos.monkey.assaults.latency-active=true", "chaos.monkey.assaults.latency-range-start=500",
            "chaos.monkey.assaults.latency-range-end=600"}, classes = {ChaosDemoApplication.class})
    @ActiveProfiles("chaos-monkey")
    @Nested
    class LatencyAssaultIntegrationTest {

        @Autowired
        private RestClient restClient;

        @Test
        public void testRestClientLatencyAssault() {
            DisposableServer server = startServer();
            try {
                long start = System.nanoTime();
                String body = restClient.get().uri("http://127.0.0.1:" + server.port()).retrieve().body(String.class);
                long elapsedMillis = (System.nanoTime() - start) / 1_000_000;

                assertThat(body).isEqualTo("OK");
                assertThat(elapsedMillis).isGreaterThanOrEqualTo(500);
            } finally {
                server.disposeNow();
            }
        }
    }

    @SpringBootTest(properties = {"chaos.monkey.enabled=true", "chaos.monkey.watcher.rest-client=false",
            "chaos.monkey.assaults.exceptions-active=true"}, classes = {ChaosDemoApplication.class})
    @ActiveProfiles("chaos-monkey")
    @Nested
    class WatcherDisabledIntegrationTest {

        @Autowired
        private RestClient restClient;

        @Test
        public void testNoChaosWhenWatcherIsDisabled() {
            DisposableServer server = startServer();
            try {
                String body = restClient.get().uri("http://127.0.0.1:" + server.port()).retrieve().body(String.class);
                assertThat(body).isEqualTo("OK");
            } finally {
                server.disposeNow();
            }
        }
    }
}