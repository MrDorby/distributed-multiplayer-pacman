package it.unibo.view.screens.game;

import it.unibo.view.game.GameContainer;
import it.unibo.view.navigation.AppNavigator;
import it.unibo.view.screens.ScreenController;

import javax.swing.*;

public class GameController implements ScreenController {
    private final AppNavigator navigator;
    private GameContainer gameSubsystem;

    public GameController(AppNavigator navigator) {
        this.navigator = navigator;
    }

    @Override
    public void onEnter() {
        // this.gameSubsystem = new GameSubsystemContainer();
        // this.gameSubsystem.onEscapePressed(() -> navigator.goTo(AppState.MAIN_MENU));
    }

    @Override
    public void onExit() {
//        if (gameSubsystem != null) {
//            gameSubsystem.disconnectAndCleanup();
//        }
    }

    @Override
    public JPanel getPanel() {
        return gameSubsystem;
    }
}