package it.unibo.controller.server;

import it.unibo.controller.server.persistence.backup.DummyGameBackupService;
import it.unibo.controller.server.persistence.backup.HttpGameBackupService;
import it.unibo.controller.server.persistence.results.DummyGameResultsService;
import it.unibo.controller.server.persistence.results.HttpGameResultsService;
import it.unibo.model.game.Game;

import java.net.URI;
import java.net.http.HttpClient;

public class GameServerControllerFactory {
    public static GameServerControllerImpl withDummyPersistence(Game game, int tcpPort, int udpPort) {
        return new GameServerControllerImpl(game, tcpPort, udpPort,
                new DummyGameBackupService(),
                new DummyGameResultsService());
    }

    // TODO: endpoints could be read from a config file instead of passed in directly
    public static GameServerControllerImpl withHttpPersistence(Game game, int tcpPort, int udpPort,
                                                               URI backupEndpoint, URI resultsEndpoint) {
        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            return new GameServerControllerImpl(game, tcpPort, udpPort,
                    new HttpGameBackupService(httpClient, backupEndpoint),
                    new HttpGameResultsService(httpClient, resultsEndpoint));
        }
    }
}
