package it.unibo.controller.server.engine;

import it.unibo.controller.shared.engine.GameLifecycleEvent;
import it.unibo.model.game.GameContext;

public interface GameEngineListener {
    void onGameEvent(GameLifecycleEvent event);

    void onGameContextUpdate(GameContext context);
}
