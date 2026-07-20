package it.unibo.view.viewmodel;

import it.unibo.model.common.MatrixCoordinates;
import it.unibo.model.map.TileType;

public record TileViewModel(
        MatrixCoordinates matrixPosition,
        TileType type
) {}