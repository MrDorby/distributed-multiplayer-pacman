package it.unibo.controller.network.sockets.session;

import io.netty.channel.Channel;
import it.unibo.controller.server.network.sockets.session.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameSessionControllerTest {

    public static final String TEST_USERNAME = "player";
    private static final InetSocketAddress TEST_SOCKET_ADDRESS = new InetSocketAddress("localhost", 1234);

    private GameSessionController controller;

    @BeforeEach
    void setUp() {
        controller = new GameSessionController();
    }

    @Test
    void newTcpConnectionCreatesConnectingSessionWithUdpToken() {
        Channel channel = mock(Channel.class);
        GameSession session = controller.onTcpConnect(TEST_USERNAME, channel);
        assertEquals(TEST_USERNAME, session.getUsername());
        assertEquals(GameSessionState.CONNECTING, session.getState());
        assertNotNull(session.getUdpToken());
    }

    @Test
    void udpHandshakeMovesSessionToConnected() {
        GameSession session = controller.onTcpConnect(TEST_USERNAME, mock(Channel.class));
        String token = session.getUdpToken().value();
        UdpHandshakeResult result = controller.onUdpHandshake(token, TEST_SOCKET_ADDRESS);
        assertEquals(UdpHandshakeResult.ACCEPTED, result);
        assertEquals(GameSessionState.CONNECTED, session.getState());
        assertEquals(TEST_SOCKET_ADDRESS, session.getUdpAddress());
        assertNull(session.getUdpToken());
    }

    @Test
    void sessionReadyNotifiesConnectedListener() {
        GameSessionLifecycleListener listener = mock(GameSessionLifecycleListener.class);
        controller.addListener(listener);
        GameSession session = controller.onTcpConnect(TEST_USERNAME, mock(Channel.class));
        GameSessionState previousState = session.getState();
        controller.onUdpHandshake(session.getUdpToken().value(), TEST_SOCKET_ADDRESS);
        controller.onSessionReady(session, previousState);
        verify(listener).onPlayerConnected(session);
    }

    @Test
    void invalidUdpHandshakeReturnsInvalidToken() {
        UdpHandshakeResult result = controller.onUdpHandshake("invalid-token", TEST_SOCKET_ADDRESS);
        assertEquals(UdpHandshakeResult.INVALID_TOKEN, result);
    }

    @Test
    void reconnectReusesExistingSessionAndReplacesTcpChannel() {
        Channel oldChannel = mock(Channel.class);
        Channel newChannel = mock(Channel.class);
        GameSession original = controller.onTcpConnect(TEST_USERNAME, oldChannel);
        GameSession reconnected = controller.onTcpConnect(TEST_USERNAME, newChannel);
        assertSame(original, reconnected);
        assertSame(newChannel, reconnected.getTcpChannel());
        assertEquals(GameSessionState.RECONNECTING, reconnected.getState());
        assertNull(reconnected.getUdpAddress());
    }

    @Test
    void reconnectHandshakeNotifiesReconnectListenerAfterSessionReady() {
        GameSessionLifecycleListener listener = mock(GameSessionLifecycleListener.class);
        controller.addListener(listener);
        GameSession session = controller.onTcpConnect(TEST_USERNAME, mock(Channel.class));
        GameSessionState initialState = session.getState();
        controller.onUdpHandshake(session.getUdpToken().value(), TEST_SOCKET_ADDRESS);
        controller.onSessionReady(session, initialState);
        verify(listener).onPlayerConnected(session);
        controller.onTcpConnect(TEST_USERNAME, mock(Channel.class));
        GameSessionState previousState = session.getState();
        UdpHandshakeResult result = controller.onUdpHandshake(session.getUdpToken().value(), TEST_SOCKET_ADDRESS);
        assertEquals(UdpHandshakeResult.ACCEPTED, result);
        controller.onSessionReady(session, previousState);
        verify(listener).onPlayerReconnected(session);
    }

    @Test
    void disconnectMovesSessionToDisconnected() {
        GameSessionLifecycleListener listener = mock(GameSessionLifecycleListener.class);
        controller.addListener(listener);
        Channel channel = mock(Channel.class);
        GameSession session = controller.onTcpConnect(TEST_USERNAME, channel);
        controller.onDisconnect(session);
        assertEquals(GameSessionState.DISCONNECTED, session.getState());
        verify(listener).onPlayerDisconnected(session);
    }

    @Test
    void expiredUdpHandshakeIsRejectedAndDoesNotBindUdpPort() {
        GameSession session = controller.onTcpConnect(TEST_USERNAME, mock(Channel.class));
        UdpHandshakeToken expiredToken = new UdpHandshakeToken(session.getUdpToken().value(), Instant.now().minusSeconds(1));
        session.setUdpToken(expiredToken);
        UdpHandshakeResult result = controller.onUdpHandshake(expiredToken.value(), TEST_SOCKET_ADDRESS);
        assertEquals(UdpHandshakeResult.EXPIRED_TOKEN, result);
        assertEquals(GameSessionState.CONNECTING, session.getState());
        assertNull(session.getUdpAddress());
    }
}