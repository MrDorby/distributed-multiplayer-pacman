package it.unibo.controller.client.engine;

import it.unibo.controller.shared.engine.AbstractFixedTimeStepGameEngine;
import it.unibo.controller.shared.input.PacmanCommand;
import it.unibo.controller.client.GameCommandDispatcher;
import it.unibo.controller.client.GameContextBuffer;
import it.unibo.model.game.Game;
import it.unibo.model.game.GameContext;
import it.unibo.model.game.GameImpl;

public class ClientGameEngine extends AbstractFixedTimeStepGameEngine {
    private final GameCommandDispatcher commandSender;
    private final GameContextBuffer contextBuffer;

    public ClientGameEngine(Game game, GameContextBuffer contextBuffer, GameCommandDispatcher commandSender) {
        super(game);
        this.commandSender = commandSender;
        this.contextBuffer = contextBuffer;
    }

    @Override
    protected void beforeTick() {
        GameContext snapshot = contextBuffer.get();
        if (snapshot != null) {
            this.game = new GameImpl(snapshot);
        }
    }

    @Override
    protected void afterCommandExecuted(PacmanCommand command) {
        commandSender.sendMoveCommand(command);
    }

    @Override
    protected void afterTick() {

    }
}
