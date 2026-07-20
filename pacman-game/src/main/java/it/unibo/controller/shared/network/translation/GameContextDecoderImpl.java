package it.unibo.controller.shared.network.translation;

import it.unibo.controller.shared.network.dto.DotDTO;
import it.unibo.controller.shared.network.dto.GameContextDTO;
import it.unibo.controller.shared.network.dto.GhostDTO;
import it.unibo.controller.shared.network.dto.PacmanDTO;
import it.unibo.model.common.Direction;
import it.unibo.model.common.MatrixCoordinates;
import it.unibo.model.common.Vector2D;
import it.unibo.model.entities.*;
import it.unibo.model.game.GameContext;
import it.unibo.model.game.GameContextImpl;
import it.unibo.model.map.FourPlayersGameMapFactory;
import it.unibo.model.map.GameMap;
import it.unibo.model.map.Tile;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static it.unibo.controller.shared.utils.ReflectionUtils.getField;
import static it.unibo.controller.shared.utils.ReflectionUtils.setField;

/**
 * Decodes a {@link GameContextDTO} back into a {@link GameContext}.
 * The game map is loaded once and cached for subsequent decode calls.
 */
public class GameContextDecoderImpl implements GameContextDecoder {
    private final GameEntityFactory entityFactory;
    private GameMap cachedMap;

    public GameContextDecoderImpl(GameEntityFactory entityFactory) {
        this.entityFactory = entityFactory;
    }

    @Override
    public GameContext decode(GameContextDTO dto) {
        if (cachedMap == null) {
            cachedMap = new FourPlayersGameMapFactory().fromJSON("maps/" + dto.mapName() + ".json");
        }
        Map<MatrixCoordinates, Dot> dotsMap = dto.dots().stream()
                .collect(Collectors.toMap(dot ->
                        new MatrixCoordinates(dot.currentTileRow(), dot.currentTileCol()),
                        this::decodeDot));
        Set<Ghost> ghosts = dto.ghosts().stream()
                .map(ghost -> decodeGhost(ghost, cachedMap))
                .collect(Collectors.toSet());
        Set<Pacman> pacmans = dto.pacmans().stream()
                .map(pacman -> decodePacman(pacman, cachedMap))
                .collect(Collectors.toSet());
        return new GameContextImpl(cachedMap, dto.tick(), dotsMap, ghosts, pacmans, dto.gameState().timeLeftInMillis());
    }

    private Pacman decodePacman(PacmanDTO dto, GameMap map) {
        try {
            Vector2D position = new Vector2D(dto.x(), dto.y());
            MatrixCoordinates matrixPosition = new MatrixCoordinates(dto.currentTileRow(), dto.currentTileCol());
            Tile tile = map.getTile(matrixPosition);
            Pacman pacman = this.entityFactory.createPacman(tile, map);
            pacman.setId(dto.id());
            pacman.setIsAlive(dto.isAlive());
            pacman.setPosition(position);
            setField(pacman, "score", dto.score());
            setField(pacman, "lives", dto.lives());
            setField(pacman, "controlledByPlayer", dto.controlledByPlayer());
            setField(pacman, "canEatGhosts", dto.canEatGhosts());
            setField(pacman, "invincible", dto.isInvincible());
            setField(pacman, "lastTimeSpecialDotWasEaten", dto.lastTimeSpecialDotWasEaten());
            setField(pacman, "lastTimeBecameInvincible", dto.lastTimeBecameInvincible());
            setField(pacman, "lastTimeDirectionWasChanged", dto.lastTimeDirectionWasChanged());
            // Set the movement manager fields via reflection
            Object movementManager = getField(pacman, "movementManager");
            setField(movementManager, "currentDirection", Direction.valueOf(dto.currentDirection()));
            setField(movementManager, "desiredDirection", Direction.valueOf(dto.desiredDirection()));
            setField(movementManager, "currentMatrixCoordinates", matrixPosition);
            setField(movementManager, "targetMatrixCoordinates", new MatrixCoordinates(dto.targetTileRow(), dto.targetTileCol()));
            setField(movementManager, "position", position);
            return pacman;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to set pacman fields via reflection", e);
        }
    }

    private Dot decodeDot(DotDTO dto) {
        try {
            Dot dot = this.entityFactory.createDot(new Vector2D(dto.x(), dto.y()), dto.isSpecial());
            dot.setIsAlive(dto.isAlive());
            setField(dot, "lastTimeEaten", dto.lastTimeEaten());
            return dot;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to set dot fields via reflection", e);
        }
    }

    private Ghost decodeGhost(GhostDTO dto, GameMap map) {
        try {
            Vector2D position = new Vector2D(dto.x(), dto.y());
            MatrixCoordinates matrixPosition = new MatrixCoordinates(dto.currentTileRow(), dto.currentTileCol());
            Tile tile = map.getTile(matrixPosition);
            Ghost ghost = this.entityFactory.createGhost(tile, map);
            ghost.setIsAlive(dto.isAlive());
            ghost.setPosition(position);
            setField(ghost, "lastTimeDead", dto.lastTimeDead());
            setField(ghost, "lastTimeDirectionWasChanged", dto.lastTimeDirectionWasChanged());
            // Set the movement manager fields via reflection
            Object movementManager = getField(ghost, "movementManager");
            setField(movementManager, "currentDirection", Direction.valueOf(dto.currentDirection()));
            setField(movementManager, "desiredDirection", Direction.valueOf(dto.desiredDirection()));
            setField(movementManager, "currentMatrixCoordinates", matrixPosition);
            setField(movementManager, "targetMatrixCoordinates", new MatrixCoordinates(dto.targetTileRow(), dto.targetTileCol()));
            setField(movementManager, "position", position);
            return ghost;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to set ghost fields via reflection", e);
        }
    }
}
