package it.unibo.controller.server.persistence.dto;

import it.unibo.controller.shared.network.dto.GameContextDTO;

public record MatchSnapshot(
        String matchId,
        long timestamp,
        GameContextDTO context
) {}