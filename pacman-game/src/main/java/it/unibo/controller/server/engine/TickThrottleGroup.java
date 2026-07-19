package it.unibo.controller.server.engine;

import java.util.ArrayList;
import java.util.List;

/**
 * Coordinates a group of {@link TickThrottle}s driven by a single tick source.
 */
public class TickThrottleGroup {
    private final List<TickThrottle> throttles = new ArrayList<>();
    private final int sourceTickRateHz;

    /**
     * @param sourceTickRateHz the rate, in ticks per second, at which {@link #tick()} will be called;
     *                         used to scale the target rates passed to {@link #register(int, Runnable)}
     *                         into ticks per action.
     */
    public TickThrottleGroup(int sourceTickRateHz) {
        this.sourceTickRateHz = sourceTickRateHz ;
    }

    /**
     * Registers an action to run approximately {@code targetRateHz} times per second.
     *
     * @param targetRateHz the desired rate at which the action should run, in times per second.
     * @param action       the action to run when the computed throttle fires.
     */
    public void register(int targetRateHz, Runnable action) {
        throttles.add(TickThrottle.atRate(sourceTickRateHz, targetRateHz, action));
    }

    /**
     * Advances every registered throttle by one tick.
     */
    public void tick() {
        for (TickThrottle throttle : throttles) {
            throttle.tick();
        }
    }
}