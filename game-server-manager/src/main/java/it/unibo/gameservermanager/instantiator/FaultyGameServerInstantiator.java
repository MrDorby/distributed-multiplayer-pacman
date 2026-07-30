package it.unibo.gameservermanager.instantiator;

import it.unibo.gameservermanager.dto.GameServerInfo;
import it.unibo.gameservermanager.dto.GameServerInitParameters;
import it.unibo.gameservermanager.dto.GameServerStatus;
import it.unibo.gameservermanager.instantiator.exceptions.GameServerCheckException;
import it.unibo.gameservermanager.instantiator.exceptions.GameServerInstantiationException;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component("faultyInstantiator")
public class FaultyGameServerInstantiator implements GameServerInstantiator {
    @Override
    public GameServerInfo instantiateNormalGameServer(GameServerInitParameters initParameters) {
        throw new GameServerInstantiationException("Could not instantiate normal GameServer.");
    }

    @Override
    public GameServerInfo instantiateRecoveryGameServer(String matchID) {
        throw new GameServerInstantiationException("Could not instantiate recovery GameServer.");
    }

    @Override
    public GameServerStatus getGameServerStatus(String serverName) {
        Random random = new Random();
        int result = random.nextInt(2);
        if (result > 0) throw new GameServerCheckException("Could not check GameServer status.");
        return GameServerStatus.HEALTHY;
    }
}
