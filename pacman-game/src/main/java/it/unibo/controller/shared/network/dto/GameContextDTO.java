package it.unibo.controller.shared.network.dto;

import java.util.List;

public record GameContextDTO(
        String mapName,
        long tick,
        GameStateDTO gameState,
        List<PacmanDTO> pacmans,
        List<GhostDTO> ghosts,
        List<DotDTO> dots
) {}