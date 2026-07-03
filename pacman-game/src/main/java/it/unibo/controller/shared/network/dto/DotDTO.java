package it.unibo.controller.shared.network.dto;

public record DotDTO(
        boolean isSpecial,
        boolean isAlive,
        long lastTimeEaten,
        int currentTileRow,
        int currentTileCol,
        int x,
        int y
) {}