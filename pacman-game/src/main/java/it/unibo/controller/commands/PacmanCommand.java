package it.unibo.controller.commands;

import it.unibo.model.game.Game;

public interface PacmanCommand {
    void execute(Game game);
}
