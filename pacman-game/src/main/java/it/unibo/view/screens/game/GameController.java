package it.unibo.view.screens.game;

import it.unibo.controller.shared.input.InputHandler;
import it.unibo.controller.shared.input.PlayerInputHandler;
import it.unibo.controller.client.engine.ClientGameEngine;
import it.unibo.controller.shared.engine.GameEngine;
import it.unibo.model.game.Game;
import it.unibo.model.game.GameContext;
import it.unibo.model.game.GameContextFactory;
import it.unibo.model.game.GameImpl;
import it.unibo.view.SwingGameView;
import it.unibo.view.navigation.AppNavigator;
import it.unibo.view.navigation.AppState;
import it.unibo.view.screens.ScreenController;

import javax.swing.*;

public class GameController implements ScreenController {
    private SwingGameView gameView;
    private final AppNavigator navigator;
    private final String localPlayerUsername;
    private GameEngine engine;

    public GameController(AppNavigator navigator, String localPlayerUsername) {
        this.navigator = navigator;
        this.localPlayerUsername = localPlayerUsername;
    }

    @Override
    public void onEnter() {
        GameContext context = GameContextFactory.getTestContext();
        Game game = new GameImpl(context);
        this.engine = new ClientGameEngine(game);
        PlayerInputHandler inputHandler = new PlayerInputHandler(localPlayerUsername);
        inputHandler.setEngine(engine);
        this.gameView = new SwingGameView(inputHandler);
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