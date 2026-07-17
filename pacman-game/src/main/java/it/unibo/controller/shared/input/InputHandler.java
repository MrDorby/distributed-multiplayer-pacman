package it.unibo.controller.shared.input;

import it.unibo.controller.shared.engine.GameEngine;
import it.unibo.model.common.Direction;

public interface InputHandler {
    void setEngine(GameEngine engine);

    void onDirectionPressed(Direction direction);
}
