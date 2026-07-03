package it.unibo.controller.shared.network.translation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.cbor.databind.CBORMapper;
import it.unibo.controller.shared.network.dto.GameContextDTO;
import it.unibo.model.entities.GameEntityFactoryImpl;
import it.unibo.model.game.GameContext;
import it.unibo.model.game.GameContextFactory;

public class GameContextTranslationDemo {
    static void main() throws Exception {
        GameContext context = GameContextFactory.createFromMap("maps/map1.json", new GameEntityFactoryImpl());
        GameContextDTO dto = new GameContextEncoderImpl().encode(context);
        ObjectMapper jsonMapper = new ObjectMapper();
        CBORMapper cborMapper = new CBORMapper();
        String json = jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(dto);
        byte[] cbor = cborMapper.writeValueAsBytes(dto);
        // Files.writeString(Path.of("gamecontext.json"), json);
        // Files.write(Path.of("gamecontext.cbor"), cbor);
        System.out.println("JSON size: " + json.getBytes().length + " bytes");
        System.out.println("CBOR size: " + cbor.length + " bytes");
    }
}
