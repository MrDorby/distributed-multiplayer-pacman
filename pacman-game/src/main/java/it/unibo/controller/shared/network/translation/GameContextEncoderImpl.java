package it.unibo.controller.shared.network.translation;

import it.unibo.controller.shared.network.dto.*;
import it.unibo.model.common.Direction;
import it.unibo.model.common.MatrixCoordinates;
import it.unibo.model.common.Vector2D;
import it.unibo.model.entities.*;
import it.unibo.model.game.GameContext;
import it.unibo.model.game.GameState;

import static it.unibo.controller.shared.utils.ReflectionUtils.getField;

/**
 * Implementation of {@link GameContextEncoder} that uses reflection
 * to access private fields not exposed through the public API.
 */
public class GameContextEncoderImpl implements GameContextEncoder {
    @Override
    public GameContextDTO encode(GameContext context) {
        return new GameContextDTO(
                context.getMap().getName(),
                context.getTick(),
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
            MatrixCoordinates currentMatrixCoordinates = pacman.getMatrixCoordinates();
            Vector2D position = pacman.getPosition();
            Object movementManager = getField(pacman, "movementManager");
            Direction desiredDirection = (Direction) getField(movementManager, "desiredDirection");
            MatrixCoordinates targetMatrixCoordinates = (MatrixCoordinates) getField(movementManager, "targetMatrixCoordinates");
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
                    desiredDirection.name(),
                    targetMatrixCoordinates.row(),
                    targetMatrixCoordinates.column(),
                    currentMatrixCoordinates.row(),
                    currentMatrixCoordinates.column(),
                    position.x(),
                    position.y()
            );
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to read pacman fields via reflection", e);
        }
    }

    private static GhostDTO encode(Ghost ghost) {
        try {
            MatrixCoordinates currentMatrixCoordinates = ghost.getMatrixCoordinates();
            Vector2D position = ghost.getPosition();
            Object movementManager = getField(ghost, "movementManager");
            Direction desiredDirection = (Direction) getField(movementManager, "desiredDirection");
            MatrixCoordinates targetMatrixCoordinates = (MatrixCoordinates) getField(movementManager, "targetMatrixCoordinates");
            return new GhostDTO(
                    ghost.isAlive(),
                    (long) getField(ghost, "lastTimeDead"),
                    (long) getField(ghost, "lastTimeDirectionWasChanged"),
                    ghost.getDirection().name(),
                    desiredDirection.name(),
                    targetMatrixCoordinates.row(),
                    targetMatrixCoordinates.column(),
                    currentMatrixCoordinates.row(),
                    currentMatrixCoordinates.column(),
                    position.x(),
                    position.y()
            );
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to read ghost fields via reflection", e);
        }
    }

    private static DotDTO encode(MatrixCoordinates matrixCoordinates, Dot dot) {
        try {
            Vector2D position = dot.getPosition();
            return new DotDTO(
                    dot.isSpecial(),
                    dot.isAlive(),
                    (long) getField(dot, "lastTimeEaten"),
                    matrixCoordinates.row(),
                    matrixCoordinates.column(),
                    position.x(),
                    position.y()
            );
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to read dot fields via reflection", e);
        }
    }
}