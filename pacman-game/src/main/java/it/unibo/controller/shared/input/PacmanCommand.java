package it.unibo.controller.shared.input;

import it.unibo.model.game.Game;

public interface PacmanCommand {
    void execute(Game game);
}
