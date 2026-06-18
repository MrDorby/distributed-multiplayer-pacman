package it.unibo.controller.engine;

import it.unibo.controller.commands.PacmanCommand;
import it.unibo.controller.network.NetworkServer;
import it.unibo.model.game.Game;
import it.unibo.model.game.GameContext;
import it.unibo.view.HeadlessView;

public class ServerGameEngine extends AbstractFixedTimeStepGameEngine {
    private final NetworkServer networkServer;

    public ServerGameEngine(Game game, NetworkServer networkServer) {
        super(game);
        this.networkServer = networkServer;
        this.setView(new HeadlessView());
    }

    @Override
    protected void beforeTick() {

    }

    @Override
    protected void afterCommandExecuted(PacmanCommand command) {

    }

    @Override
    protected void afterTick() {
        GameContext context = this.game.getContext();
        networkServer.broadcast(context);
    }
}
