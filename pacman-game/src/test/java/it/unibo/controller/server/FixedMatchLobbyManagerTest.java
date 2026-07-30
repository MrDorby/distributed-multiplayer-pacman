package it.unibo.controller.server;

import it.unibo.controller.server.engine.ServerGameEngine;
import it.unibo.controller.server.lobby.FixedMatchLobbyManager;
import it.unibo.controller.server.lobby.LobbyState;
import it.unibo.controller.server.network.sockets.GameServerGateway;
import it.unibo.controller.server.network.sockets.session.GameSession;
import it.unibo.controller.shared.network.sockets.packets.GameStartPacket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class FixedMatchLobbyManagerTest {

    private ServerGameEngine engine;
    private GameServerGateway gateway;
    private GameSession aliceSession;
    private GameSession bobSession;
    private FixedMatchLobbyManager manager;

    @BeforeEach
    void setUp() {
        engine = mock(ServerGameEngine.class);
        gateway = mock(GameServerGateway.class);
        aliceSession = mock(GameSession.class);
        bobSession = mock(GameSession.class);
        when(aliceSession.getUsername()).thenReturn("alice");
        when(bobSession.getUsername()).thenReturn("bob");
        Set<String> expectedPlayers = Set.of("alice", "bob");
        manager = new FixedMatchLobbyManager(expectedPlayers, 15, engine, gateway);
    }

    @Test
    void testInitialStateIsWaitingAndEmpty() {
        assertEquals(LobbyState.WAITING, manager.getState());
        assertTrue(manager.getConnectedPlayers().isEmpty());
    }

    @Test
    void testExpectedPlayerCanConnect() {
        manager.onPlayerConnected(aliceSession);
        assertEquals(LobbyState.WAITING, manager.getState());
        assertEquals(Set.of("alice"), manager.getConnectedPlayers());
    }

    @Test
    void testGameStartsWhenExpectedCapacityReached() {
        manager.onPlayerConnected(aliceSession);
        assertEquals(Set.of("alice"), manager.getConnectedPlayers());
        assertEquals(LobbyState.WAITING, manager.getState());
        manager.onPlayerConnected(bobSession);
        verify(engine, timeout(1000)).start();
        verify(gateway, timeout(1000)).broadcastTcp(any(GameStartPacket.class));
        assertEquals(Set.of("alice", "bob"), manager.getConnectedPlayers());
        assertEquals(LobbyState.PLAYING, manager.getState());
    }
}