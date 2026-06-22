package it.unibo;

import it.unibo.controller.engine.GameEngine;
import it.unibo.controller.engine.ServerGameEngine;
import it.unibo.model.game.Game;
import it.unibo.model.game.GameContextFactory;
import it.unibo.model.game.GameImpl;
import it.unibo.view.HeadlessView;

public class HeadlessPacmanGame {
    static void main() {
        Game game = new GameImpl(GameContextFactory.getTestContext());
        GameEngine engine = new ServerGameEngine(game, null);
        engine.setView(new HeadlessView());
        new Thread(engine::start).start();
    }
}
