package it.unibo.controller.server;

import it.unibo.controller.server.engine.ServerGameEngine;
import it.unibo.controller.server.lobby.LobbyState;
import it.unibo.controller.server.lobby.OpenLobbyManager;
import it.unibo.controller.server.network.sockets.GameServerGateway;
import it.unibo.controller.server.network.sockets.session.GameSession;
import it.unibo.controller.shared.engine.command.ChangePacmanBehaviourCommand;
import it.unibo.controller.shared.network.sockets.packets.GameStartPacket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OpenLobbyManagerTest {

    private ServerGameEngine engine;
    private GameServerGateway gateway;
    private GameSession session1;
    private GameSession session2;
    private OpenLobbyManager manager;

    @BeforeEach
    void setUp() {
        engine = mock(ServerGameEngine.class);
        gateway = mock(GameServerGateway.class);
        session1 = mock(GameSession.class);
        session2 = mock(GameSession.class);
        when(session1.getUsername()).thenReturn("alice");
        when(session2.getUsername()).thenReturn("bob");
        manager = new OpenLobbyManager(2, engine, gateway);
    }

    @Test
    void testInitialStateIsWaiting() {
        assertEquals(LobbyState.WAITING, manager.getState());
    }

    @Test
    void testGameStartsWhenCapacityReached() {
        manager.onPlayerConnected(session1);
        manager.onPlayerConnected(session2);
        assertEquals(LobbyState.PLAYING, manager.getState());
        verify(engine).start();
        verify(gateway).broadcastTcp(any(GameStartPacket.class));
    }

    @Test
    void testPlayerDisconnectsMidGameAssignsBot() {
        manager.onPlayerConnected(session1);
        manager.onPlayerConnected(session2);
        manager.onPlayerDisconnected(session1);
        verify(engine).enqueueCommand(new ChangePacmanBehaviourCommand("alice", false));
    }

    @Test
    void testPlayerReconnectsMidGameRestoresHuman() {
        manager.onPlayerConnected(session1);
        manager.onPlayerConnected(session2);
        manager.onPlayerReconnected(session1);
        verify(engine).enqueueCommand(new ChangePacmanBehaviourCommand("alice", true));
        verify(gateway).sendTcp(eq("alice"), any(GameStartPacket.class));
    }

    @Test
    void testUnknownPlayerCannotReconnectMidGame() {
        GameSession strangerSession = mock(GameSession.class);
        when(strangerSession.getUsername()).thenReturn("charlie");
        manager.onPlayerConnected(session1);
        manager.onPlayerConnected(session2);
        manager.onPlayerReconnected(strangerSession);
        verify(gateway, never()).sendTcp(eq("charlie"), any());
    }
}