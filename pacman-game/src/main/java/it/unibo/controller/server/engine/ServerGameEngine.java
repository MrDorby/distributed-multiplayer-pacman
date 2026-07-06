package it.unibo.controller.server.engine;

import it.unibo.controller.shared.engine.AbstractFixedTimeStepGameEngine;
import it.unibo.controller.shared.input.PacmanCommand;
import it.unibo.controller.server.GameContextBroadcaster;
import it.unibo.model.game.Game;

/**
 * Server-side implementation of the fixed-timestep game engine.
 * <p>
 * Runs the same simulation loop as {@link AbstractFixedTimeStepGameEngine}, and
 * additionally broadcasts the current game state to clients at a reduced rate
 * ({@value #BROADCAST_RATE_IN_HZ} Hz), independent of the engine's own tick rate.
 */
public class ServerGameEngine extends AbstractFixedTimeStepGameEngine {
    private static final int BROADCAST_RATE_IN_HZ = 16;
    private final TickThrottleGroup tickThrottleGroup;

    public ServerGameEngine(Game game, GameContextBroadcaster broadcaster) {
        super(game);
        this.tickThrottleGroup = new TickThrottleGroup(getTickRate());
        tickThrottleGroup.register(BROADCAST_RATE_IN_HZ, () -> broadcaster.broadcast(this.game.getContext()));
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
