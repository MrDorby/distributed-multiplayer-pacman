package it.unibo.view.viewmodel;

import java.util.List;

public record GameContextViewModel(
        GameStateViewModel gameState,
        MapViewModel map,
        List<DotViewModel> dots,
        List<GhostViewModel> ghosts,
        List<PacmanViewModel> pacmans
) {}