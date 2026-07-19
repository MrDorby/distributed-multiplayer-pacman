package it.unibo.controller.shared.engine;

import it.unibo.controller.shared.input.PacmanCommand;
import it.unibo.model.game.Game;

import java.util.List;

public class LocalGameEngine extends AbstractFixedTimeStepGameEngine {
    public LocalGameEngine(Game game, String localPlayerUsername) {
        super(game);
        List<String> localNames = List.of(localPlayerUsername, "Alpha", "Beta", "Gamma");
        this.getGame().setPacmanNames(localNames);
        this.getGame().getContext().getPacmans().stream()
                .filter(pacman -> !pacman.getId().equals(localPlayerUsername))
                .forEach(pacman -> pacman.changeBehaviour(false));
    }

    @Override
    protected void beforeTick() {}

    @Override
    protected void afterCommandExecuted(PacmanCommand command) {}

    @Override
    protected void afterTick() {
        if (this.game.getContext().getGameState().isGameOver()) {
            super.stop();
        }
    }
}
