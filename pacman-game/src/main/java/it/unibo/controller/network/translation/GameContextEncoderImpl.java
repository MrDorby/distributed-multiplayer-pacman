package it.unibo.controller.network.translation;

import it.unibo.controller.network.dto.*;
import it.unibo.model.common.MatrixCoordinates;
import it.unibo.model.common.Vector2D;
import it.unibo.model.entities.*;
import it.unibo.model.game.GameContext;
import it.unibo.model.game.GameState;

import java.lang.reflect.Field;

/**
 * Implementation of {@link GameContextEncoder} that uses reflection
 * to access private fields not exposed through the public API.
 */
public class GameContextEncoderImpl implements GameContextEncoder {
    @Override
    public GameContextDTO encode(GameContext context) {
        return new GameContextDTO(
                context.getMap().getName(),
                encode(context.getGameState()),
                context.getPacmans().stream().map(GameContextEncoderImpl::encode).toList(),
                context.getGhosts().stream().map(GameContextEncoderImpl::encode).toList(),
                context.getDotsMap().entrySet().stream().map(e -> encode(e.getKey(), e.getValue())).toList()
        );
    }

    private static GameStateDTO encode(GameState state) {
        return new GameStateDTO(
                state.getLeaderboard(),
                state.getTimeLeftInMillis(),
                state.isGameOver(),
                state.getWinnerId()
        );
    }

    private static PacmanDTO encode(Pacman pacman) {
        try {
            MatrixCoordinates coords = pacman.getMatrixCoordinates();
            Vector2D position = pacman.getPosition();
            return new PacmanDTO(
                    pacman.getId(),
                    pacman.getScore(),
                    pacman.getLives(),
                    pacman.isPlayer(),
                    pacman.isAlive(),
                    pacman.canEatGhost(),
                    pacman.isInvincible(),
                    (long) getField(pacman, "lastTimeSpecialDotWasEaten"),
                    (long) getField(pacman, "lastTimeBecameInvincible"),
                    (long) getField(pacman, "lastTimeDirectionWasChanged"),
                    pacman.getDirection().name(),
                    coords.row(),
                    coords.column(),
                    position.x(),
                    position.y()
            );
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to read pacman fields via reflection", e);
        }
    }

    private static GhostDTO encode(Ghost ghost) {
        try {
            MatrixCoordinates coords = ghost.getMatrixCoordinates();
            Vector2D position = ghost.getPosition();
            return new GhostDTO(
                    ghost.isAlive(),
                    (long) getField(ghost, "lastTimeDead"),
                    (long) getField(ghost, "lastTimeDirectionWasChanged"),
                    ghost.getDirection().name(),
                    coords.row(),
                    coords.column(),
                    position.x(),
                    position.y()
            );
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to read ghost fields via reflection", e);
        }
    }

    private static DotDTO encode(MatrixCoordinates coords, Dot dot) {
        try {
            Vector2D position = dot.getPosition();
            return new DotDTO(
                    dot.isSpecial(),
                    dot.isAlive(),
                    (long) getField(dot, "lastTimeEaten"),
                    coords.row(),
                    coords.column(),
                    position.x(),
                    position.y()
            );
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to read dot fields via reflection", e);
        }
    }

    private static Object getField(Object instance, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Field field = instance.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(instance);
    }
}