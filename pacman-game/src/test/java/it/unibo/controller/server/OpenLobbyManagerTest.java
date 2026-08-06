package it.unibo.controller.server;

import it.unibo.controller.server.engine.ServerGameEngine;
import it.unibo.controller.server.lobby.LobbyState;
import it.unibo.controller.server.lobby.OpenLobbyManager;
import it.unibo.controller.server.network.sockets.GameServerGateway;
import it.unibo.controller.server.network.sockets.session.GameSession;
import it.unibo.controller.shared.network.sockets.packets.GameStartPacket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OpenLobbyManagerTest {

    private ServerGameEngine engine;
    private GameServerGateway gateway;
    private GameSession aliceSession;
    private GameSession bobSession;
    private OpenLobbyManager manager;

    @BeforeEach
    void setUp() {
        engine = mock(ServerGameEngine.class);
        gateway = mock(GameServerGateway.class);
        aliceSession = mock(GameSession.class);
        bobSession = mock(GameSession.class);
        when(aliceSession.getUsername()).thenReturn("alice");
        when(bobSession.getUsername()).thenReturn("bob");
        manager = new OpenLobbyManager(2, engine, gateway);
    }

    @Test
    void testInitialStateIsWaitingAndEmpty() {
        assertEquals(LobbyState.WAITING, manager.getState());
        assertTrue(manager.getConnectedPlayers().isEmpty());
    }

    @Test
    void testSinglePlayerConnects() {
        manager.onPlayerConnected(aliceSession);
        assertEquals(LobbyState.WAITING, manager.getState());
        assertEquals(Set.of("alice"), manager.getConnectedPlayers());
    }

    @Test
    void testGameStartsWhenCapacityReached() {
        manager.onPlayerConnected(aliceSession);
        manager.onPlayerConnected(bobSession);
        assertEquals(LobbyState.PLAYING, manager.getState());
        assertEquals(Set.of("alice", "bob"), manager.getConnectedPlayers());
        verify(engine).start();
        verify(gateway).broadcastTcp(any(GameStartPacket.class));
    }
}