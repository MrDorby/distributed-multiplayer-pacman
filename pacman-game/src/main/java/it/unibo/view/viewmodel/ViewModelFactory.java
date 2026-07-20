package it.unibo.view.viewmodel;

import it.unibo.model.entities.Dot;
import it.unibo.model.entities.Ghost;
import it.unibo.model.entities.Pacman;
import it.unibo.model.game.GameContext;
import it.unibo.model.game.GameState;
import it.unibo.model.map.GameMap;

import java.util.List;
import java.util.Map;

public final class ViewModelFactory {

    private ViewModelFactory() {}

    public static GameContextViewModel create(GameContext context) {
        return new GameContextViewModel(
                createState(context.getGameState()),
                createMap(context.getMap()),
                context.getDotsMap().values().stream().map(ViewModelFactory::createDot).toList(),
                context.getGhosts().stream().map(ViewModelFactory::createGhost).toList(),
                context.getPacmans().stream().map(ViewModelFactory::createPacman).toList()
        );
    }

    private static GameStateViewModel createState(GameState state) {
        return new GameStateViewModel(
                Map.copyOf(state.getLeaderboard()),
                state.getTimeLeftInMillis(),
                state.isGameOver()
        );
    }

    private static MapViewModel createMap(GameMap map) {
        List<TileViewModel> tiles = map.getTiles().stream()
                .map(t -> new TileViewModel(
                        t.getMatrixPosition(),
                        t.getTileType()
                ))
                .toList();

        return new MapViewModel(
                map.getGridSize().row(),
                map.getGridSize().column(),
                tiles
        );
    }

    private static PacmanViewModel createPacman(Pacman pacman) {
        return new PacmanViewModel(
                pacman.getId(),
                pacman.getPosition(),
                pacman.getDirection().name(),
                pacman.isAlive(),
                pacman.isInvincible(),
                pacman.canEatGhost(),
                pacman.isPlayer(),
                pacman.getScore(),
                pacman.getLives()
        );
    }

    private static GhostViewModel createGhost(Ghost ghost) {
        return new GhostViewModel(
                ghost.getPosition(),
                ghost.getDirection().name(),
                ghost.isAlive()
        );
    }

    private static DotViewModel createDot(Dot dot) {
        return new DotViewModel(
                dot.getPosition(),
                dot.isAlive(),
                dot.isSpecial()
        );
    }
}