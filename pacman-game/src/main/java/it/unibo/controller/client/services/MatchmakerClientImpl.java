package it.unibo.controller.client.services;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.unibo.controller.client.common.ConnectionParameters;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

public class MatchmakerClientImpl implements MatchmakerClient {
    private static final String JOIN_LOBBY_PATH = "/join_lobby";
    private static final String QUIT_LOBBY_PATH = "/quit_lobby";
    private static final String GAME_SERVER_PATH = "/game_server";
    private static final String QUIT_MATCH_PATH = "/quit_match";

    private final String joinLobbyUrl;
    private final String quitLobbyUrl;
    private final String gameServerUrl;
    private final String quitMatchUrl;

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
        this.quitMatchUrl = matchmakerBase + QUIT_MATCH_PATH;
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
        JoinLobbyResponse lobbyResponse = objectMapper.readValue(response.body(), JoinLobbyResponse.class);
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
        var payload = objectMapper.writeValueAsString(new GameServerRequest(userToken, null));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(joinLobbyUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200 && response.body() != null && !response.body().isBlank()) {
            JoinLobbyResponse joinLobbyResponse = objectMapper.readValue(response.body(), JoinLobbyResponse.class);
            if (joinLobbyResponse.isMatchFound()) {
                this.currentMatchId = joinLobbyResponse.id();
                return true;
            }
        }
        return false;
    }

    @Override
    public Optional<ConnectionParameters> getServerParametersByMatchId(String matchId, String userToken) throws Exception {
        if (matchId == null || matchId.isBlank()) {
            throw new IllegalArgumentException("matchId cannot be null or blank");
        }
        return fetchServerParameters(userToken, matchId);
    }

    @Override
    public Optional<ConnectionParameters> getServerParametersByToken(String userToken) throws Exception {
        return fetchServerParameters(userToken, null);
    }

    private Optional<ConnectionParameters> fetchServerParameters(String userToken, String matchId) throws Exception {
        var payload = objectMapper.writeValueAsString(new GameServerRequest(userToken, matchId));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(gameServerUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200 || response.body() == null || response.body().isBlank()) {
            return Optional.empty();
        }
        GameServerResponse serverResponse = objectMapper.readValue(response.body(), GameServerResponse.class);
        if (serverResponse == null || serverResponse.parameters == null) {
            return Optional.empty();
        }
        if (serverResponse.matchId() != null) {
            this.currentMatchId = serverResponse.matchId();
        }
        ServerParameters parameters = serverResponse.parameters;
        return Optional.of(new ConnectionParameters(parameters.host(), parameters.tcpPort(), parameters.udpPort()));
    }

    @Override
    public boolean quitMatch(String userToken) throws Exception {
        var payload = objectMapper.writeValueAsString(new RemoveRequest(userToken, this.currentMatchId));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(quitMatchUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        boolean success = response.statusCode() == 200;
        if (success) clearMatchmakingData();
        return success;
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
            @JsonProperty("lobby") String lobbyId
    ) {}

    private record GameServerRequest(
            @JsonProperty("token") String token,
            @JsonProperty("match") String matchId
    ) {}

    private record RemoveRequest(
            @JsonProperty("token") String token,
            @JsonProperty("match") String matchId
    ) {}

    private record ServerParameters(
            @JsonProperty("host") String host,
            @JsonProperty("tcpPort") int tcpPort,
            @JsonProperty("udpPort") int udpPort
    ) {}

    private record GameServerResponse(
            @JsonProperty("matchId") String matchId,
            @JsonProperty("serverParameters") ServerParameters parameters
    ) {}

    public record JoinLobbyResponse(
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