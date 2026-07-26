package it.unibo.controller.shared.engine.command;

import it.unibo.model.game.Game;

public record ChangePacmanBehaviourCommand(String username, boolean isPlayer) implements PacmanCommand{
    @Override
    public void execute(Game game) {
        game.changePacmanBehaviour(username, isPlayer);
    }
}
