package it.unibo.controller.server.engine;

import it.unibo.controller.shared.engine.AbstractFixedTimeStepGameEngine;
import it.unibo.controller.shared.input.PacmanCommand;
import it.unibo.controller.server.network.GameBroadcaster;
import it.unibo.model.game.Game;
import it.unibo.view.HeadlessView;

public class ServerGameEngine extends AbstractFixedTimeStepGameEngine {
    private static final int BROADCAST_TICK_DIVISOR = 4;
    private int tickCounter = 0;
    private final GameBroadcaster broadcaster;

    public ServerGameEngine(Game game, GameBroadcaster broadcaster) {
        super(game);
        this.broadcaster = broadcaster;
        this.setView(new HeadlessView());
    }

    @Override
    protected void beforeTick() {

    }

    @Override
    protected void afterCommandExecuted(PacmanCommand command) {

    }

    @Override
    protected void afterTick() {
        tickCounter++;
        if (tickCounter >= BROADCAST_TICK_DIVISOR) {
            tickCounter = 0;
            broadcaster.broadcast(this.game.getContext());
        }
    }
}
