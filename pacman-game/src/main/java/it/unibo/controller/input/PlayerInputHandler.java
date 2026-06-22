package it.unibo.controller.input;

import it.unibo.controller.engine.GameEngine;
import it.unibo.model.common.Direction;

public class PlayerInputHandler implements InputHandler {
    private final GameEngine engine;
    private final String localPlayerUsername;

    public PlayerInputHandler(GameEngine engine, String localPlayerUsername) {
        this.engine = engine;
        this.localPlayerUsername = localPlayerUsername;
    }

    @Override
    public void onDirectionPressed(Direction direction) {
        engine.enqueueCommand(new PacmanMoveCommand(localPlayerUsername, direction));
    }
}
