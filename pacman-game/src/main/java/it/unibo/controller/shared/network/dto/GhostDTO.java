package it.unibo.controller.shared.network.dto;

public record GhostDTO(
        boolean isAlive,
        long lastTimeDead,
        long lastTimeDirectionWasChanged,
        String currentDirection,
        String desiredDirection,
        int targetTileRow,
        int targetTileCol,
        int currentTileRow,
        int currentTileCol,
        int x,
        int y
) {}