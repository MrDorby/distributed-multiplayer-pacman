package it.unibo.controller.server.network.sockets.channel;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import it.unibo.controller.server.network.sockets.session.GameSession;
import it.unibo.controller.server.network.sockets.session.GameSessionController;
import it.unibo.controller.shared.network.sockets.handlers.TcpHandler;
import it.unibo.controller.shared.network.sockets.packets.HeartbeatPacket;
import it.unibo.controller.shared.network.sockets.packets.NetworkPacket;
import it.unibo.controller.shared.network.sockets.packets.PacketType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

/**
 * Handles inbound TCP network traffic for the game server.
 * <p>
 * This handler is responsible for intercepting {@link HeartbeatPacket}s maintenance packets,
 * routing gameplay payload packets to their respective domain handlers and managing session lifecycle events
 * like connection drops and idle timeouts.
 * </p>
 */
public class GameServerNetworkHandler extends SimpleChannelInboundHandler<Object> {
    private static final Logger logger = LoggerFactory.getLogger(GameServerNetworkHandler.class);

    private final Map<PacketType, TcpHandler> tcpHandlers;
    private final GameSessionController controller;

    /**
     * Constructs a new network handler.
     *
     * @param tcpHandlers A lookup map of strategy handlers indexed by their packet type.
     * @param controller  The single source of truth managing player connection states and session memory.
     */
    public GameServerNetworkHandler(Map<PacketType, TcpHandler> tcpHandlers, GameSessionController controller) {
        this.tcpHandlers = tcpHandlers;
        this.controller = controller;
    }

    /**
     * Intercepts and processes fully decoded objects arriving from the pipeline.
     *
     * @param ctx The execution context of the underlying Netty channel.
     * @param msg The incoming object (either a heartbeat packet or a gameplay packet).
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
        // Intercept and immediately consume the generic heartbeat.
        // Client Ping -> Server Pong
        if (msg instanceof HeartbeatPacket) {
            logger.debug("Acknowledging heartbeat received from {}", ctx.channel().remoteAddress());
            ctx.writeAndFlush(new HeartbeatPacket());
            return;
        }
        // Route regular packets to their handlers
        if (msg instanceof NetworkPacket packet) {
            TcpHandler handler = tcpHandlers.get(packet.getType());
            if (handler != null) {
                handler.handle(ctx.channel(), packet);
            } else {
                logger.warn("No TCP handler registered for {}", packet.getType());
            }
        }
    }

    /**
     * Intercepts socket idle events raised by Netty's IdleStateHandler.
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // Catch reader-idle timeouts (i.e., this socket hasn't transmitted any data in X seconds)
        if (evt instanceof IdleStateEvent e && e.state() == IdleState.READER_IDLE) {
            GameSession session = controller.getSessionByChannel(ctx.channel());
            if (session != null) {
                // Active player silently dropped or froze. Transition them into a disconnected
                // state but keep their session data in memory for reconnection.
                logger.info("Connection with player {} at {} has timed out", session.getUsername(), session.getTcpChannel().remoteAddress());
                controller.onDisconnect(session);
            } else {
                // An unknown connection opened the socket but was never registered as a session to begin with.
                ctx.close();
            }
        } else {
            // Forward other non-idle pipeline events down to subsequent handlers
            super.userEventTriggered(ctx, evt);
        }
    }

    /**
     * Automatically triggered when the physical TCP connection is cut abruptly.
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        GameSession session = controller.getSessionByChannel(ctx.channel());
        if (session != null) {
            controller.onDisconnect(session);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (cause instanceof IOException) {
            logger.warn("Connection with {} was lost abruptly: {}", ctx.channel().remoteAddress(), cause.getMessage());
        } else {
            logger.error("Unexpected exception in network pipeline:", cause);
        }
        ctx.close();
    }
}