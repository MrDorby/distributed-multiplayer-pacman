package it.unibo.controller.network.game;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import it.unibo.controller.commands.PacmanMoveCommand;
import it.unibo.controller.engine.GameEngine;
import it.unibo.controller.network.packets.*;
import it.unibo.model.entities.Pacman;
import it.unibo.model.game.GameContext;
import it.unibo.model.game.GameContextImpl;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class KryonetGameNetworkServer implements GameNetworkServer {
    private final Server server;
    private final GameEngine gameEngine;

    private final Map<Connection, String> connectionToUsername = new ConcurrentHashMap<>();
    private boolean gameStarted = false;

    public KryonetGameNetworkServer(int port, GameEngine gameEngine) throws IOException {
        this.server = new Server();
        this.gameEngine = gameEngine;

        server.getKryo().register(JoinMatchPacket.class);
        server.getKryo().register(GameContextImpl.class);
        server.getKryo().register(PacmanMoveCommand.class);
        server.getKryo().register(GameStartPacket.class);
        server.getKryo().register(HashMap.class);

        server.addListener(new Listener() {
            @Override
            public void connected(Connection connection) {
                if (gameStarted) {
                    connection.close();
                }
            }

            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof JoinMatchPacket joinPacket) {
                    handlePlayerJoin(connection, joinPacket);
                } else if (object instanceof PacmanMoveCommand command) {
                    String verifiedUsername = connectionToUsername.get(connection);
                    if (verifiedUsername != null && verifiedUsername.equals(command.pacmanId())) {
                        gameEngine.enqueueCommand(command);
                    }
                }
            }

            @Override
            public void disconnected(Connection connection) {
                connectionToUsername.remove(connection);
            }
        });
        server.bind(port, port);
        server.start();
    }

    private void handlePlayerJoin(Connection connection, JoinMatchPacket packet) {
        String username = packet.username();
        connectionToUsername.put(connection, username);
        if (connectionToUsername.size() == 4 && !gameStarted) {
            gameStarted = true;
            GameContext initialContext = gameEngine.getCurrentContext();
            List<Pacman> pacmans = new ArrayList<>(initialContext.getPacmans());
            List<String> usernames = new ArrayList<>(connectionToUsername.values());
            for (int i = 0; i < pacmans.size(); i++) {
                if (i < usernames.size()) {
                    pacmans.get(i).setId(usernames.get(i));
                }
            }
            server.sendToAllTCP(initialContext);
            server.sendToAllTCP(new GameStartPacket());
            gameEngine.start();
        }
    }

    @Override
    public void broadcast(GameContext context) {
        if (gameStarted) {
            server.sendToAllUDP(context);
        }
    }
}
