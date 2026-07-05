package it.unibo.controller.server.network;

import io.javalin.Javalin;
import io.javalin.http.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Generic HTTP server implemented with Javalin. Endpoints can be
 * registered via {@link #addGetEndpoint(String, Handler)} before calling {@link #start()}.
 */
public class GameHttpServer {
    private final Logger logger = LoggerFactory.getLogger(GameHttpServer.class);
    private final int port;
    private final Map<String, Handler> getEndpoints = new LinkedHashMap<>();
    private Javalin app;

    /**
     * @param port the port this server will listen on once {@link #start()} is called
     */
    public GameHttpServer(int port) {
        this.port = port;
    }

    /**
     * Registers a GET endpoint. Must be called before {@link #start()}.
     * @param path    the URL path to handle (e.g. "/health")
     * @param handler the handler invoked for requests to this path
     */
    public void addGetEndpoint(String path, Handler handler) {
        if (app != null) {
            throw new IllegalStateException("Cannot add endpoints after the server has started");
        }
        getEndpoints.put(path, handler);
    }

    /**
     * Builds the HTTP server with the registered endpoints and starts listening.
     */
    public void start() {
        this.app = Javalin.create(config -> {
            getEndpoints.forEach(config.routes::get);
            config.routes.exception(Exception.class, (e, ctx) -> {
                logger.error("Unhandled exception while processing {} {}", ctx.method(), ctx.path(), e);
                ctx.status(500).result("Internal error");
            });
        });
        this.app.start(port);
    }

    /**
     * Stops the HTTP server.
     */
    public void stop() {
        if (app != null) {
            app.stop();
        }
    }
}