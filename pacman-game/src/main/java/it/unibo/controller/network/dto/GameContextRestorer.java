package it.unibo.controller.network.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.cbor.databind.CBORMapper;
import it.unibo.model.common.MatrixCoordinates;
import it.unibo.model.entities.Dot;
import it.unibo.model.entities.GameEntityFactoryImpl;
import it.unibo.model.entities.Ghost;
import it.unibo.model.entities.Pacman;
import it.unibo.model.game.GameContext;
import it.unibo.model.game.GameContextFactory;
import it.unibo.model.game.GameContextImpl;
import it.unibo.model.map.GameMap;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class GameContextRestorer {
    private final GameContextRestoreFactory factory;
    private GameMap cachedMap;

    public GameContextRestorer(GameContextRestoreFactory factory) {
        this.factory = factory;
    }

    public GameContext restore(GameContextDTO dto) {
        if (cachedMap == null) {
            cachedMap = factory.restoreMap(dto.mapName());
        }
        Map<MatrixCoordinates, Dot> dotsMap = dto.dots().stream()
                .collect(Collectors.toMap(d -> new MatrixCoordinates(d.row(), d.col()), factory::restoreDot));
        Set<Ghost> ghosts = dto.ghosts().stream()
                .map(g -> factory.restoreGhost(g, cachedMap))
                .collect(Collectors.toSet());
        Set<Pacman> pacmans = dto.pacmans().stream()
                .map(p -> factory.restorePacman(p, cachedMap))
                .collect(Collectors.toSet());
        return new GameContextImpl(cachedMap, dotsMap, ghosts, pacmans, dto.gameState().timeLeftInMillis());
    }

    static void main() throws IOException {
        GameContext context = GameContextFactory.createFromMap("maps/map1.json", new GameEntityFactoryImpl());
        GameContextDTO dto = GameContextMapper.toDTO(context);
        CBORMapper cborMapper = new CBORMapper();
        ObjectMapper jsonMapper = new ObjectMapper();

        byte[] cbor = cborMapper.writeValueAsBytes(dto);
        String json = jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(dto);

        Files.write(Path.of("gamecontext.cbor"), cbor);
        Files.writeString(Path.of("gamecontext.json"), json);

        System.out.println("CBOR size: " + cbor.length + " bytes");
        System.out.println("JSON size: " + json.getBytes().length + " bytes");
    }
}