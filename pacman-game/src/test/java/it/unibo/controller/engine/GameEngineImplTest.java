package it.unibo.controller.engine;

import it.unibo.model.game.GameImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameEngineImplTest {

    GameEngineImpl engine;

    @BeforeEach
    void setUp() {
        engine = new GameEngineImpl(new GameImpl(null));
    }

    @Test
    void engineStartsAndStops() throws InterruptedException {
        Thread thread = new Thread(engine::start);
        thread.start();
        assertTrue(engine.isRunning());
        engine.stop();
        thread.join(500);
        assertFalse(engine.isRunning());
    }

    @Test
    void doesNotExceedTickRate() throws InterruptedException {
        Thread thread = new Thread(engine::start);
        thread.start();
        Thread.sleep(2000);
        engine.stop();
        thread.join(500);
        assert(engine.getCurrentTps() <= 64);
    }
}