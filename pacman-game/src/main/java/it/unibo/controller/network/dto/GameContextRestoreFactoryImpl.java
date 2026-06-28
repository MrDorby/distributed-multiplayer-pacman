package it.unibo.controller.network.dto;

import it.unibo.model.common.MatrixCoordinates;
import it.unibo.model.common.Vector2D;
import it.unibo.model.entities.*;
import it.unibo.model.map.FourPlayersGameMapFactory;
import it.unibo.model.map.GameMap;
import it.unibo.model.map.Tile;

public class GameContextRestoreFactoryImpl implements GameContextRestoreFactory {
    @Override
    public GameMap restoreMap(String mapName) {
        return new FourPlayersGameMapFactory().fromJSON("maps/" + mapName + ".json");
    }

    @Override
    public Pacman restorePacman(PacmanDTO dto, GameMap map) {
        Tile tile = map.getTile(new MatrixCoordinates(dto.tileRow(), dto.tileCol()));
        return new PacmanImpl(
                tile,
                map,
                dto.id(),
                dto.score(),
                dto.lives(),
                dto.controlledByPlayer(),
                dto.canEatGhosts(),
                dto.isInvincible(),
                dto.whenBecameInvincible(),
                dto.whenSpecialDotWasEaten(),
                dto.lastTimeDirectionWasChanged(),
                dto.isAlive());
    }

    @Override
    public Dot restoreDot(DotDTO dto) {
        return new DotImpl(new Vector2D(dto.x(), dto.y()), dto.isSpecial(), dto.lastTimeEaten(), dto.isAlive());
    }

    @Override
    public Ghost restoreGhost(GhostDTO dto, GameMap map) {
        Tile tile = map.getTile(new MatrixCoordinates(dto.tileRow(), dto.tileCol()));
        return new GhostImpl(tile, map, dto.isAlive(), dto.lastTimeDead(), dto.lastTimeDirectionWasChanged());
    }
}
