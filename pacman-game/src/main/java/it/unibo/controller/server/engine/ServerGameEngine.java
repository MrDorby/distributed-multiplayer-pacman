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
    private GameContextBroadcaster broadcaster;

    public ServerGameEngine(Game game) {
        super(game);
        this.tickThrottleGroup = new TickThrottleGroup(getTickRate());

    }

    public void setBroadcaster(GameContextBroadcaster broadcaster) {
        this.broadcaster = broadcaster;
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
        if (broadcaster != null) {
            tickThrottleGroup.tick();
        }
    }
}
