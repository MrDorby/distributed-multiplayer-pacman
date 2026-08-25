package it.unibo.matchmaker;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.util.Objects;
import java.net.http.HttpResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
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
import it.unibo.dto.GameServerResponse;
import it.unibo.dto.GameServerInfo;
import it.unibo.dto.JoinLobbyRequest;
import it.unibo.dto.JoinLobbyResponse;
import it.unibo.dto.QuitLobbyRequest;
import it.unibo.dto.RemoveFromMatchRequest;
import it.unibo.mongodb.MatchInfoMongoDB;
import it.unibo.mongodb.ServerParameters;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * MatchmakerImpl
 * <p>
 * Service that manages the lobbies and lets users find a match.
 */
@RestController
@RequestMapping(value = "/matchmaker")
public class MatchmakerImpl implements Matchmaker{

    /**
     * DTO containing a token.
     * @param token String version of the token.
    */
    private record TokenDTO(
        @JsonProperty("token") String token) {
    }

    private static final String AUTHENTICATOR_ENV = "AUTHENTICATOR";
    private static final String AUTHENTICATOR_REQUEST = System.getenv().get(AUTHENTICATOR_ENV) + "/auth/token";
    private static final Logger LOGGER = LoggerFactory.getLogger(MatchmakerImpl.class);

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
        TokenDTO tokenDTO = new TokenDTO(token);
        String request = this.objectMapper.writeValueAsString(tokenDTO);
        HttpRequest httpTokenRequest = HttpRequest.newBuilder()
                                    .uri(URI.create(AUTHENTICATOR_REQUEST))
                                    .header("Content-Type", "application/json")
                                    .POST(BodyPublishers.ofString(request))
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
    
    @Override
    @PostMapping(value = "/game_server", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getGameServer(@RequestBody GameServerRequest info) {
        try {
            String username = checkTokenPermission(info.token());
            MatchInfoMongoDB match = getMatch(info.matchId(), username);
            GameServerInfo gameServerInfo = this.matchmakerDetailsService.checkGameServerAvailability(match);
            ServerParameters serverParameters = match.getServerParameters();
            if (Objects.nonNull(gameServerInfo)) {
                serverParameters = new ServerParameters(gameServerInfo.ip(), gameServerInfo.tcpPort(), gameServerInfo.udpPort());
                this.matchmakerDetailsService.setNewGameServerInfo(match, gameServerInfo);
            }
            GameServerResponse response = new GameServerResponse(info.matchId(), serverParameters);
            LOGGER.debug("GAME SERVER INFOS {}", response);
            String result = this.objectMapper.writeValueAsString(response);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatusCode.valueOf(500)).body(e.getMessage());
        }
    }

    /* Retrieves the match from the specified matchId or by Token. */
    private MatchInfoMongoDB getMatch(String matchId, String username) throws Exception {
        if (matchId != null && !matchId.isBlank()) {
            return this.matchmakerDetailsService.getMatchById(matchId);
        }
        return this.matchmakerDetailsService.getMatchByToken(username);
    }

    @Override
    @PostMapping(value = "/delete_match", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void deleteMatchOnDB(@RequestBody String matchId) {
        this.matchmakerDetailsService.deleteMatch(matchId);
    }

    @Override
    @PostMapping(value = "/quit_match", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> removePlayerFromMatch(@RequestBody RemoveFromMatchRequest remove) {
        LOGGER.debug("{} QUIT MATCH MESSAGGE: {}", System.lineSeparator(), remove);
        try {
            String username = checkTokenPermission(remove.token());
            this.matchmakerDetailsService.deleteUserFromMatch(remove.matchId(), username);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
