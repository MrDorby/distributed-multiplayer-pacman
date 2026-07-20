package it.unibo.controller.server.engine;

import it.unibo.controller.shared.engine.AbstractFixedTimeStepGameEngine;
import it.unibo.controller.shared.engine.GameEndedEvent;
import it.unibo.controller.shared.engine.GameLifecycleEvent;
import it.unibo.controller.shared.engine.RemoteGameEngineListener;
import it.unibo.controller.shared.input.PacmanCommand;
import it.unibo.model.game.Game;
import it.unibo.model.game.GameContext;

import java.util.ArrayList;
import java.util.List;

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
    private final List<RemoteGameEngineListener> listeners = new ArrayList<>();

    public ServerGameEngine(Game game) {
        super(game);
        this.tickThrottleGroup = new TickThrottleGroup(super.getTickRate());
        this.tickThrottleGroup.register(BROADCAST_RATE_IN_HZ, this::broadcastContextUpdate);
    }

    public void addListener(RemoteGameEngineListener listener) {
        this.listeners.add(listener);
    }

    @Override
    protected void beforeTick() {
        this.getGame().getContext().setTick(this.getCurrentTick());
    }

    @Override
    protected void afterCommandExecuted(PacmanCommand command) {}

    @Override
    protected void afterTick() {
        if (this.getGame().getContext().getGameState().isGameOver()) {
            this.stop();
            broadcastLifecycleEvent(new GameEndedEvent(this.getGame().getContext()));
        } else {
            tickThrottleGroup.tick();
        }
    }

    private void broadcastContextUpdate() {
        GameContext context = this.getGame().getContext();
        for (RemoteGameEngineListener listener : listeners) {
            listener.onGameContextUpdate(context);
        }
    }

    private void broadcastLifecycleEvent(GameLifecycleEvent event) {
        for (RemoteGameEngineListener listener : listeners) {
            listener.onGameEvent(event);
        }
    }
}
