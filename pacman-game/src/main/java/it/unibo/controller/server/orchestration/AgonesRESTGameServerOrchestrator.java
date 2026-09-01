package it.unibo.controller.server.orchestration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AgonesRESTGameServerOrchestrator implements GameServerOrchestrator {
    private static final String AGONES_SIDECAR_DEFAULT_HTTP_PORT = "9358";
    private static final String AGONES_REST_API_ADDRESS = "http://localhost:";
    private static final long INITIAL_DELAY_MILLIS = 1000;
    private static final long HEALTHCHECK_PERIOD_IN_MILLIS = 2000;

    private final String agonesRestApiBaseUrl;
    private final String matchId;
    private final URI gameServerManagerUri;
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ScheduledExecutorService healthCheckExecutor = Executors.newSingleThreadScheduledExecutor();
    private final Logger logger = LoggerFactory.getLogger(AgonesRESTGameServerOrchestrator.class);

    public AgonesRESTGameServerOrchestrator (final String matchId) {
        this.matchId = matchId;
        String gameServerManagerUriString = System.getenv("GAMESERVER_MANAGER_URI");
        if (gameServerManagerUriString == null) {
            throw new IllegalStateException("Environment variable GAMESERVER_MANAGER_URI must be set.");
        }
        try {
            this.gameServerManagerUri = new URI(gameServerManagerUriString);
            if (!this.gameServerManagerUri.isAbsolute()) {
                throw new IllegalArgumentException("The specified GAMESERVER_MANAGER_URI is not absolute.");
            }
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Syntax error in the specified GAMESERVER_MANAGER_URI: " + e);
        }
        String agonesSidecarHTTPPort = System.getenv("AGONES_SIDECAR_HTTP_PORT");
        if (agonesSidecarHTTPPort == null) {
            agonesSidecarHTTPPort = AGONES_SIDECAR_DEFAULT_HTTP_PORT;
        }
        this.logger.debug("Communicating with Agones through port: {}", agonesSidecarHTTPPort);
        this.agonesRestApiBaseUrl = AGONES_REST_API_ADDRESS + agonesSidecarHTTPPort;
    }

    @Override
    public void start() {
        try {
            startHealthCheck();
            this.logger.info("Starting and allocating game server");
            readyRequest();
            allocateRequest();
        } catch (Exception e) {
            this.logger.error(e.getMessage());
        }
    }

    @Override
    public void shutdown() {
        try {
            signalMatchEnded();
            stopHealthCheck();
            this.logger.info("Shutting down game server orchestration");
            shutdownRequest();
        } catch (Exception e) {
            this.logger.error(e.getMessage());
        }
    }

    private void startHealthCheck() {
        this.logger.info("Starting Agones health checking");
        healthCheckExecutor.scheduleAtFixedRate(() -> {
            try {
                healthPing();
                this.logger.debug("Sent health ping"); // TODO: only to check whether the ping is actually being sent
            } catch (Exception e) {
                this.logger.error(e.getMessage());
            }
        }, INITIAL_DELAY_MILLIS, HEALTHCHECK_PERIOD_IN_MILLIS, TimeUnit.MILLISECONDS);
    }

    private void stopHealthCheck() {
        healthCheckExecutor.shutdown();
    }

    private void readyRequest() throws Exception {
        sendAgonesAPIRequest("/ready");
    }

    private void allocateRequest() throws Exception {
        sendAgonesAPIRequest("/allocate");
    }

    private void shutdownRequest() throws Exception {
        sendAgonesAPIRequest("/shutdown");
    }

    private void healthPing() throws Exception {
        sendAgonesAPIRequest("/health");
    }

    private void signalMatchEnded() throws Exception {
        sendAPIPostRequest(
                this.gameServerManagerUri.resolve("/gameservermanager/match/ended"),
                "{\"match-id\": \"" + matchId + "\"}");
    }

    private void sendAgonesAPIRequest(final String path) throws Exception {
        HttpResponse<String> response = sendAPIPostRequest(URI.create(agonesRestApiBaseUrl + path), "{}");
        this.logger.debug("Response status for endpoint \"{}\": {}", path, response.statusCode()); // TODO: debug log to see response status
        // this.logger.debug(response.body()); TODO: debug log to see response
    }

    private HttpResponse<String> sendAPIPostRequest(final URI uri, final String body) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    }
}
