package it.unibo;

import it.unibo.controller.server.GameContextServerController;
import it.unibo.controller.server.GameServerControllerFactory;
import it.unibo.model.entities.GameEntityFactoryImpl;
import it.unibo.model.game.GameContext;
import it.unibo.model.game.GameContextFactory;
import it.unibo.model.game.GameImpl;

public class GameServerMain {
    private static final String DEFAULT_MAP_NAME = "map1";
    private static final String MAP_PATH_FORMAT = "maps/%s.json";
    private static final int DEFAULT_TCP_PORT = 7777 ;
    private static final int DEFAULT_UDP_PORT = 7778;

    /**
     * Launches the game server.
     *
     * <p>Usage: {@code GameServerMain [mapPath] [tcpPort] [udpPort]}
     */
    static void main(String[] args) throws Exception {
        String mapName = args.length > 0 ? args[0] : DEFAULT_MAP_NAME;
        int tcpPort = args.length > 1 ? Integer.parseInt(args[1]) : DEFAULT_TCP_PORT;
        int udpPort = args.length > 2 ?  Integer.parseInt(args[2]) : DEFAULT_UDP_PORT;
        String mapPath = MAP_PATH_FORMAT.formatted(mapName);
        GameContext context = GameContextFactory.createFromMap(mapPath, new GameEntityFactoryImpl());
        GameContextServerController controller = GameServerControllerFactory.withDummyPersistence(new GameImpl(context), tcpPort, udpPort);
    }
}
