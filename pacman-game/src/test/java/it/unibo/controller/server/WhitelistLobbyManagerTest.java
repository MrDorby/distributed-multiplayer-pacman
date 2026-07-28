package it.unibo.controller.server;

import it.unibo.controller.server.engine.ServerGameEngine;
import it.unibo.controller.server.lobby.LobbyState;
import it.unibo.controller.server.lobby.WhitelistedLobbyManager;
import it.unibo.controller.server.network.sockets.GameServerGateway;
import it.unibo.controller.server.network.sockets.session.GameSession;
import it.unibo.controller.shared.network.sockets.packets.GameStartPacket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class WhitelistLobbyManagerTest {

    private ServerGameEngine engine;
    private GameServerGateway gateway;
    private GameSession aliceSession;
    private GameSession bobSession;
    private GameSession strangerSession;
    private WhitelistedLobbyManager manager;

    @BeforeEach
    void setUp() {
        engine = mock(ServerGameEngine.class);
        gateway = mock(GameServerGateway.class);
        aliceSession = mock(GameSession.class);
        bobSession = mock(GameSession.class);
        strangerSession = mock(GameSession.class);
        when(aliceSession.getUsername()).thenReturn("alice");
        when(bobSession.getUsername()).thenReturn("bob");
        when(strangerSession.getUsername()).thenReturn("charlie");
        Set<String> whitelist = Set.of("alice", "bob");
        manager = new WhitelistedLobbyManager(whitelist, 15, engine, gateway);
    }

    @Test
    void testInitialStateIsWaitingAndEmpty() {
        assertEquals(LobbyState.WAITING, manager.getState());
        assertTrue(manager.getConnectedPlayers().isEmpty());
    }

    @Test
    void testWhitelistedPlayerCanConnect() {
        manager.onPlayerConnected(aliceSession);
        assertEquals(LobbyState.WAITING, manager.getState());
        assertEquals(Set.of("alice"), manager.getConnectedPlayers());
    }

    @Test
    void testNonWhitelistedPlayerIsRejected() {
        manager.onPlayerConnected(strangerSession);
        assertEquals(LobbyState.WAITING, manager.getState());
        assertTrue(manager.getConnectedPlayers().isEmpty());
    }

    @Test
    void testGameStartsWhenWhitelistedCapacityReached() {
        manager.onPlayerConnected(aliceSession);
        assertEquals(Set.of("alice"), manager.getConnectedPlayers());
        assertEquals(LobbyState.WAITING, manager.getState());
        // Charlie tries to join but is ignored
        manager.onPlayerConnected(strangerSession);
        assertEquals(Set.of("alice"), manager.getConnectedPlayers());
        assertEquals(LobbyState.WAITING, manager.getState());
        // Bob connects, hitting full capacity of valid players
        manager.onPlayerConnected(bobSession);
        verify(engine, timeout(1000)).start();
        verify(gateway, timeout(1000)).broadcastTcp(any(GameStartPacket.class));
        assertEquals(Set.of("alice", "bob"), manager.getConnectedPlayers());
        assertEquals(LobbyState.PLAYING, manager.getState());
    }
}