package it.unibo.controller.shared.input;

import it.unibo.controller.shared.engine.GameEngine;
import it.unibo.model.common.Direction;

public class PlayerInputHandler implements InputHandler {
    private GameEngine engine;
    private final String localPlayerUsername;

    public PlayerInputHandler(String localPlayerUsername) {
        this.localPlayerUsername = localPlayerUsername;
    }

    public void setEngine(GameEngine engine) {
        this.engine = engine;
    }

    @Override
    public void onDirectionPressed(Direction direction) {
        engine.enqueueCommand(new PacmanMoveCommand(localPlayerUsername, direction));
    }
}
