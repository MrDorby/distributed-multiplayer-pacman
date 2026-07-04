package it.unibo.controller.server;

import it.unibo.controller.server.persistence.backup.DummyGameBackupService;
import it.unibo.controller.server.persistence.backup.HttpGameBackupService;
import it.unibo.controller.server.persistence.results.DummyGameResultsService;
import it.unibo.controller.server.persistence.results.HttpGameResultsService;
import it.unibo.model.game.Game;

import java.net.URI;

public class GameServerControllerFactory {
    public static GameContextServerController withDummyPersistence(Game game, int tcpPort, int udpPort) throws Exception {
        return new GameContextServerController(game, tcpPort, udpPort,
                new DummyGameBackupService(),
                new DummyGameResultsService());
    }

    // TODO: endpoints could be read from a config file instead of passed in directly
    public static GameContextServerController withHttpPersistence(Game game, int tcpPort, int udpPort,
                                                                  URI backupEndpoint, URI resultsEndpoint) throws Exception {
        return new GameContextServerController(game, tcpPort, udpPort,
                new HttpGameBackupService(backupEndpoint),
                new HttpGameResultsService(resultsEndpoint));
    }
}
