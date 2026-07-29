package it.unibo.gameservermanager.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nullable;

/**
 * The results of a liveness check on a GameServer.
 * @param status the current status of the GameServer.
 * @param serverInfo information about the newly instantiated recovery GameServer.
 *                   Present only if {@code status} is {@code UNHEALTHY} and a recovery GameServer has been instantiated,
 *                   null otherwise.
 */
public record GameServerCheckResults(
        @JsonProperty("status") GameServerStatus status,
        @JsonProperty("server-info") @Nullable GameServerInfo serverInfo) {
    public GameServerCheckResults {
        if (!status.equals(GameServerStatus.UNHEALTHY) && serverInfo != null) {
            throw new IllegalArgumentException("Can't specify GameServer information unless the status is "
                    + GameServerStatus.UNHEALTHY);
        }
    }
}
