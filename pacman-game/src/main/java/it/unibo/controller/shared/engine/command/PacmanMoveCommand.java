package it.unibo.controller.shared.engine.command;

import it.unibo.model.common.Direction;
import it.unibo.model.game.Game;

public record PacmanMoveCommand(String pacmanId, Direction direction) implements PacmanCommand {

    @Override
    public void execute(Game game) {
        game.movePacman(pacmanId, direction);
    }
}
