package it.unibo.controller.network;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import it.unibo.controller.commands.PacmanCommand;
import it.unibo.controller.commands.PacmanMoveCommand;
import it.unibo.controller.network.packets.AssignmentPacket;
import it.unibo.controller.network.packets.GameStartPacket;
import it.unibo.controller.network.packets.LobbyStatusPacket;
import it.unibo.controller.network.packets.PlayerReadyPacket;
import it.unibo.model.game.GameContext;
import it.unibo.model.game.GameContextImpl;

import java.io.IOException;
import java.util.HashMap;

public class KryonetClient implements NetworkClient {
    private static final int TIMEOUT_IN_MILLIS = 5000;
    private final Client client;

    public interface LobbyEventListener {
        void onLobbyUpdated(Object packet);
    }

    public KryonetClient(String host, int port, SnapshotMailbox mailbox, LobbyEventListener lobbyListener) throws IOException {
        this.client = new Client();

        client.getKryo().register(GameContextImpl.class);
        client.getKryo().register(PacmanMoveCommand.class);
        client.getKryo().register(PlayerReadyPacket.class);
        client.getKryo().register(AssignmentPacket.class);
        client.getKryo().register(GameStartPacket.class);
        client.getKryo().register(LobbyStatusPacket.class);
        client.getKryo().register(HashMap.class);

        client.addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof GameContext gameContext) {
                    mailbox.deliver(gameContext);
                } else {
                    lobbyListener.onLobbyUpdated(object);
                }
            }
        });
        client.start();
        client.connect(TIMEOUT_IN_MILLIS, host, port, port);
    }

    @Override
    public void send(PacmanCommand command) {
        client.sendUDP(command);
    }

    @Override
    public void sendReliable(Object packet) {
        client.sendTCP(packet);
    }
}
