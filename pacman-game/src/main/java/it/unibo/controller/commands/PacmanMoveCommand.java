package it.unibo.controller.commands;

import it.unibo.model.common.Direction;
import it.unibo.model.game.Game;

public class PacmanMoveCommand implements PacmanCommand {

    private final String pacmanId;
    private final Direction direction;

    public PacmanMoveCommand(String pacmanId, Direction desiredDirection) {
        this.pacmanId = pacmanId;
        this.direction = desiredDirection;
    }

    @Override
    public void execute(Game game) {
        game.movePacman(pacmanId, direction);
    }
}
