package it.unibo.controller.shared.engine.event;

import it.unibo.controller.shared.network.dto.GameContextDTO;

public record GameEndedEvent(GameContextDTO context) implements GameEvent {
}
