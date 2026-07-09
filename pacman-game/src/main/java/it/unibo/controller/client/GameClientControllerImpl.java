package it.unibo.controller.client;

import it.unibo.controller.client.engine.ClientGameEngine;
import it.unibo.controller.client.network.NettyGameNetworkClientFactory;
import it.unibo.controller.shared.engine.GameEngine;
import it.unibo.controller.shared.input.InputHandler;
import it.unibo.controller.client.network.GameCommandDispatcher;
import it.unibo.controller.client.network.GameClientNetworkListener;
import it.unibo.controller.client.network.NettyGameNetworkClient;
import it.unibo.controller.shared.input.PacmanCommand;
import it.unibo.controller.shared.input.PacmanMoveCommand;
import it.unibo.controller.shared.input.PlayerInputHandler;
import it.unibo.controller.shared.network.packets.PacmanMovePacket;
import it.unibo.model.game.Game;
import it.unibo.model.game.GameContext;
import it.unibo.model.game.GameImpl;
import it.unibo.view.GameViewImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.UUID;

public class GameClientController implements GameClientNetworkListener, GameCommandDispatcher {
    private static final Logger logger = LoggerFactory.getLogger(GameClientController.class);

    private final NettyGameNetworkClient client;
    private final ClientGameEngine engine;
    private final GameContextBuffer contextBuffer = new GameContextBuffer();

    public GameClientController(Game game, String host, int tcpPort, int udpPort, String username) throws InterruptedException {
        this.client = NettyGameNetworkClientFactory.create(host, tcpPort, udpPort, this);
        this.engine = new ClientGameEngine(game, contextBuffer, this);
        logger.info("Client controller initialized for user '{}', connecting to {}:{} (TCP) / {} (UDP).",
                username, host, tcpPort, udpPort);
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

    public ClientGameEngine getEngine() {
        return engine;
    }

     static void main(String[] args) throws InterruptedException {
        String username = UUID.randomUUID().toString();
        Game game = new GameImpl(null);

        GameClientController controller = new GameClientController(game, "localhost", 700, 701, username);
        GameEngine engine = controller.getEngine();

        InputHandler inputHandler = new PlayerInputHandler(engine, username);
        GameViewImpl view = new GameViewImpl(inputHandler);
        engine.setView(view);

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Pacman Client");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(view.getGamePanel());
            frame.pack();
            frame.setVisible(true);
            view.getGamePanel().requestFocusInWindow();
        });
    }
}
