package it.unibo.controller.network.game;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import it.unibo.controller.commands.PacmanCommand;
import it.unibo.controller.commands.PacmanMoveCommand;
import it.unibo.controller.network.packets.*;
import it.unibo.model.game.GameContext;
import it.unibo.model.game.GameContextImpl;

import java.io.IOException;
import java.util.HashMap;

public class KryonetGameNetworkClient implements GameNetworkClient {
    private static final int TIMEOUT_IN_MILLIS = 5000;
    private final Client client;
    private final String localUsername;

    public KryonetGameNetworkClient(String host, int port, String localUsername, SnapshotMailbox mailbox) throws IOException {
        this.client = new Client();
        this.localUsername = localUsername;

        client.getKryo().register(GameContextImpl.class);
        client.getKryo().register(PacmanMoveCommand.class);
        client.getKryo().register(GameStartPacket.class);
        client.getKryo().register(HashMap.class);

        client.addListener(new Listener() {
            @Override
            public void received(Connection connection, Object object) {
                if (object instanceof GameContext gameContext) {
                    mailbox.deliver(gameContext);
                }
            }
        });
        client.start();
        client.connect(TIMEOUT_IN_MILLIS, host, port, port);
        sendReliable(new JoinMatchPacket(localUsername));
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
