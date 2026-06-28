package it.unibo.controller.network.dto;

import it.unibo.model.common.MatrixCoordinates;
import it.unibo.model.common.Vector2D;
import it.unibo.model.entities.*;
import it.unibo.model.map.FourPlayersGameMapFactory;
import it.unibo.model.map.GameMap;
import it.unibo.model.map.Tile;

import java.lang.reflect.Field;

public class GameContextRestoreFactoryImpl implements GameContextRestoreFactory {
    @Override
    public GameMap restoreMap(String mapName) {
        return new FourPlayersGameMapFactory().fromJSON("maps/" + mapName + ".json");
    }

    @Override
    public Pacman restorePacman(PacmanDTO dto, GameMap map) {
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
            setField(pacman, "lastTImeSpecialDotWasEaten", dto.lastTimeSpecialDotWasEaten());
            setField(pacman, "lastTimeBecameInvincible", dto.lastTimeBecameInvincible());
            setField(pacman, "lastTimeDirectionWasChanged", dto.lastTimeDirectionWasChanged());
            return pacman;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to set pacman fields via reflection", e);
        }
    }

    @Override
    public Dot restoreDot(DotDTO dto) {
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

    @Override
    public Ghost restoreGhost(GhostDTO dto, GameMap map) {
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
