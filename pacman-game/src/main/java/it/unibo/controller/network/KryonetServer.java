package it.unibo.controller.network;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import it.unibo.controller.commands.PacmanCommand;
import it.unibo.controller.commands.PacmanMoveCommand;
import it.unibo.controller.engine.GameEngine;
import it.unibo.controller.network.packets.AssignmentPacket;
import it.unibo.controller.network.packets.GameStartPacket;
import it.unibo.controller.network.packets.LobbyStatusPacket;
import it.unibo.controller.network.packets.PlayerReadyPacket;
import it.unibo.model.game.GameContext;
import it.unibo.model.game.GameContextImpl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class KryonetServer implements NetworkServer {
    private final Server server;
    private final GameEngine gameEngine;
    private final Map<String, Boolean> readyPlayers = new ConcurrentHashMap<>();
    private boolean gameStarted = false;

    public KryonetServer(int port, GameEngine gameEngine) throws IOException {
        this.server = new Server();
        this.gameEngine = gameEngine;

        server.getKryo().register(GameContextImpl.class);
        server.getKryo().register(PacmanMoveCommand.class);
        server.getKryo().register(PlayerReadyPacket.class);
        server.getKryo().register(LobbyStatusPacket.class);
        server.getKryo().register(AssignmentPacket.class);
        server.getKryo().register(GameStartPacket.class);
        server.getKryo().register(HashMap.class);

        server.addListener(new Listener() {
            @Override
            public void connected(Connection connection) {
                if (gameStarted) {
                    connection.close();
                    return;
                }
                readyPlayers.put(String.valueOf(connection.getID()), false);
                connection.sendTCP(new AssignmentPacket(connection.getID()));
                server.sendToAllTCP(new LobbyStatusPacket(new HashMap<>(readyPlayers)));
            }

            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof PacmanCommand command) {
                    gameEngine.enqueueCommand(command);
                } else if (object instanceof PlayerReadyPacket(boolean isReady)) {
                    handleReadyState(connection, isReady);
                }
            }

            @Override
            public void disconnected(Connection connection) {
                readyPlayers.remove(String.valueOf(connection.getID()));
                if (!gameStarted) {
                    server.sendToAllTCP(new LobbyStatusPacket(new HashMap<>(readyPlayers)));
                }
            }
        });
        server.bind(port, port);
        server.start();
    }

    private void handleReadyState(Connection connection, boolean isReady) {
        readyPlayers.put(String.valueOf(connection.getID()), isReady);
        server.sendToAllTCP(new LobbyStatusPacket(new HashMap<>(readyPlayers)));
        if (readyPlayers.size() == 4 && !readyPlayers.containsValue(false)) {
            gameStarted = true;
            server.sendToAllTCP(new GameStartPacket());
            gameEngine.start();
        }
    }

    @Override
    public void broadcast(GameContext context) {
        server.sendToAllUDP(context);
    }
}
