package it.unibo.controller.client.network.sockets.session;

import it.unibo.controller.client.network.sockets.GameClientGateway;
import it.unibo.controller.shared.network.sockets.packets.ExplicitDisconnectPacket;
import it.unibo.controller.shared.network.sockets.packets.JoinServerPacket;
import it.unibo.controller.shared.network.sockets.packets.UdpHandshakePacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Implementation of {@link ClientGameSessionManager} coordinating the connection
 * lifecycle of a game client over a hybrid TCP/UDP architecture.
 */
public class ClientGameSessionManagerImpl implements ClientGameSessionManager {
    private static final Logger logger = LoggerFactory.getLogger(ClientGameSessionManagerImpl.class);

    private static final long HANDSHAKE_START_DELAY_MS = 0;
    private static final long UDP_HANDSHAKE_TIMEOUT_MS = 5000;
    public static final int UDP_HANDSHAKE_RETRY_INTERVAL_MS = 500;

    private final ReentrantLock lock = new ReentrantLock();

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private ScheduledFuture<?> handshakeTask;
    private ScheduledFuture<?> handshakeTimeoutTask;

    private final GameClientGateway gateway;
    private final String username;

    private final List<ClientSessionListener> listeners = new ArrayList<>();

    private ConnectionState state = ConnectionState.DISCONNECTED;
    private String udpToken;

    public ClientGameSessionManagerImpl(GameClientGateway gateway, String username) {
        this.gateway = gateway;
        this.username = username;
    }

    @Override
    public void addListener(ClientSessionListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public void joinServer() {
        lock.lock();
        try {
            if (state != ConnectionState.DISCONNECTED && state != ConnectionState.FAILED) return;
            gateway.sendTcp(new JoinServerPacket(username));
            setStateAndNotify(ConnectionState.CONNECTING);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void onJoinAck(String token) {
        lock.lock();
        try {
            if (state != ConnectionState.CONNECTING) return;
            this.udpToken = token;
            setStateAndNotify(ConnectionState.HANDSHAKING);
            handshakeTask = scheduler.scheduleAtFixedRate(
                    this::sendHandshake,
                    HANDSHAKE_START_DELAY_MS,
                    UDP_HANDSHAKE_RETRY_INTERVAL_MS,
                    TimeUnit.MILLISECONDS
            );
            handshakeTimeoutTask = scheduler.schedule(
                    this::handshakeFailed,
                    UDP_HANDSHAKE_TIMEOUT_MS,
                    TimeUnit.MILLISECONDS
            );
        } finally {
            lock.unlock();
        }
    }

    private void sendHandshake() {
        lock.lock();
        try {
            if (state != ConnectionState.HANDSHAKING || udpToken == null) return;
            gateway.sendUdp(new UdpHandshakePacket(udpToken));
        } finally {
            lock.unlock();
        }
    }

    private void handshakeFailed() {
        lock.lock();
        try {
            if (state != ConnectionState.HANDSHAKING) return;
            logger.warn("UDP Handshake timed out for user: {}", username);
            cleanupHandshake();
            setStateAndNotify(ConnectionState.FAILED);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void onUdpReady() {
        lock.lock();
        try {
            if (state != ConnectionState.HANDSHAKING) return;
            cleanupHandshake();
            setStateAndNotify(ConnectionState.CONNECTED);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void onConnectionLost() {
        lock.lock();
        try {
            if (state == ConnectionState.DISCONNECTED || state == ConnectionState.FAILED || state == ConnectionState.LOST) return;
            ConnectionState previousState = this.state;
            cleanupHandshake();
            setStateAndNotify(previousState == ConnectionState.CONNECTED ? ConnectionState.LOST : ConnectionState.FAILED);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void disconnect() {
        lock.lock();
        try {
            cleanupHandshake();
            if (state != ConnectionState.DISCONNECTED) {
                gateway.sendTcp(new ExplicitDisconnectPacket());
            }
            setStateAndNotify(ConnectionState.DISCONNECTED);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void close() {
        lock.lock();
        try {
            cleanupHandshake();
            scheduler.shutdownNow();
        } finally {
            lock.unlock();
        }
    }

    private void setStateAndNotify(ConnectionState newState) {
        boolean isChanged = false;
        lock.lock();
        try {
            if (this.state != newState) {
                this.state = newState;
                isChanged = true;
            }
        } finally {
            lock.unlock();
        }
        if (isChanged) {
            for (ClientSessionListener listener : listeners) {
                listener.onConnectionStateChanged(newState);
            }
        }
    }

    private void cleanupHandshake() {
        if (handshakeTask != null) handshakeTask.cancel(false);
        if (handshakeTimeoutTask != null) handshakeTimeoutTask.cancel(false);
        handshakeTask = null;
        handshakeTimeoutTask = null;
        udpToken = null;
    }
}