package it.unibo.controller.server.engine;

/**
 * Runs an action once every N ticks, where N is derived from a desired target rate
 * relative to the rate at which {@link #tick()} is called.
 * <p>
 * Useful for reducing the frequency of an expensive or high-volume action (e.g.
 * broadcasting state to clients).
 */
public class TickThrottle {
    private final int ticksPerAction;
    private final Runnable action;
    private int counter = 0;

    private TickThrottle(int ticksPerAction, Runnable action) {
        this.ticksPerAction = ticksPerAction;
        this.action = action;
    }

    /**
     * Creates a throttle that runs the given action approximately {@code targetRateHz}
     * times per second, given that {@link #tick()} will be called at {@code sourceTickRateHz}
     * times per second.
     *
     * @param sourceTickRateHz the rate, in ticks per second, at which {@link #tick()} will be called
     * @param targetRateHz     the desired rate at which the action should run, in times per second
     * @param action           the action to run when the throttle fires
     * @return a {@link TickThrottle} configured for the given rate
     * @throws IllegalArgumentException if {@code targetRateHz} is not positive or exceeds {@code sourceTickRateHz}
     */
    public static TickThrottle atRate(int sourceTickRateHz, int targetRateHz, Runnable action) {
        if (targetRateHz <= 0 || targetRateHz > sourceTickRateHz) {
            throw new IllegalArgumentException("targetRateHz must be > 0 and <= sourceTickRateHz (got " + targetRateHz + ")");
        }
        int intervalTicks = Math.round((float) sourceTickRateHz / targetRateHz);
        return new TickThrottle(intervalTicks, action);
    }

    /**
     * Advances the throttle by one tick, running the action once the configured
     * interval has been reached and resetting the internal counter.
     */
    public void tick() {
        counter++;
        if (counter >= ticksPerAction) {
            counter = 0;
            action.run();
        }
    }
}
