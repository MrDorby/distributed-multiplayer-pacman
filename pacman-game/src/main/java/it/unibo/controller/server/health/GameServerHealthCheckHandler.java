package it.unibo.controller.server.health;

import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * Handles requests to the health check endpoint by serializing the
 * game server's current {@link GameServerStatus} as JSON.
 */
public class GameServerHealthCheckHandler implements Handler {
    private final Supplier<GameServerStatus> statusSupplier;

    public GameServerHealthCheckHandler(Supplier<GameServerStatus> statusSupplier) {
        this.statusSupplier = statusSupplier;
    }

    @Override
    public void handle(@NotNull Context ctx) {
        ctx.json(statusSupplier.get());
    }
}