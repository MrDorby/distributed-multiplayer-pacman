package it.unibo.controller.commands;

import it.unibo.model.common.Direction;
import it.unibo.model.game.Game;

import java.util.UUID;

public class PacmanMoveCommand implements PacmanCommand {

    private final UUID pacmanId;
    private final Direction direction;

    public PacmanMoveCommand(UUID pacmanId, Direction desiredDirection) {
        this.pacmanId = pacmanId;
        this.direction = desiredDirection;
    }

    @Override
    public void execute(Game game) {
        game.movePacman(pacmanId, direction);
    }
}
