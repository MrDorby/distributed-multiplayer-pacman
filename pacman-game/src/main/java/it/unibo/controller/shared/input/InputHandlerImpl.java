package it.unibo.controller.shared.input;

import it.unibo.controller.shared.engine.GameEngine;
import it.unibo.model.common.Direction;

public class InputHandlerImpl implements InputHandler {
    private GameEngine engine;
    private final String username;

    public InputHandlerImpl(String username) {
        this.username = username;
    }

    @Override
    public void setEngine(GameEngine engine) {
        this.engine = engine;
    }

    @Override
    public void onDirectionPressed(Direction direction) {
        engine.enqueueCommand(new PacmanMoveCommand(username, direction));
    }
}
