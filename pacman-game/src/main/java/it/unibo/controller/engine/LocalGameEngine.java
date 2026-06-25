package it.unibo.controller.engine;

import it.unibo.controller.input.PacmanCommand;
import it.unibo.model.game.Game;

import java.util.List;

public class LocalGameEngine extends AbstractFixedTimeStepGameEngine {
    public LocalGameEngine(Game game, String localPlayerUsername) {
        super(game);
        List<String> localNames = List.of(localPlayerUsername, "Bot1", "Bot2", "Bot3");
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
    protected void afterTick() {}
}
