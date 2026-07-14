package it.unibo.controller.shared.engine;

import it.unibo.model.game.GameContext;

public record GameEndedEvent(GameContext context) implements GameLifecycleEvent {
}
