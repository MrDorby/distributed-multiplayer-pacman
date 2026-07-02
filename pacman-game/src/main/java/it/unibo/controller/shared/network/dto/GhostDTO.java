package it.unibo.controller.shared.network.dto;

public record GhostDTO(
        boolean isAlive,
        long lastTimeDead,
        long lastTimeDirectionWasChanged,
        String currentDirection, // Unused for now when recreating a GhostImpl
        // String desiredDirection,
        int tileRow,
        int tileCol,
        int x,
        int y
) {}