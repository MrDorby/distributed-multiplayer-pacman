package it.unibo;

import it.unibo.controller.engine.GameEngineImpl;
import it.unibo.model.collisions.CollisionManagerImpl;
import it.unibo.model.game.Game;
import it.unibo.model.game.GameContextFactory;
import it.unibo.model.game.GameImpl;
import it.unibo.view.HeadlessView;

public class HeadlessGameMain {
    static void main() {
        Game game = new GameImpl(GameContextFactory.getTestContext(), new CollisionManagerImpl());
        GameEngineImpl engine = new GameEngineImpl(game);
        engine.setView(new HeadlessView());
        new Thread(engine::start).start();
    }
}
