package it.unibo.controller.server.persistence.dto;

import it.unibo.controller.shared.network.dto.GameContextDTO;

/**
 * MatchSnapshot
 * @param matchId
 * @param timestamp
 * @param context
 */
public record MatchSnapshot(
        String matchId,
        long timestamp,
        GameContextDTO context
) {}