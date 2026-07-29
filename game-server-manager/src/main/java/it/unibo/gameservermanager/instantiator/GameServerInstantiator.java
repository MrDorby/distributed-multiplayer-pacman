package it.unibo.gameservermanager.instantiator;

import it.unibo.gameservermanager.dto.GameServerInfo;
import it.unibo.gameservermanager.dto.GameServerInitParameters;
import it.unibo.gameservermanager.dto.GameServerStatus;

/**
 * Component responsible for instantiating new GameServers and monitoring their status.
 */
public interface GameServerInstantiator {
    GameServerInfo instantiateGameServer(GameServerInitParameters initParameters, boolean recovery);

    GameServerStatus getGameServerStatus(String serverName);
}
