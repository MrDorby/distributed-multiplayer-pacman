package it.unibo.controller.client;

import it.unibo.controller.client.engine.ClientGameEngine;
import it.unibo.controller.client.network.sockets.GameClientGateway;
import it.unibo.controller.shared.engine.GameEndedEvent;
import it.unibo.controller.shared.engine.GameEngine;
import it.unibo.controller.shared.input.PacmanCommand;
import it.unibo.controller.shared.input.PacmanMoveCommand;
import it.unibo.controller.shared.network.sockets.packets.JoinGamePacket;
import it.unibo.controller.shared.network.sockets.packets.PacmanMovePacket;
import it.unibo.model.game.GameContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameClientImpl implements GameClient {
    private static final Logger logger = LoggerFactory.getLogger(GameClientImpl.class);

    private final GameClientGateway gateway;
    private final ClientGameEngine engine;
    private final String username;

    public GameClientImpl(ClientGameEngine engine, GameClientGateway gateway, String username) {
        this.engine = engine;
        this.gateway = gateway;
        this.username = username;
    }

    @Override
    public void start() {
        try {
            gateway.start();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stop() {
        engine.stop();
        gateway.stop();
    }

    /* ************************************ *
     * Game client network listener methods *
     * ************************************ */

    @Override
    public void onGameContext(GameContext context) {
        logger.trace("Received authoritative game context update");
        engine.onGameContextUpdate(context);
    }

    @Override
    public void onGameStart() {
        logger.debug("Received signal to start the game. Starting the engine");
        engine.start();
    }

    @Override
    public void onGameEnd() {
        logger.debug("Received game end event from server");
        engine.onGameEvent(new GameEndedEvent(engine.getGame().getContext()));
    }

    /* ************************************ *
     * Game engine command listener methods *
     * ************************************ */

    @Override
    public void onGameCommand(PacmanCommand command) {
        if (command instanceof PacmanMoveCommand moveCommand) {
            logger.debug("Sending move command: {}", moveCommand);
            gateway.sendUdp(new PacmanMovePacket(moveCommand.pacmanId(), moveCommand.direction()));
        }
    }

    /* *********************** *
     * Client specific methods *
     * *********************** */

    @Override
    public void connectToServer() {
        gateway.sendTcp(new JoinGamePacket(username));
        logger.info("Sent JOIN_MATCH for user '{}'", username);
    }

    @Override
    public GameEngine getEngine() {
        return engine;
    }
}
