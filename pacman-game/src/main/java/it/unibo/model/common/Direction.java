package it.unibo.model.common;

public enum Direction {
    UP,
    RIGHT,
    DOWN,
    LEFT,
    NONE;

    public Direction getOpposite() {
        return switch (this) {
            case UP -> DOWN;
            case RIGHT -> LEFT;
            case DOWN -> UP;
            case LEFT -> RIGHT;
            case NONE -> NONE;
        };
    }
}
