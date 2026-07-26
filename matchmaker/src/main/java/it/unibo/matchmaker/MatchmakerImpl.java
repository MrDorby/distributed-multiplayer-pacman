package it.unibo.matchmaker;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.auth0.jwt.JWT;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.unibo.MatchmakerDetailsService;
import it.unibo.dto.GameServerRequest;
import it.unibo.dto.JoinLobbyRequest;
import it.unibo.dto.JoinLobbyResponse;
import it.unibo.dto.QuitLobbyRequest;
import it.unibo.mongodb.MatchInfoMongoDB;

/**
 * MatchmakerImpl
 * <p>
 * Service that manages the lobbies and lets users find a match.
 */
@RestController
@RequestMapping(value = "/matchmaker")
public class MatchmakerImpl implements Matchmaker{

    // TODO: add to the env.
    private static final String AUTHENTICATOR_REQUEST = "http://localhost:8080/auth/token";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final MatchmakerDetailsService matchmakerDetailsService;

    public MatchmakerImpl(MatchmakerDetailsService matchmakerDetailsService) {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.matchmakerDetailsService = matchmakerDetailsService;
    }

    @Override
    @PostMapping(value = "/join_lobby", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> joinLobby(@RequestBody JoinLobbyRequest join) {
        try {
            String token = join.token();
            String username = checkTokenPermission(token);
            JoinLobbyResponse response = this.matchmakerDetailsService.checkForLobby(username, join.map());
            String res = this.objectMapper.writeValueAsString(response);
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Override
    @PostMapping(value = "/quit_lobby", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> quitLobby(@RequestBody QuitLobbyRequest quit) {
        try {
            String token = quit.token();
            String username = checkTokenPermission(token);
            //LobbyInfoMongoDB lobby = this.matchmakerDetailsService.getLobby(quit.lobbyId());
            this.matchmakerDetailsService.deleteUserByLobbyId(quit.lobbyId(), username);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /* Checks if the user is permitted to do the differents requests by controlling the token received. */
    private String checkTokenPermission(String token) throws Exception { 
        HttpRequest httpTokenRequest = HttpRequest.newBuilder()
                                    .uri(URI.create(AUTHENTICATOR_REQUEST))
                                    .header("Content-Type", "application/json")
                                    .POST(BodyPublishers.ofString(token))
                                    .build();
        
        try {
            HttpResponse<String> tokenResponse = httpClient.send(httpTokenRequest, HttpResponse.BodyHandlers.ofString());
            if (tokenResponse.statusCode() == 200) {
                return JWT.decode(token).getClaim("username").asString();
            }
            throw new Exception(tokenResponse.body());
        } catch (IOException | InterruptedException e) {
            throw new Exception(e.getMessage());
        }
    }

    // TODO: Change
    @Override
    @PostMapping(value = "/game_server", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getGameServer(@RequestBody GameServerRequest info) {
        try {
            String username = checkTokenPermission(info.token());
            MatchInfoMongoDB match = this.matchmakerDetailsService.getMatch(info.matchId()); // TODO: to change.
            String result = this.objectMapper.writeValueAsString(match.getGameServerSocket());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
