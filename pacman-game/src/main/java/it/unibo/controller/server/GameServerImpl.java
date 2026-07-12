package it.unibo.controller.server;

import it.unibo.controller.server.network.sockets.GameServerGateway;
import it.unibo.controller.server.network.sockets.session.GameSession;
import it.unibo.controller.server.persistence.GamePersistenceController;
import it.unibo.controller.shared.engine.GameEngine;
import it.unibo.controller.shared.input.PacmanMoveCommand;
import it.unibo.controller.shared.network.dto.GameContextDTO;
import it.unibo.controller.shared.network.sockets.packets.GameContextPacket;
import it.unibo.controller.shared.network.sockets.packets.GameStartPacket;
import it.unibo.controller.shared.network.translation.GameContextEncoder;
import it.unibo.controller.shared.network.translation.GameContextEncoderImpl;
import it.unibo.model.game.GameContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class GameServerImpl implements GameServer {
    private static final Logger logger = LoggerFactory.getLogger(GameServerImpl.class);

    private final GameServerGateway tcpUdpServer;
    private final GameEngine engine;
    private final GamePersistenceController persistenceCoordinator;
    private final GameContextEncoder encoder = new GameContextEncoderImpl();

    private final FourManLobby lobby = new FourManLobby();

    public GameServerImpl(GameEngine engine, GameServerGateway server, GamePersistenceController persistenceCoordinator) {
        this.tcpUdpServer = server;
        this.engine = engine;
        this.persistenceCoordinator = persistenceCoordinator;
    }

    @Override
    public void start() throws Exception {
        try {
            tcpUdpServer.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stop() throws Exception {
        try {
            engine.stop();
            tcpUdpServer.stop();
            persistenceCoordinator.stop();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Session handling

    @Override
    public void onPlayerConnected(GameSession session) {
        String username = session.getUsername();
        if (lobby.isPlaying()) {
            logger.info("Player {} tried to join after game already started", username);
            return;
        }
        lobby.addPlayer(username);
        logger.info("Player {} joined ({}/{})", username, lobby.getCurrentPlayerCount(), lobby.getRequiredPlayerCount());
        if (lobby.isFull()) {
            lobby.setPlaying(true);
            startGame();
        }
    }

    private void startGame() {
        List<String> players = lobby.getPlayers();
        logger.info("Required player count reached. Starting game with players: {}", players);
        engine.getGame().setPacmanNames(players);
        GameContextDTO dto = encoder.encode(engine.getGame().getContext());
        tcpUdpServer.broadcastTcp(new GameContextPacket(dto));
        tcpUdpServer.broadcastTcp(new GameStartPacket());
        engine.start();
        persistenceCoordinator.start();
    }

    @Override
    public void onPlayerReconnected(GameSession session) {
        // the game needs to know that it has to override the pacman status to human, not bot
    }

    @Override
    public void onPlayerDisconnect(GameSession session) {
        // the game needs to set the pacman originally controlled by the player as bot.
    }

    // Command handling

    @Override
    public void onCommandReceived(PacmanMoveCommand command) {
        logger.debug("Received move command: {}", command);
        engine.enqueueCommand(command);
    }

    // Game logic handling

    @Override
    public void onGameEnded(GameContext finalContext) {
        GameContextDTO dto = encoder.encode(finalContext);
        persistenceCoordinator.onGameEnded(dto);
    }

    @Override
    public void broadcast(GameContext context) {
        if (lobby.isPlaying()) {
            GameContextDTO dto = encoder.encode(context);
            persistenceCoordinator.updateContext(dto);
            tcpUdpServer.broadcastUdp(new GameContextPacket(dto));
            logger.debug("Broadcasted game context to all sessions.");
        }
    }
}