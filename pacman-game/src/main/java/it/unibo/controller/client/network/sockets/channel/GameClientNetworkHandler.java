package it.unibo.controller.client.network.sockets.channel;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import it.unibo.controller.client.network.sockets.session.ClientGameSessionManager;
import it.unibo.controller.shared.network.sockets.handlers.TcpHandler;
import it.unibo.controller.shared.network.sockets.packets.HeartbeatPacket;
import it.unibo.controller.shared.network.sockets.packets.NetworkPacket;
import it.unibo.controller.shared.network.sockets.packets.PacketType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

/**
 * Handles inbound TCP network traffic on the game client.
 * <p>
 * This handler is responsible for keeping the connection alive by writing outbound
 * heartbeats (pings), consuming inbound heartbeats (pongs), routing regular payloads
 * to active client-side handlers, and detecting connection loss or server timeouts.
 * </p>
 */
public class GameClientNetworkHandler extends SimpleChannelInboundHandler<Object> {
    private static final Logger logger = LoggerFactory.getLogger(GameClientNetworkHandler.class);

    private final Map<PacketType, TcpHandler> handlers;
    private final ClientGameSessionManager sessionManager;

    /**
     * Constructs a new client network handler.
     *
     * @param handlers A lookup map of strategy handlers indexed by their packet type.
     */
    public GameClientNetworkHandler(Map<PacketType, TcpHandler> handlers, ClientGameSessionManager sessionManager) {
        this.handlers = handlers;
        this.sessionManager = sessionManager;
    }

    /**
     * Intercepts and processes fully decoded objects arriving from the server.
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
        // Intercept and silently consume the heartbeat.
        // If we receive a heartbeat from the server, it serves as the 'pong' (acknowledgement)
        // confirming that the network link is active and healthy.
        if (msg instanceof HeartbeatPacket) {
            logger.debug("Heartbeat acknowledged by the server {}", ctx.channel().remoteAddress());
            return;
        }
        // Route regular packets to their handlers
        if (msg instanceof NetworkPacket packet) {
            TcpHandler handler = handlers.get(packet.getType());
            if (handler != null) {
                handler.handle(ctx.channel(), packet);
            }
        }
    }

    /**
     * Catches idle state events from Netty's IdleStateHandler to actively enforce
     * connection diagnostics (firing pings or declaring timeouts).
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent e) {
            if (e.state() == IdleState.WRITER_IDLE) {
                // The client hasn't sent any data to the server for a while.
                // We proactively send a heartbeat ping to keep the link alive and prompt a response
                logger.debug("Sending heartbeat to the server {}", ctx.channel().remoteAddress());
                ctx.writeAndFlush(new HeartbeatPacket());
            } else if (e.state() == IdleState.READER_IDLE) {
                // The server went completely silent and failed to return a heartbeat response within our timeout window.
                // We assume the server is dead or frozen.
                logger.debug("Server connection timed out. Closing socket");
                sessionManager.onConnectionLost();
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
        logger.info("TCP Channel became inactive with {}", ctx.channel().remoteAddress());
        sessionManager.onConnectionLost();
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