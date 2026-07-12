package it.unibo.controller.client;

import it.unibo.controller.client.engine.ClientGameEngine;
import it.unibo.controller.client.network.sockets.NettyGameNetworkClient;
import it.unibo.controller.client.network.sockets.NettyGameNetworkClientFactory;
import it.unibo.controller.shared.engine.GameEngine;
import it.unibo.controller.shared.input.InputHandler;
import it.unibo.controller.shared.input.PacmanCommand;
import it.unibo.controller.shared.input.PacmanMoveCommand;
import it.unibo.controller.shared.input.PlayerInputHandler;
import it.unibo.controller.shared.network.sockets.packets.JoinGamePacket;
import it.unibo.controller.shared.network.sockets.packets.PacmanMovePacket;
import it.unibo.model.game.Game;
import it.unibo.model.game.GameContext;
import it.unibo.model.game.GameImpl;
import it.unibo.view.GameViewImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

public class GameClientControllerImpl implements GameClientController {
    private static final Logger logger = LoggerFactory.getLogger(GameClientControllerImpl.class);

    private final NettyGameNetworkClient client;
    private final ClientGameEngine engine;
    private final GameContextBuffer contextBuffer = new GameContextBuffer();

    private final String username;

    public GameClientControllerImpl(Game game, String host, int tcpPort, int udpPort, String username) {
        this.client = NettyGameNetworkClientFactory.create(host, tcpPort, udpPort, this);
        this.engine = new ClientGameEngine(game, contextBuffer, this);
        this.username = username;
        logger.info("Client controller initialized for user '{}', connecting to {}:{} (TCP) / {} (UDP).", username, host, tcpPort, udpPort);
    }

    @Override
    public void start() {
        try {
            client.start();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stop() {
        engine.stop();
        client.stop();
    }

    @Override
    public void onGameContext(GameContext context) {
        logger.debug("Received game context update.");
        contextBuffer.put(context);
    }

    @Override
    public void onGameStart() {
        logger.info("Game start signal received from server. Starting engine.");
        engine.start();
    }

    @Override
    public void sendMoveCommand(PacmanCommand command) {
        PacmanMoveCommand moveCommand = (PacmanMoveCommand) command;
        logger.debug("Sending move command: {}", moveCommand);
        client.sendUdp(new PacmanMovePacket(moveCommand.pacmanId(), moveCommand.direction()));
    }

    @Override
    public void connectToServer() {
        client.sendTcp(new JoinGamePacket(username));
        logger.info("Sent JOIN_MATCH for user '{}'", username);
    }

    @Override
    public ClientGameEngine getEngine() {
        return engine;
    }
}
