package it.unibo.controller.engine;

import it.unibo.controller.server.engine.TickThrottle;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TickThrottleTest {
    @Test
    void firesActionEveryConfiguredInterval() {
        AtomicInteger callCount = new AtomicInteger();
        TickThrottle throttle = TickThrottle.atRate(64, 16, callCount::incrementAndGet);
        // 64/16 = 4 ticks per firing; simulate 12 ticks -> expect 3 firings
        for (int i = 0; i < 12; i++) {
            throttle.tick();
        }
        assertEquals(3, callCount.get());
    }

    @Test
    void doesNotFireBeforeIntervalIsReached() {
        AtomicInteger callCount = new AtomicInteger();
        TickThrottle throttle = TickThrottle.atRate(64, 16, callCount::incrementAndGet);
        // interval is 4 ticks; only 3 ticks should not trigger a firing yet
        for (int i = 0; i < 3; i++) {
            throttle.tick();
        }
        assertEquals(0, callCount.get());
    }

    @Test
    void firesOnEveryTickWhenTargetRateEqualsSourceRate() {
        AtomicInteger callCount = new AtomicInteger();
        TickThrottle throttle = TickThrottle.atRate(64, 64, callCount::incrementAndGet);
        for (int i = 0; i < 5; i++) {
            throttle.tick();
        }
        assertEquals(5, callCount.get());
    }

    @Test
    void throwsWhenTargetRateIsZeroOrNegative() {
        assertThrows(IllegalArgumentException.class, () -> TickThrottle.atRate(64, 0, () -> {}));
        assertThrows(IllegalArgumentException.class, () -> TickThrottle.atRate(64, -5, () -> {}));
    }

    @Test
    void throwsWhenTargetRateExceedsSourceRate() {
        assertThrows(IllegalArgumentException.class, () -> TickThrottle.atRate(64, 65, () -> {}));
    }
}
