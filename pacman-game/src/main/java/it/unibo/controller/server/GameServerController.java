package it.unibo.controller.server;

import it.unibo.controller.server.engine.ServerGameEngine;
import it.unibo.controller.shared.input.PacmanMoveCommand;
import it.unibo.controller.server.network.GameBroadcaster;
import it.unibo.controller.server.network.GameServerNetworkListener;
import it.unibo.controller.server.network.NettyGameNetworkServer;
import it.unibo.controller.shared.network.packets.GameStartPacket;
import it.unibo.controller.shared.network.packets.PacketType;
import it.unibo.model.entities.GameEntityFactoryImpl;
import it.unibo.model.game.Game;
import it.unibo.model.game.GameContext;
import it.unibo.model.game.GameContextFactory;
import it.unibo.model.game.GameImpl;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameServerController implements GameServerNetworkListener, GameBroadcaster {
    private static final int REQUIRED_PLAYERS = 4;

    private final NettyGameNetworkServer server;
    private final ServerGameEngine engine;

    private final List<String> joinedUsernames = new CopyOnWriteArrayList<>();
    private volatile boolean gameStarted = false;

    public GameServerController(Game game, int tcpPort, int udpPort) throws Exception {
        this.server = new NettyGameNetworkServer(tcpPort, udpPort, this);
        this.engine = new ServerGameEngine(game, this);
        server.start();
    }

    @Override
    public void onPlayerJoined(String username) {
        if (gameStarted) {
            return;
        }
        joinedUsernames.add(username);
        if (joinedUsernames.size() == REQUIRED_PLAYERS) {
            startGame();
        }
    }

    private void startGame() {
        gameStarted = true;
        engine.getGame().setPacmanNames(List.copyOf(joinedUsernames));
        server.broadcastTcp(PacketType.GAME_CONTEXT.getId(), engine.getGame().getContext());
        server.broadcastTcp(PacketType.GAME_START.getId(), new GameStartPacket());
        engine.start();
    }


    @Override
    public void onCommandReceived(String username, PacmanMoveCommand command) {
        engine.enqueueCommand(command);
    }

    @Override
    public void broadcast(GameContext context) {
        if (gameStarted) {
            server.broadcastUdp(PacketType.GAME_CONTEXT.getId(), context);
        }
    }

    static void main() throws Exception {
        GameContext context = GameContextFactory.createFromMap("maps/map1.json", new GameEntityFactoryImpl());
        GameServerController controller = new GameServerController(new GameImpl(context), 700, 701);
    }
}