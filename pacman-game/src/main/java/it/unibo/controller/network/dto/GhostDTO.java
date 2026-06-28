package it.unibo.controller.network.dto;

public record GhostDTO(
        String currentDirection,
        // String desiredDirection,
        int tileRow,
        int tileCol,
        double x,
        double y,
        boolean isAlive,
        long lastTimeDead,
        long lastTimeDirectionWasChanged
) {}