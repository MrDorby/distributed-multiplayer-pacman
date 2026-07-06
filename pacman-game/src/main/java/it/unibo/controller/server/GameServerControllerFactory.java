package it.unibo.controller.server;

import it.unibo.controller.server.persistence.backup.DummyGameBackupService;
import it.unibo.controller.server.persistence.backup.HttpGameBackupService;
import it.unibo.controller.server.persistence.results.DummyGameResultsService;
import it.unibo.controller.server.persistence.results.HttpGameResultsService;
import it.unibo.model.game.Game;

import java.net.URI;
import java.net.http.HttpClient;

public class GameServerControllerFactory {
    public static GameServerController withDummyPersistence(Game game, int tcpPort, int udpPort, int httpPort) throws Exception {
        return new GameServerController(game, tcpPort, udpPort, httpPort,
                new DummyGameBackupService(),
                new DummyGameResultsService());
    }

    // TODO: endpoints could be read from a config file instead of passed in directly
    public static GameServerController withHttpPersistence(Game game, int tcpPort, int udpPort, int httpPort,
                                                           URI backupEndpoint, URI resultsEndpoint) throws Exception {
        try (HttpClient httpClient = HttpClient.newHttpClient()) {
            return new GameServerController(game, tcpPort, udpPort, httpPort,
                    new HttpGameBackupService(httpClient, backupEndpoint),
                    new HttpGameResultsService(httpClient, resultsEndpoint));
        }
    }
}
