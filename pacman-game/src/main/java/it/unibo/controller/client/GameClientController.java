package it.unibo.controller.client;

import it.unibo.controller.client.engine.ClientGameEngine;
import it.unibo.controller.shared.input.PacmanCommand;
import it.unibo.controller.client.network.GameCommandDispatcher;
import it.unibo.controller.client.network.GameClientNetworkListener;
import it.unibo.controller.client.network.NettyGameNetworkClient;
import it.unibo.controller.shared.network.packets.PacketType;
import it.unibo.model.game.Game;
import it.unibo.model.game.GameContext;

public class GameClientController implements GameClientNetworkListener, GameCommandDispatcher {
    private final NettyGameNetworkClient client;
    private final ClientGameEngine engine;
    private final GameContextBuffer contextBuffer = new GameContextBuffer();

    public GameClientController(Game game, String host, int tcpPort, int udpPort, String username) throws InterruptedException {
        this.client = new NettyGameNetworkClient(host, tcpPort, udpPort, username, this);
        this.engine = new ClientGameEngine(game, contextBuffer, this);
    }

    @Override
    public void onGameContext(GameContext context) {
        contextBuffer.put(context);
    }

    @Override
    public void onGameStart() {
        engine.start();
    }

    @Override
    public void sendMoveCommand(PacmanCommand command) {
        client.sendTcp(PacketType.MOVE_COMMAND, command);
    }
}
