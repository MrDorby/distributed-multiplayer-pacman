package it.unibo.gameservermanager.instantiator;

import it.unibo.gameservermanager.dto.GameServerInfo;
import it.unibo.gameservermanager.dto.GameServerInitParameters;
import it.unibo.gameservermanager.dto.GameServerStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * A dummy Instantiator that does nothing but logging operations, and returns fixed results for each method
 * (except for {@code getGameServerStatus()}, which returns a random result).
 */
@Component("dummyInstantiator")
public class DummyGameServerInstantiator implements GameServerInstantiator {
    private final Logger logger = LoggerFactory.getLogger(DummyGameServerInstantiator.class);

    @Override
    public GameServerInfo instantiateNormalGameServer(GameServerInitParameters initParameters) {
        this.logger.debug("Received GameServerInitParameters: {}", initParameters);
        return instantiateGameServer();
    }

    @Override
    public GameServerInfo instantiateRecoveryGameServer(String matchID) {
        this.logger.debug("Received recovery GameServer matchID: {}", matchID);
        return instantiateGameServer();
    }

    private GameServerInfo instantiateGameServer() {
        return new GameServerInfo("pacman-server", "127.0.0.1", 7777, 7777);
    }

    @Override
    public GameServerStatus getGameServerStatus(String serverName) {
        this.logger.debug("Checking status for server \"{}\"", serverName);
        Random random = new Random();
        int result = random.nextInt(3);
        return switch (result) {
            case 0 -> GameServerStatus.HEALTHY;
            case 1 -> GameServerStatus.UNHEALTHY;
            case 2 -> GameServerStatus.NOT_FOUND;
            default -> throw new IllegalArgumentException("Invalid value.");
        };
    }
}
