package it.unibo.controller.engine;

import it.unibo.controller.commands.PacmanCommand;
import it.unibo.model.game.Game;

public class LocalGameEngine extends AbstractFixedTimeStepGameEngine {
    public LocalGameEngine(Game game) {
        super(game);
    }

    @Override
    protected void beforeTick() {}

    @Override
    protected void afterCommandExecuted(PacmanCommand command) {}

    @Override
    protected void afterTick() {}
}
