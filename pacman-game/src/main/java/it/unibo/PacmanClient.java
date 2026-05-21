package it.unibo;

import it.unibo.view.navigation.AppState;
import it.unibo.view.navigation.ScreenRouter;
import it.unibo.view.screens.game.GameController;
import it.unibo.view.screens.login.LoginController;
import it.unibo.view.screens.menu.MainMenuController;

import javax.swing.*;
import java.awt.*;

public class PacmanClient {
    private final JFrame frame = new JFrame("Pacman");
    private final static int WIDTH_FRAME = Toolkit.getDefaultToolkit().getScreenSize().width;
    private final static int HEIGHT_FRAME = Toolkit.getDefaultToolkit().getScreenSize().height;

    public PacmanClient() {
        frame.setSize(WIDTH_FRAME, HEIGHT_FRAME);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ScreenRouter router = new ScreenRouter(frame);
        router.register(AppState.LOGIN, new LoginController(router));
        router.register(AppState.MAIN_MENU, new MainMenuController(router));
        router.register(AppState.IN_GAME, new GameController(router));
        router.goTo(AppState.LOGIN);
    }

    public void start() {
        frame.setVisible(true);
    }

    static void main() {
        SwingUtilities.invokeLater(() -> {
            PacmanClient app = new PacmanClient();
            app.start();
        });
    }
}