package it.unibo.view.panels;

import it.unibo.view.navigation.AppNavigator;
import it.unibo.view.navigation.AppState;

import javax.swing.*;

public class GameController implements ScreenController {
    private final GamePanel panel;
    private final AppNavigator navigator;

    public GameController(AppNavigator navigator) {
        this.navigator = navigator;
        this.panel = new GamePanel();
        panel.onEscape(() -> navigator.goTo(AppState.MAIN_MENU));
    }

    @Override
    public void onEnter() {
        // TODO connect to game engine
        panel.setCommandSink(command -> System.out.println("Controller received: " + command.getClass().getSimpleName()));
        SwingUtilities.invokeLater(panel::requestFocusInWindow);
    }

    @Override
    public void onExit() {

    }

    @Override
    public JPanel getPanel() {
        return panel;
    }
}