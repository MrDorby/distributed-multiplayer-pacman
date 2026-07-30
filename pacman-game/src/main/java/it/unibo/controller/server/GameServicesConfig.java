package it.unibo.controller.server;

import java.net.URI;

public record GameServicesConfig(
        ShortTermDB shortTermDB,
        LongTermDB longTermDB
) {
    public static final String SHORT_TERM_DB_KEY = "SHORT_TERM_DB_URI";
    public static final String LONG_TERM_DB_KEY = "LONG_TERM_DB_URI";

    public record ShortTermDB(URI endpoint) {}
    public record LongTermDB(URI endpoint) {}

    public static GameServicesConfig fromEnv() {
        return new GameServicesConfig(
                new ShortTermDB(getUri(SHORT_TERM_DB_KEY)),
                new LongTermDB(getUri(LONG_TERM_DB_KEY))
        );
    }

    private static URI getUri(String key) {
        return URI.create(System.getenv().getOrDefault(key, ""));
    }
}