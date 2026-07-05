package it.unibo.controller.server.health;

public record GameServerStatus(
        boolean gameStarted,
        int playersJoined,
        int playersRequired,
        boolean engineRunning
) {}