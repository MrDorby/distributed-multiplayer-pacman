package it.unibo.controller.network.dto;

import it.unibo.model.common.MatrixCoordinates;
import it.unibo.model.common.Vector2D;
import it.unibo.model.entities.Dot;
import it.unibo.model.entities.Ghost;
import it.unibo.model.entities.Pacman;
import it.unibo.model.game.GameContext;
import it.unibo.model.game.GameState;

public class GameContextMapper {
    public static GameContextDTO toDTO(GameContext context) {
        return new GameContextDTO(
                context.getMap().getName(),
                toDTO(context.getGameState()),
                context.getPacmans().stream().map(GameContextMapper::toDTO).toList(),
                context.getGhosts().stream().map(GameContextMapper::toDTO).toList(),
                context.getDotsMap().entrySet().stream()
                        .map(e -> toDTO(e.getKey(), e.getValue())).toList()
        );
    }

    private static GameStateDTO toDTO(GameState state) {
        return new GameStateDTO(
                state.getLeaderboard(),
                state.getTimeLeftInMillis(),
                state.isGameOver(),
                state.getWinnerId()
        );
    }

    private static PacmanDTO toDTO(Pacman pacman) {
        MatrixCoordinates coords = pacman.getMatrixCoordinates();
        Vector2D position = pacman.getPosition();
        return new PacmanDTO(
                pacman.getId(),
                pacman.getScore(),
                pacman.getLives(),
                pacman.isPlayer(),
                pacman.canEatGhost(),
                pacman.getSpecialDotEatenTime(),
                pacman.isInvincible(),
                pacman.getWhenInvincible(),
                pacman.getDirection().name(),
                coords.row(),
                coords.column(),
                position.x(),
                position.y(),
                pacman.getLastTimeDirectionChanged(),
                pacman.isAlive()
        );
    }

    private static GhostDTO toDTO(Ghost ghost) {
        MatrixCoordinates coords = ghost.getMatrixCoordinates();
        Vector2D position = ghost.getPosition();
        return new GhostDTO(
                ghost.getDirection().name(),
                coords.row(),
                coords.column(),
                position.x(),
                position.y(),
                ghost.isAlive(),
                ghost.getLastTimeDead(),
                ghost.getLastTimeDirectionChanged()
        );
    }

    private static DotDTO toDTO(MatrixCoordinates coords, Dot dot) {
        Vector2D position = dot.getPosition();
        return new DotDTO(
                dot.isSpecial(),
                dot.getLastTimeEaten(),
                position.x(),
                position.y(),
                dot.isAlive(),
                coords.row(),
                coords.column()
        );
    }
}