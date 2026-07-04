package it.unibo.controller.server.engine;

import it.unibo.controller.shared.engine.AbstractFixedTimeStepGameEngine;
import it.unibo.controller.shared.input.PacmanCommand;
import it.unibo.controller.server.network.GameBroadcaster;
import it.unibo.model.game.Game;

public class ServerGameEngine extends AbstractFixedTimeStepGameEngine {
    private static final int BROADCAST_RATE_HZ = 16;
    private final TickThrottleGroup tickThrottleGroup;

    public ServerGameEngine(Game game, GameBroadcaster broadcaster) {
        super(game);
        this.tickThrottleGroup = new TickThrottleGroup(getTickRate());
        tickThrottleGroup.register(BROADCAST_RATE_HZ, () -> broadcaster.broadcast(this.game.getContext()));
    }

    @Override
    protected void beforeTick() {

    }

    @Override
    protected void afterCommandExecuted(PacmanCommand command) {

    }

    @Override
    protected void afterTick() {
        tickThrottleGroup.tick();
    }
}
