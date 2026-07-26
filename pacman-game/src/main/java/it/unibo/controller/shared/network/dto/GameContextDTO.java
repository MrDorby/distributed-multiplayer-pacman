package it.unibo.controller.shared.network.dto;

import java.util.List;

/**
 * GameContextDTO
 * @param mapName
 * @param tick
 * @param gameState
 * @param pacmans
 * @param ghosts
 * @param dots
 */
public record GameContextDTO(
        String mapName,
        long tick,
        GameStateDTO gameState,
        List<PacmanDTO> pacmans,
        List<GhostDTO> ghosts,
        List<DotDTO> dots
) {}