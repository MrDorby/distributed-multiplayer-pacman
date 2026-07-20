package it.unibo.controller.server.engine;

import it.unibo.controller.shared.engine.AbstractFixedTimeStepGameEngine;
import it.unibo.controller.shared.engine.event.GameEndedEvent;
import it.unibo.controller.shared.engine.event.GameEvent;
import it.unibo.controller.shared.engine.RemoteGameEngineListener;
import it.unibo.controller.shared.engine.command.PacmanCommand;
import it.unibo.controller.shared.network.dto.GameContextDTO;
import it.unibo.controller.shared.network.translation.GameContextEncoder;
import it.unibo.controller.shared.network.translation.GameContextEncoderImpl;
import it.unibo.model.game.Game;

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

    private final GameContextEncoder encoder = new GameContextEncoderImpl();
    private volatile GameContextDTO latestContext;

    public ServerGameEngine(Game game) {
        super(game);
        this.tickThrottleGroup = new TickThrottleGroup(super.getTickRate());
        this.tickThrottleGroup.register(BROADCAST_RATE_IN_HZ, this::broadcastContextUpdate);
        this.latestContext = encoder.encode(game.getContext());
    }

    public void addListener(RemoteGameEngineListener listener) {
        this.listeners.add(listener);
    }

    public void initialize(List<String> playerNames) {
        if (this.isRunning()) {
            throw new IllegalStateException("Cannot initialize the engine while it is already running!");
        }
        this.game.setPacmanNames(playerNames);
        this.latestContext = encoder.encode(this.game.getContext());
    }

    public GameContextDTO getLatestContext() {
        return this.latestContext;
    }

    @Override
    protected void beforeTick() {
        this.game.getContext().setTick(this.getCurrentTick());
    }

    @Override
    protected void afterCommandExecuted(PacmanCommand command) {}

    @Override
    protected void afterTick() {
        this.latestContext = encoder.encode(this.game.getContext());
        if (this.game.getContext().getGameState().isGameOver()) {
            this.stop();
            broadcastLifecycleEvent(new GameEndedEvent(this.latestContext));
        } else {
            tickThrottleGroup.tick();
        }
    }

    private void broadcastContextUpdate() {
        for (RemoteGameEngineListener listener : listeners) {
            listener.onGameContextUpdate(latestContext);
        }
    }

    private void broadcastLifecycleEvent(GameEvent event) {
        for (RemoteGameEngineListener listener : listeners) {
            listener.onGameEvent(event);
        }
    }
}
