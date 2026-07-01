package it.unibo.controller.network.translation;

import it.unibo.controller.network.dto.DotDTO;
import it.unibo.controller.network.dto.GameContextDTO;
import it.unibo.controller.network.dto.GhostDTO;
import it.unibo.controller.network.dto.PacmanDTO;
import it.unibo.model.common.MatrixCoordinates;
import it.unibo.model.common.Vector2D;
import it.unibo.model.entities.*;
import it.unibo.model.game.GameContext;
import it.unibo.model.game.GameContextImpl;
import it.unibo.model.map.FourPlayersGameMapFactory;
import it.unibo.model.map.GameMap;
import it.unibo.model.map.Tile;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Decodes a {@link GameContextDTO} back into a {@link GameContext}.
 * The game map is loaded once and cached for subsequent decode calls.
 */
public class GameContextDecoderImpl implements GameContextDecoder{
    private GameMap cachedMap;

    @Override
    public GameContext decode(GameContextDTO dto) {
        if (cachedMap == null) {
            cachedMap = new FourPlayersGameMapFactory().fromJSON("maps/" + dto.mapName() + ".json");
        }
        Map<MatrixCoordinates, Dot> dotsMap = dto.dots().stream()
                .collect(Collectors.toMap(d -> new MatrixCoordinates(d.tileRow(), d.tileCol()), this::decodeDot));
        Set<Ghost> ghosts = dto.ghosts().stream()
                .map(g -> decodeGhost(g, cachedMap))
                .collect(Collectors.toSet());
        Set<Pacman> pacmans = dto.pacmans().stream()
                .map(p -> decodePacman(p, cachedMap))
                .collect(Collectors.toSet());
        return new GameContextImpl(cachedMap, dotsMap, ghosts, pacmans, dto.gameState().timeLeftInMillis());
    }

    private Pacman decodePacman(PacmanDTO dto, GameMap map) {
        try {
            Tile tile = map.getTile(new MatrixCoordinates(dto.tileRow(), dto.tileCol()));
            PacmanImpl pacman = new PacmanImpl(tile, map);
            pacman.setId(dto.id());
            pacman.setIsAlive(dto.isAlive());
            pacman.setPosition(new Vector2D(dto.x(), dto.y()));
            setField(pacman, "score", dto.score());
            setField(pacman, "lives", dto.lives());
            setField(pacman, "controlledByPlayer", dto.controlledByPlayer());
            setField(pacman, "canEatGhosts", dto.canEatGhosts());
            setField(pacman, "invincible", dto.isInvincible());
            setField(pacman, "lastTimeSpecialDotWasEaten", dto.lastTimeSpecialDotWasEaten());
            setField(pacman, "lastTimeBecameInvincible", dto.lastTimeBecameInvincible());
            setField(pacman, "lastTimeDirectionWasChanged", dto.lastTimeDirectionWasChanged());
            return pacman;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to set pacman fields via reflection", e);
        }
    }

    private Dot decodeDot(DotDTO dto) {
        try {
            DotImpl dot = new DotImpl(new Vector2D(dto.x(), dto.y()));
            dot.setIsAlive(dto.isAlive());
            setField(dot, "lastTimeEaten", dto.lastTimeEaten());
            setField(dot, "isSpecial", dto.isSpecial());
            return dot;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to set dot fields via reflection", e);
        }
    }

    private Ghost decodeGhost(GhostDTO dto, GameMap map) {
        try {
            Tile tile = map.getTile(new MatrixCoordinates(dto.tileRow(), dto.tileCol()));
            GhostImpl ghost = new GhostImpl(tile, map);
            ghost.setIsAlive(dto.isAlive());
            ghost.setPosition(new Vector2D(dto.x(), dto.y()));
            setField(ghost, "lastTimeDead", dto.lastTimeDead());
            setField(ghost, "lastTimeDirectionWasChanged", dto.lastTimeDirectionWasChanged());
            return ghost;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to set ghost fields via reflection", e);
        }
    }

    private static void setField(Object instance, String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field field = instance.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(instance, value);
    }
}
