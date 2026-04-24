package it.unibo.model.entities;

import it.unibo.model.common.Direction;

import java.util.UUID;

public interface Pacman {
    UUID getId();
    int getScore();
    int getLives();
    void move(Direction direction);
    boolean isPlayer();
    boolean canEatGhost();
}
