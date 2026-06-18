package it.unibo.controller.network;

import it.unibo.model.game.GameContext;

import java.util.concurrent.atomic.AtomicReference;

public class SnapshotMailbox {
    private final AtomicReference<GameContext> box = new AtomicReference<>();

    public void deliver(GameContext snapshot) {
        box.set(snapshot);
    }

    public GameContext collect() {
        return box.getAndSet(null);
    }

    public boolean hasMail() {
        return box.get() != null;
    }
}
