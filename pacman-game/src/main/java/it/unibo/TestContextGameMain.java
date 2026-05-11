package it.unibo;

import it.unibo.controller.engine.GameEngine;
import it.unibo.controller.engine.GameEngineImpl;
import it.unibo.model.collisions.CollisionManagerImpl;
import it.unibo.model.game.Game;
import it.unibo.model.game.GameContext;
import it.unibo.model.game.GameContextFactory;
import it.unibo.model.game.GameImpl;
import it.unibo.view.GameView;
import it.unibo.view.GameViewImpl;

public class TestContextGameMain {
    static void main() {
        GameContext context = GameContextFactory.getTestContext();
        Game game = new GameImpl(context, new CollisionManagerImpl());
        GameEngine engine = new GameEngineImpl(game);
        GameView view = new GameViewImpl(engine, game.getContext());
        engine.setView(view);
        view.show();
        new Thread(engine::start).start();
    }
}
