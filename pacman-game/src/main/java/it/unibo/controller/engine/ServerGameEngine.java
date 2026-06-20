package it.unibo.controller.engine;

import it.unibo.controller.commands.PacmanCommand;
import it.unibo.controller.network.game.GameNetworkServer;
import it.unibo.model.game.Game;
import it.unibo.model.game.GameContext;
import it.unibo.view.HeadlessView;

public class ServerGameEngine extends AbstractFixedTimeStepGameEngine {
    private final GameNetworkServer server;

    public ServerGameEngine(Game game, GameNetworkServer server) {
        super(game);
        this.server = server;
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
        server.broadcast(context);
    }
}
