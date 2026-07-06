package it.unibo.controller.server;

import it.unibo.controller.server.network.http.GameHttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GameHttpServerTest {
    private GameHttpServer server;
    private static final int TEST_PORT = 8080;

    @BeforeEach
    void setup() {
        server = new GameHttpServer(TEST_PORT);
    }

    @AfterEach
    void tearDown() {
        if (server != null) {
            server.stop();
        }
    }

    @Test
    void registeredEndpointRespondsAfterStart() throws IOException, InterruptedException {
        server.addGetEndpoint("/test-endpoint", ctx -> ctx.result("OK"));
        server.start();
        HttpResponse<String> response = sendGetRequest("/test-endpoint");
        assertEquals("OK", response.body());
    }

    @Test
    void unregisteredPathReturns404() throws Exception {
        server.addGetEndpoint("/test-endpoint", ctx -> ctx.result("OK"));
        server.start();
        HttpResponse<String> response = sendGetRequest("/endpoint-that-does-not-exist");
        assertEquals(404, response.statusCode());
    }

    @Test
    void addingEndpointAfterStartThrows() {
        server.addGetEndpoint("/test-endpoint", ctx -> ctx.result("OK"));
        server.start();
        assertThrows(IllegalStateException.class, () -> server.addGetEndpoint("/test-endpoint", ctx -> ctx.result("OK")));
    }

    private HttpResponse<String> sendGetRequest(String path) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + TEST_PORT + path))
                .GET()
                .build();
        try (HttpClient client = HttpClient.newHttpClient()) {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        }
    }
}
