package it.unibo.controller.shared.engine.command;

import it.unibo.model.game.Game;

public interface PacmanCommand {
    void execute(Game game);
}
