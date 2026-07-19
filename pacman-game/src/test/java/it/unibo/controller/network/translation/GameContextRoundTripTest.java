package it.unibo.controller.network.translation;

import it.unibo.controller.shared.network.dto.GameContextDTO;
import it.unibo.controller.shared.network.translation.GameContextDecoderImpl;
import it.unibo.controller.shared.network.translation.GameContextEncoderImpl;
import it.unibo.model.entities.GameEntityFactoryImpl;
import it.unibo.model.entities.SpeculativeEntityFactoryImpl;
import it.unibo.model.game.GameContext;
import it.unibo.model.game.GameContextFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GameContextRoundTripTest {
    private static GameContext context;
    private final GameContextEncoderImpl encoder = new GameContextEncoderImpl();
    private final GameContextDecoderImpl decoder = new GameContextDecoderImpl(new SpeculativeEntityFactoryImpl());

    @BeforeAll
    static void setUp() {
        context = GameContextFactory.createFromMap("maps/map1.json", new GameEntityFactoryImpl());
    }

    @Test
    void roundTripPreservesPacmanCount() {
        GameContextDTO dto = encoder.encode(context);
        GameContext restored = decoder.decode(dto);
        assertEquals(context.getPacmans().size(), restored.getPacmans().size());
    }

    @Test
    void roundTripPreservesGhostCount() {
        GameContextDTO dto = encoder.encode(context);
        GameContext restored = decoder.decode(dto);
        assertEquals(context.getGhosts().size(), restored.getGhosts().size());
    }

    @Test
    void roundTripPreservesDotCount() {
        GameContextDTO dto = encoder.encode(context);
        GameContext restored = decoder.decode(dto);
        assertEquals(context.getDotsMap().size(), restored.getDotsMap().size());
    }

    @Test
    void roundTripPreservesGameState() {
        GameContextDTO dto = encoder.encode(context);
        GameContext restored = decoder.decode(dto);
        assertEquals(context.getGameState().getTimeLeftInMillis(), restored.getGameState().getTimeLeftInMillis());
        assertEquals(context.getGameState().isGameOver(), restored.getGameState().isGameOver());
    }
}