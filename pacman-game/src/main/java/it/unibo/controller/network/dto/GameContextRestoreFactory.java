package it.unibo.controller.network.dto;

import it.unibo.model.entities.Dot;
import it.unibo.model.entities.Ghost;
import it.unibo.model.entities.Pacman;
import it.unibo.model.map.GameMap;

public interface GameContextRestoreFactory {

    GameMap restoreMap(String mapName);

    Pacman restorePacman(PacmanDTO dto, GameMap map);

    Dot restoreDot(DotDTO dto);

    Ghost restoreGhost(GhostDTO dto, GameMap map);
}