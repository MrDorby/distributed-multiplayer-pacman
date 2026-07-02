package it.unibo.controller.client;

import it.unibo.model.game.GameContext;

import java.util.concurrent.atomic.AtomicReference;

public class GameContextBuffer {
    private final AtomicReference<GameContext> box = new AtomicReference<>();

    public void put(GameContext snapshot) {
        box.set(snapshot);
    }

    public GameContext get() {
        return box.getAndSet(null);
    }
}
