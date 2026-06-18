package it.unibo.controller.network.packets;

import java.util.Map;

public record LobbyStatusPacket(Map<String, Boolean> playerReadyStates) {}