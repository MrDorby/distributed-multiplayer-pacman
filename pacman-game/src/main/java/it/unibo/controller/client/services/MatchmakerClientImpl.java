package it.unibo.controller.client.services;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.unibo.controller.client.common.ConnectionParameters;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class MatchmakerClientImpl implements MatchmakerClient {
    private static final String JOIN_LOBBY_PATH = "/join_lobby";
    private static final String QUIT_LOBBY_PATH = "/quit_lobby";
    private static final String GAME_SERVER_PATH = "/game_server";

    private final String joinLobbyUrl;
    private final String quitLobbyUrl;
    private final String gameServerUrl;

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    private String currentLobbyId;
    private String currentMatchId;

    public MatchmakerClientImpl(HttpClient httpClient, UriReader uri) {
        this.httpClient = httpClient;
        this.objectMapper = new ObjectMapper();
        String matchmakerBase = uri.matchmaker();
        this.joinLobbyUrl = matchmakerBase + JOIN_LOBBY_PATH;
        this.quitLobbyUrl = matchmakerBase + QUIT_LOBBY_PATH;
        this.gameServerUrl = matchmakerBase + GAME_SERVER_PATH;
    }

    @Override
    public boolean queue(String mapName, String userToken) throws Exception {
        clearMatchmakingData();
        var payload = objectMapper.writeValueAsString(new JoinLobbyRequest(userToken, mapName));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(joinLobbyUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) return false;
        LobbyResponse lobbyResponse = objectMapper.readValue(response.body(), LobbyResponse.class);
        if (lobbyResponse.isMatchFound()) {
            this.currentMatchId = lobbyResponse.id();
        } else {
            this.currentLobbyId = lobbyResponse.id();
        }
        return this.currentLobbyId != null || this.currentMatchId != null;
    }

    @Override
    public boolean cancelQueue(String userToken) throws Exception {
        if (this.currentLobbyId == null) return false;
        var payload = objectMapper.writeValueAsString(new QuitLobbyRequest(userToken, this.currentLobbyId));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(quitLobbyUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        boolean success = response.statusCode() == 200;
        if (success) clearMatchmakingData();
        return success;
    }

    @Override
    public boolean checkQueueStatus(String userToken) throws Exception {
        if (this.currentLobbyId == null) {
            throw new IllegalStateException("Cannot check status: No active queue");
        }
        if (this.currentMatchId != null) return true;
        var payload = objectMapper.writeValueAsString(new GameServerRequest(userToken, this.currentLobbyId));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(gameServerUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200 && response.body() != null && !response.body().isBlank()) {
            LobbyResponse pollResponse = objectMapper.readValue(response.body(), LobbyResponse.class);
            if (pollResponse.isMatchFound()) {
                this.currentMatchId = pollResponse.id();
                return true;
            }
        }
        return false;
    }

    // TODO likely needs to use a different endpoint
    @Override
    public ConnectionParameters getServerParameters(String userToken) throws Exception {
        if (this.currentMatchId == null) {
            throw new IllegalStateException("Cannot fetch server parameters: Match is not ready yet.");
        }
        var payload = objectMapper.writeValueAsString(new GameServerRequest(userToken, this.currentMatchId));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(gameServerUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new RuntimeException("Could not fetch game server parameters: " + response.body());
        }
        return objectMapper.readValue(response.body(), ConnectionParameters.class);
    }

    @Override
    public String getCurrentLobbyId() {
        return this.currentLobbyId;
    }

    @Override
    public String getCurrentMatchId() {
        return this.currentMatchId;
    }

    @Override
    public void clearMatchmakingData() {
        this.currentLobbyId = null;
        this.currentMatchId = null;
    }

    private record JoinLobbyRequest(
            @JsonProperty("token") String token,
            @JsonProperty("map") String map
    ) {}

    private record QuitLobbyRequest(
            @JsonProperty("token") String token,
            @JsonProperty("lobbyId") String lobbyId
    ) {}

    private record GameServerRequest(
            @JsonProperty("token") String token,
            @JsonProperty("lobbyId") String lobbyId
    ) {}

    public record LobbyResponse(
            @JsonProperty("type") LobbyTypeResponse typeResponse,
            @JsonProperty("id") String id
    ) {
        public boolean isMatchFound() {
            return typeResponse == LobbyTypeResponse.FOUND && id != null && !id.isBlank();
        }
    }

    public enum LobbyTypeResponse {
        FOUND,
        WAITING
    }
}