package it.unibo.controller.engine;

import it.unibo.controller.input.PacmanCommand;
import it.unibo.controller.network.game.GameNetworkClient;
import it.unibo.controller.network.game.GameContextMailbox;
import it.unibo.model.game.Game;
import it.unibo.model.game.GameContext;
import it.unibo.model.game.GameImpl;

public class PredictiveClientGameEngine extends AbstractFixedTimeStepGameEngine {
    private final GameNetworkClient client;
    private final GameContextMailbox gameContextMailbox;

    public PredictiveClientGameEngine(Game game, GameNetworkClient client, GameContextMailbox gameContextMailbox) {
        super(game);
        this.client = client;
        this.gameContextMailbox = gameContextMailbox;
    }


    @Override
    protected void beforeTick() {
        GameContext snapshot = gameContextMailbox.collect();
        if (snapshot != null) {
            this.game = new GameImpl(snapshot);
        }
    }

    @Override
    protected void afterCommandExecuted(PacmanCommand command) {
        client.send(command);
    }

    @Override
    protected void afterTick() {

    }
}
