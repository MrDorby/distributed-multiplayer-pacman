package it.unibo.controller.server;

import java.net.URI;

public record GameServicesConfig(
        BackupConfig backup,
        ResultsConfig results,
        MatchmakingConfig matchmaking,
        AgonesConfig agones
) {
    public record BackupConfig(URI endpoint) {}
    public record ResultsConfig(URI endpoint) {}
    public record MatchmakingConfig(URI endpoint) {}
    public record AgonesConfig(URI endpoint) {}

    public static GameServicesConfig fromEnv() {
        return new GameServicesConfig(
                new BackupConfig(getUri("BACKUP_SERVICE_URL", "")),
                new ResultsConfig(getUri("RESULTS_SERVICE_URL", "")),
                new MatchmakingConfig(getUri("MATCHMAKER_URL", "")),
                new AgonesConfig(getUri("AGONES_SDK_URL", ""))
        );
    }

    private static URI getUri(String environmentVariable, String defaultValue) {
        return URI.create(getEnv(environmentVariable, defaultValue));
    }

    private static String getEnv(String environmentVariable, String defaultValue) {
        return System.getenv().getOrDefault(environmentVariable, defaultValue);
    }
}