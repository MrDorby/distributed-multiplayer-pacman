package it.unibo.controller.shared.network.dto;

public record DotDTO(
        boolean isSpecial,
        boolean isAlive,
        long lastTimeEaten,
        int tileRow,
        int tileCol,
        int x,
        int y
) {}