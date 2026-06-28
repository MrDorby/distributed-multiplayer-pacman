package it.unibo.controller.network.dto;

public record DotDTO(
        boolean isSpecial,
        long lastTimeEaten,
        int x,
        int y,
        boolean isAlive,
        int row,
        int col
) {}