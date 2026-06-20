package it.unibo.controller.engine;

import it.unibo.controller.commands.PacmanCommand;
import it.unibo.model.game.Game;
import it.unibo.model.game.GameContext;
import it.unibo.view.GameView;
import it.unibo.view.HeadlessView;

import java.time.Duration;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Fixed-rate game loop running at {@value TICKS_PER_SECOND} ticks per second.
 *
 * <p>Each iteration of the loop measures how much real time has elapsed since
 * the previous iteration and accumulates it as lag. The loop then drains that
 * lag in fixed-size tick steps, calling {@link #tick()} once per step.
 *
 * <p>To avoid the scenario where a slow frame causes many catch-up
 * ticks that the next frame is even slower, catch-up is capped at
 * {@value MAX_CATCHUP_TICKS} ticks per iteration. If the loop falls further
 * behind than that, such ticks are dropped.
 */
public abstract class AbstractFixedTimeStepGameEngine implements GameEngine {
    private static final int TICKS_PER_SECOND = 64;
    private static final long NANOS_PER_TICK = 1_000_000_000L / TICKS_PER_SECOND;
    private static final long NANOS_PER_SECOND = 1_000_000_000L;
    private static final long NANOS_PER_MILLI = 1_000_000L;
    private static final int MAX_CATCHUP_TICKS = 5;

    protected Game game;
    private GameView view = new HeadlessView();
    private final Queue<PacmanCommand> commandQueue = new ConcurrentLinkedQueue<>();

    private volatile boolean running = true;
    private volatile int currentTps = 0;

    public AbstractFixedTimeStepGameEngine(Game game) {
        this.game = game;
    }

    protected abstract void beforeTick();

    protected abstract void afterCommandExecuted(PacmanCommand command);

    protected abstract void afterTick();

    @Override
    public void enqueueCommand(PacmanCommand command) {
        commandQueue.add(command);
    }

    @Override
    public GameContext getCurrentContext() {
        return this.game.getContext();
    }

    @Override
    public void start() {
        long previousTime = System.nanoTime();
        long lastTpsTime = previousTime;
        long lag = 0;
        int tickCount = 0;

        while (running) {
            long currentTime = System.nanoTime();
            long elapsed = currentTime - previousTime;
            previousTime = currentTime;

            lag = Math.min(lag + elapsed, NANOS_PER_TICK * MAX_CATCHUP_TICKS);

            int ticksThisFrame = 0;
            while (lag >= NANOS_PER_TICK && ticksThisFrame < MAX_CATCHUP_TICKS) {
                tick();
                lag -= NANOS_PER_TICK;
                tickCount++;
                ticksThisFrame++;
            }

            if (currentTime - lastTpsTime >= NANOS_PER_SECOND) {
                currentTps = tickCount;
                tickCount = 0;
                lastTpsTime = currentTime;
            }

            long timeUntilNextTick = NANOS_PER_TICK - lag;
            if (timeUntilNextTick > NANOS_PER_MILLI) {
                try {
                    Thread.sleep((timeUntilNextTick - NANOS_PER_MILLI) / NANOS_PER_MILLI);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    running = false;
                }
            } else {
                Thread.onSpinWait();
            }
        }
    }

    private void tick() {
        beforeTick();
        while (!commandQueue.isEmpty()) {
            PacmanCommand command = commandQueue.poll();
            if (command != null) {
                command.execute(game);
                afterCommandExecuted(command);
            }
        }
        game.update(Duration.ofNanos(NANOS_PER_TICK));
        view.render(game.getContext());
        afterTick();
    }

    @Override
    public void stop() {
        this.running = false;
    }

    @Override
    public void setView(GameView view) {
        this.view = (view != null) ? view : new HeadlessView();
    }

    @Override
    public int getCurrentTps() {
        return currentTps;
    }

    @Override
    public boolean isRunning() {
        return running;
    }
}
