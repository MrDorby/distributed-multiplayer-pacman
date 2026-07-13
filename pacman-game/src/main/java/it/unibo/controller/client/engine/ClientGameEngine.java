package it.unibo.controller.client.engine;

import it.unibo.controller.shared.engine.RemoteGameEngineListener;
import it.unibo.controller.shared.engine.AbstractFixedTimeStepGameEngine;
import it.unibo.controller.shared.engine.GameEndedEvent;
import it.unibo.controller.shared.engine.GameLifecycleEvent;
import it.unibo.controller.shared.input.PacmanCommand;
import it.unibo.controller.client.GameCommandListener;
import it.unibo.model.game.Game;
import it.unibo.model.game.GameContext;
import it.unibo.model.game.GameImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicReference;

public class ClientGameEngine extends AbstractFixedTimeStepGameEngine implements RemoteGameEngineListener {
    private final AtomicReference<GameContext> latestContext = new AtomicReference<>();
    private final Queue<GameLifecycleEvent> events = new ConcurrentLinkedQueue<>();
    private final List<GameCommandListener> listeners = new ArrayList<>();

    public ClientGameEngine(Game game) {
        super(game);
    }

    public void addListener(GameCommandListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void onGameEvent(GameLifecycleEvent event) {
        this.events.add(event);
    }

    @Override
    public void onGameContextUpdate(GameContext context) {
        this.latestContext.set(context);
    }

    private void processEvents() {
        GameLifecycleEvent event;
        while ((event = events.poll()) != null) {
            if (event instanceof GameEndedEvent) {
                this.stop();
                view.onLifecycleEvent(event);
                break;
            }
        }
    }

    @Override
    protected void beforeTick() {
        processEvents();
        GameContext context = latestContext.getAndSet(null);
        if (context != null) {
            this.game = new GameImpl(context);
        }
    }

    @Override
    protected void afterCommandExecuted(PacmanCommand command) {
        for (GameCommandListener listener : listeners) {
            listener.onGameCommand(command);
        }
    }

    @Override
    protected void afterTick() {}
}
