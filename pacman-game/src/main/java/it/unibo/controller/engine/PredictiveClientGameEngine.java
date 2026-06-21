package it.unibo.controller.engine;

import it.unibo.controller.commands.PacmanCommand;
import it.unibo.controller.network.game.GameNetworkClient;
import it.unibo.controller.network.game.SnapshotMailbox;
import it.unibo.model.game.Game;
import it.unibo.model.game.GameContext;
import it.unibo.model.game.GameImpl;

public class ClientGameEngine extends AbstractFixedTimeStepGameEngine {
    private final GameNetworkClient client;
    private final SnapshotMailbox snapshotMailbox;

    public ClientGameEngine(Game game, GameNetworkClient client, SnapshotMailbox snapshotMailbox) {
        super(game);
        this.client = client;
        this.snapshotMailbox = snapshotMailbox;
    }


    @Override
    protected void beforeTick() {
        GameContext snapshot = snapshotMailbox.collect();
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
