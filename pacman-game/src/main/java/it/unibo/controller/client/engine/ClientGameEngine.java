package it.unibo.controller.client.engine;

import it.unibo.controller.shared.engine.RemoteGameEngineListener;
import it.unibo.controller.shared.engine.AbstractFixedTimeStepGameEngine;
import it.unibo.controller.shared.engine.event.GameEndedEvent;
import it.unibo.controller.shared.engine.event.GameEvent;
import it.unibo.controller.shared.engine.command.PacmanCommand;
import it.unibo.controller.client.GameCommandListener;
import it.unibo.controller.shared.network.dto.GameContextDTO;
import it.unibo.controller.shared.network.translation.GameContextDecoder;
import it.unibo.controller.shared.network.translation.GameContextDecoderImpl;
import it.unibo.model.entities.SpeculativeEntityFactoryImpl;
import it.unibo.model.game.Game;
import it.unibo.model.game.GameContext;
import it.unibo.model.game.GameImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class ClientGameEngine extends AbstractFixedTimeStepGameEngine implements RemoteGameEngineListener {
    private final AtomicReference<GameContextDTO> latestContext = new AtomicReference<>();
    private final AtomicLong maxReceivedTick = new AtomicLong(-1);
    private final GameContextDecoder decoder = new GameContextDecoderImpl(new SpeculativeEntityFactoryImpl());

    private final Queue<GameEvent> events = new ConcurrentLinkedQueue<>();
    private final List<GameCommandListener> listeners = new ArrayList<>();

    public ClientGameEngine(Game game) {
        super(game);
    }

    public void addListener(GameCommandListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void onGameEvent(GameEvent event) {
        this.events.add(event);
    }

    @Override
    public void onGameContextUpdate(GameContextDTO context) {
        long incomingTick = context.tick();
        long currentMax = maxReceivedTick.get();
        if (incomingTick > currentMax) {
            if (maxReceivedTick.compareAndSet(currentMax, incomingTick)) {
                this.latestContext.set(context);
            }
        }
    }

    @Override
    protected void beforeTick() {
        processEvents();
        GameContextDTO dto = latestContext.getAndSet(null);
        if (dto != null) {
            GameContext context = decoder.decode(dto);
            this.game = new GameImpl(context);
            this.setCurrentTick(context.getTick());
        }
    }

    private void processEvents() {
        GameEvent event;
        while ((event = events.poll()) != null) {
            if (event instanceof GameEndedEvent) {
                this.stop();
                break;
            }
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
