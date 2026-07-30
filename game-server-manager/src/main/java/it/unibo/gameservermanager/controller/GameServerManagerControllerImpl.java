package it.unibo.gameservermanager.controller;

import it.unibo.gameservermanager.dto.*;
import it.unibo.gameservermanager.instantiator.GameServerInstantiator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.databind.ObjectMapper;

import java.util.Optional;

@RestController
@RequestMapping("/gameservermanager")
public class GameServerManagerControllerImpl implements GameServerManagerController {
    private final Logger logger = LoggerFactory.getLogger(GameServerManagerControllerImpl.class);
    private final GameServerInstantiator instantiator;
    private final ObjectMapper objectMapper;
    private final long minTimeLeft;
    //private final String matchmakerURI;

    // TODO: test that it works
    public GameServerManagerControllerImpl(@Qualifier("dummyInstantiator") GameServerInstantiator instantiator) {
        //this.matchmakerURI = System.getenv("MATCHMAKER_URI");
//        try {
//            ApiClient kubeClient = ClientBuilder.cluster().build();
//            Configuration.setDefaultApiClient(kubeClient);
//        } catch (IOException e) {
//            logger.error("Error during instantiation of the Kubernetes client: {}", e.getMessage());
//        }
        this.instantiator = instantiator;
        this.objectMapper = new ObjectMapper();
        this.minTimeLeft = Long.parseLong(System.getenv("MIN_TIME_LEFT")); // TODO: document this environment variable in Main
    }

    @Override
    @PostMapping("/gameserver/create")
    public ResponseEntity<String> createGameServer(@RequestBody GameServerInitParameters initParameters) {
        GameServerInfo gameServerInfo = this.instantiator.instantiateNormalGameServer(initParameters);
        return ResponseEntity.ok(this.objectMapper.writeValueAsString(gameServerInfo));
//        return gameServerInfo.map(gsi -> ResponseEntity.ok(this.objectMapper.writeValueAsString(gsi)))
//                .orElse(ResponseEntity.internalServerError().body("Error with the instantiation of the GameServer."));
    }

    @Override
    @PostMapping("/gameserver/check")
    public ResponseEntity<String> checkGameServer(@RequestBody CheckGameServerRequest checkRequest) {
        GameServerCheckResults checkResults;
        GameServerStatus gameServerStatus = this.instantiator.getGameServerStatus(checkRequest.serverName());
        if (gameServerStatus.equals(GameServerStatus.UNHEALTHY) && checkRequest.timeLeft() >= minTimeLeft) {
            GameServerInfo recoveryGameServerInfo = this.instantiator.instantiateRecoveryGameServer(checkRequest.matchID());
            checkResults = new GameServerCheckResults(gameServerStatus, recoveryGameServerInfo);
//            Optional<GameServerInfo> recoveryGameServerInfo = this.instantiator.instantiateRecoveryGameServer(checkRequest.matchID());
//            return recoveryGameServerInfo.map(gsi -> ResponseEntity.ok(this.objectMapper.writeValueAsString(gsi)))
//                    .orElse(ResponseEntity.internalServerError().body("Error with the instantiation of the recovery GameServer."));
        } else {
            checkResults = new GameServerCheckResults(gameServerStatus, null);
        }
        return ResponseEntity.ok(this.objectMapper.writeValueAsString(checkResults));
    }

    @Override
    @PostMapping("/match/ended")
    public ResponseEntity<String> matchEnded(@RequestBody String matchID) {
        return null;
    }
}
