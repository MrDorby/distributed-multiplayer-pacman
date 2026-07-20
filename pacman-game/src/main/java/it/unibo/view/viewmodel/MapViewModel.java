package it.unibo.view.viewmodel;

import java.util.List;

public record MapViewModel(
        int row,
        int col,
        List<TileViewModel> tiles
) {}