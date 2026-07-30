package it.unibo.gameservermanager.instantiator;

import it.unibo.gameservermanager.dto.GameServerInfo;
import it.unibo.gameservermanager.dto.GameServerInitParameters;
import it.unibo.gameservermanager.dto.GameServerStatus;
import it.unibo.gameservermanager.instantiator.exceptions.GameServerCheckException;
import it.unibo.gameservermanager.instantiator.exceptions.GameServerInstantiationException;

/**
 * Component responsible for instantiating new GameServers and monitoring their status.
 */
public interface GameServerInstantiator {
    /**
     * Instantiates a new GameServer based on the provided initialization parameters.
     * @param initParameters the initialization parameters of the new GameServer.
     * @return the information about the newly instantiated GameServer, if the action was successful. An empty Optional
     * otherwise.
     * @throws GameServerInstantiationException if the instantiation operation is not successful.
     */
    GameServerInfo instantiateNormalGameServer(GameServerInitParameters initParameters);

    /**
     * Instantiates a recovery GameServer for the given match.
     * @param matchID the unique ID of the match.
     * @return the information about the newly instantiated GameServer, if the action was successful. An empty Optional
     * otherwise.
     * @throws GameServerInstantiationException if the instantiation operation is not successful.
     */
    GameServerInfo instantiateRecoveryGameServer(String matchID);

    /**
     * Returns the current status of the specified GameServer.
     * @param serverName the GameServer's unique name.
     * @return the current status of the GameServer.
     * @throws GameServerCheckException if the check is not successful.
     */
    GameServerStatus getGameServerStatus(String serverName);
}
