package it.unibo.controller.shared.network.sockets.packets;

import it.unibo.controller.shared.network.dto.GameContextDTO;

/**
 * Transmitted by the server to deliver a complete snapshot of the game state.
 * <ul>
 * <li>Over TCP: Transmitted during critical events (e.g., initial join, reconnection)
 * where guaranteed delivery state is mandatory.</li>
 * <li>Over UDP: Transmitted periodically at a high frequency during active gameplay,
 * allowing clients to quickly drop stale data and override their local simulation with the
 * latest authoritative state.</li>
 * </ul>
 */
public record GameContextPacket(GameContextDTO context) implements NetworkPacket {
    @Override
    public PacketType getType() {
        return PacketType.GAME_CONTEXT;
    }
}
