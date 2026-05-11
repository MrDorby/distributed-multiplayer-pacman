package it.unibo.controller.engine;

import it.unibo.controller.commands.PacmanCommand;
import it.unibo.model.collisions.CollisionManagerImpl;
import it.unibo.model.game.Game;
import it.unibo.model.game.GameContextFactory;
import it.unibo.model.game.GameImpl;

import java.time.Duration;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Fixed-rate game loop running at {@value TICKS_PER_SECOND} ticks per second.
 *
 * <p>Each iteration of the loop measures how much real time has elapsed since
 * the previous iteration and accumulates it as lag. The loop then drains that
 * lag in fixed-size tick steps, calling {@link #update()} once per step.
 *
 * <p>To avoid the scenario where a slow frame causes many catch-up
 * ticks that the next frame is even slower, catch-up is capped at
 * {@value MAX_CATCHUP_TICKS} ticks per iteration. If the loop falls further
 * behind than that, such ticks are dropped.
 */
public class GameEngineImpl implements GameEngine {
    private static final int TICKS_PER_SECOND = 64;
    private static final long NANOS_PER_TICK = 1_000_000_000L / TICKS_PER_SECOND;
    private static final long NANOS_PER_SECOND = 1_000_000_000L;
    private static final long NANOS_PER_MILLI = 1_000_000L;
    private static final int MAX_CATCHUP_TICKS = 5;

    private final Game game;
    private final Queue<PacmanCommand> commandQueue = new ConcurrentLinkedQueue<>();
    private volatile boolean running;
    private volatile int currentTps = 0;

    private int tickCount = 0;

    public GameEngineImpl(Game game) {
        this.game = game;
        this.running = true;
    }

    @Override
    public void enqueueCommand(PacmanCommand command) {
        commandQueue.add(command);
    }

    @Override
    public int getCurrentTps() {
        return currentTps;
    }

    @Override
    public boolean isRunning() {
        return running;
    }

    @Override
    public void start() {
        long previousTime = System.nanoTime();
        long lastTpsTime = previousTime;
        long lag = 0;
        while (running) {
            long currentTime = System.nanoTime();
            // Add elapsed time to the lag accumulator
            lag = accumulateLag(lag, currentTime - previousTime);
            previousTime = currentTime;
            // Drain lag by running as many fixed-size ticks as it allows
            lag = processTicks(lag);
            // Snapshot TPS once per second
            lastTpsTime = updateTps(currentTime, lastTpsTime);
            // Sleep until the next tick is due
            sleepUntilNextTick(lag);
        }
    }

    @Override
    public void stop() {
        this.running = false;
    }

    private long accumulateLag(long lag, long elapsed) {
        return Math.min(lag + elapsed, NANOS_PER_TICK * MAX_CATCHUP_TICKS);
    }

    private long processTicks(long lag) {
        // Drain as many ticks as the elapsed time allows, up to the catchup cap
        int ticksThisFrame = 0;
        while (lag >= NANOS_PER_TICK && ticksThisFrame < MAX_CATCHUP_TICKS) {
            update();
            lag -= NANOS_PER_TICK;
            tickCount++;
            ticksThisFrame++;
        }
        return lag;
    }

    private long updateTps(long currentTime, long lastTpsTime) {
        // Snapshots only once per second
        if (currentTime - lastTpsTime < NANOS_PER_SECOND) return lastTpsTime;
        currentTps = tickCount;
        System.out.println("TPS: " + currentTps);
        tickCount = 0;
        return currentTime;
    }

    /**
     * Thread.sleep has 1ms granularity, so sleep until 1ms before the next tick
     * to avoid overshooting and waking up too late.
     * Example: at 64 TPS each tick is ~15.6ms. If 3ms of lag has accumulated,
     * timeUntilNextTick is 12.6ms, so we sleep 11.6ms (truncated to 11ms),
     * leaving 1ms as a buffer to avoid oversleeping.
     */
    private void sleepUntilNextTick(long lag) {
        long timeUntilNextTick = NANOS_PER_TICK - lag;
        if (timeUntilNextTick > NANOS_PER_MILLI) {
            try {
                Thread.sleep((timeUntilNextTick - NANOS_PER_MILLI) / NANOS_PER_MILLI);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                running = false;
            }
        } else {
            // For the last <1ms, hint to the CPU that we're busy-waiting.
            Thread.onSpinWait();
        }
    }

    private void update() {
        Duration tickDuration = Duration.ofNanos(NANOS_PER_TICK);
        while (!commandQueue.isEmpty()) {
            PacmanCommand command = commandQueue.poll();
            if (command != null) {
                command.execute(game);
            }
        }
        game.update(tickDuration);
        // view.render(null);
    }

    static void main() {
        Game game = new GameImpl(GameContextFactory.getTestContext(), new CollisionManagerImpl());
        GameEngineImpl engine = new GameEngineImpl(game);
        new Thread(engine::start).start();
    }
}
