package it.unibo.model.map;

import it.unibo.model.common.Vector2D;
import it.unibo.model.entities.Dot;

import java.util.Optional;

public interface Tile {
    boolean isWall();
    Optional<Dot> getDot();
    Vector2D getCenterPosition();
    Vector2D getMatrixPosition();
}
