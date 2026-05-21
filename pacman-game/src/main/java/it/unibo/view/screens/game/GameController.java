package it.unibo.view.screens.game;

import it.unibo.controller.engine.GameEngine;
import it.unibo.controller.engine.GameEngineImpl;
import it.unibo.model.collisions.CollisionManagerImpl;
import it.unibo.model.game.Game;
import it.unibo.model.game.GameContext;
import it.unibo.model.game.GameContextFactory;
import it.unibo.model.game.GameImpl;
import it.unibo.view.GameViewImpl;
import it.unibo.view.navigation.AppNavigator;
import it.unibo.view.navigation.AppState;
import it.unibo.view.screens.ScreenController;

import javax.swing.*;

public class GameController implements ScreenController {
    private GameViewImpl gameView;
    private final AppNavigator navigator;
    private GameEngine engine;

    public GameController(AppNavigator navigator) {
        this.navigator = navigator;
    }

    @Override
    public void onEnter() {
        GameContext context = GameContextFactory.getTestContext();
        Game game = new GameImpl(context, new CollisionManagerImpl());
        this.engine = new GameEngineImpl(game);
        this.gameView = new GameViewImpl(engine, context);
        this.engine.setView(gameView);
        ((GamePanel) this.gameView.getGamePanel()).onEscape(() -> navigator.goTo(AppState.MAIN_MENU));
        new Thread(engine::start).start();
        SwingUtilities.invokeLater(gameView.getGamePanel()::requestFocusInWindow);
    }

    @Override
    public void onExit() {
        engine.stop();
    }

    @Override
    public JPanel getPanel() {
        return gameView.getGamePanel();
    }
}