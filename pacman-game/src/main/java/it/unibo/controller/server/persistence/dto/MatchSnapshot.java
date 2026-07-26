package it.unibo.controller.server.persistence.dto;

import it.unibo.controller.shared.network.dto.GameContextDTO;

import java.util.List;

/**
 * MatchSnapshot
 * @param matchId
 * @param timestamp
 * @param context
 */
public record MatchSnapshot(
        String matchId,
        long timestamp,
        List<String> activePlayers,
        GameContextDTO context
) {}