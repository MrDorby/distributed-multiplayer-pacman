package it.unibo.controller.engine;

import it.unibo.controller.commands.PacmanCommand;
import it.unibo.controller.network.NetworkClient;
import it.unibo.controller.network.SnapshotMailbox;
import it.unibo.model.game.Game;
import it.unibo.model.game.GameContext;
import it.unibo.model.game.GameImpl;

public class ClientGameEngine extends AbstractFixedTimeStepGameEngine {
    private final NetworkClient networkClient;
    private final SnapshotMailbox snapshotMailbox;

    public ClientGameEngine(Game game, NetworkClient networkClient, SnapshotMailbox snapshotMailbox) {
        super(game);
        this.networkClient = networkClient;
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
        networkClient.send(command);
    }

    @Override
    protected void afterTick() {

    }
}
