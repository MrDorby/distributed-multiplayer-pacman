package it.unibo.gameservermanager.controller;

import it.unibo.gameservermanager.controller.exceptions.MatchmakerCommunicationException;
import it.unibo.gameservermanager.dto.*;
import it.unibo.gameservermanager.instantiator.GameServerInstantiator;
import it.unibo.gameservermanager.utils.UriValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@RestController
@RequestMapping("/gameservermanager")
public class GameServerManagerControllerImpl implements GameServerManagerController {
    private static final long DEFAULT_MIN_TIME_LEFT = 5000;
    private static final String MATCHMAKER_URI_ENV_NAME = "MATCHMAKER_URI";

    private final Logger logger = LoggerFactory.getLogger(GameServerManagerControllerImpl.class);
    private final GameServerInstantiator instantiator;
    private final ObjectMapper objectMapper;
    private final long minTimeLeft;
    private final URI matchmakerURI;
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public GameServerManagerControllerImpl(GameServerInstantiator instantiator) {
        String matchmakerURIString = System.getenv(MATCHMAKER_URI_ENV_NAME);
        if (matchmakerURIString == null) {
            throw new IllegalStateException("Environment variable " + MATCHMAKER_URI_ENV_NAME + " must be set.");
        }
        this.matchmakerURI = UriValidator.validateURI(matchmakerURIString, MATCHMAKER_URI_ENV_NAME);
        this.instantiator = instantiator;
        this.objectMapper = new ObjectMapper();
        long minTimeLeft;
        try {
            minTimeLeft = Long.parseLong(System.getenv("MIN_TIME_LEFT"));
        } catch (Exception e) {
            this.logger.info("MIN_TIME_LEFT has not been specified or has an invalid format. This run will use the default value: {}", DEFAULT_MIN_TIME_LEFT);
            minTimeLeft = DEFAULT_MIN_TIME_LEFT;
        }
        this.minTimeLeft = minTimeLeft;
    }

    @Override
    @PostMapping(value = "/gameserver/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createGameServer(@RequestBody GameServerInitParameters initParameters) {
        GameServerInfo gameServerInfo = this.instantiator.instantiateNormalGameServer(initParameters);
        return ResponseEntity.ok(this.objectMapper.writeValueAsString(gameServerInfo));
    }

    @Override
    @PostMapping(value = "/gameserver/check", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> checkGameServer(@RequestBody CheckGameServerRequest checkRequest) {
        GameServerCheckResults checkResults;
        GameServerStatus gameServerStatus = this.instantiator.getGameServerStatus(checkRequest.serverName());
        if (gameServerStatus.equals(GameServerStatus.UNHEALTHY) && checkRequest.timeLeft() >= minTimeLeft) {
            GameServerInfo recoveryGameServerInfo = this.instantiator.instantiateRecoveryGameServer(checkRequest.matchID());
            checkResults = new GameServerCheckResults(gameServerStatus, recoveryGameServerInfo);
        } else {
            checkResults = new GameServerCheckResults(gameServerStatus, null);
        }
        return ResponseEntity.ok(this.objectMapper.writeValueAsString(checkResults));
    }

    @Override
    @PostMapping("/match/ended")
    public ResponseEntity<String> matchEnded(@RequestBody String matchID) {
        final String relativeURIString = "/matchmaker/delete_match";
        this.logger.debug("Sending request at URI: {}", this.matchmakerURI.resolve(relativeURIString));
        final HttpRequest matchEndedRequest = HttpRequest.newBuilder()
                .uri(this.matchmakerURI.resolve(relativeURIString))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(matchID))
                .build();
        try {
            HttpResponse<String> response = this.httpClient.send(matchEndedRequest, HttpResponse.BodyHandlers.ofString());
            return ResponseEntity
                    .status(response.statusCode())
                    .body("Matchmaker's response: " + response.body());
        } catch (Exception e) {
            throw new MatchmakerCommunicationException(e);
        }
    }
}
