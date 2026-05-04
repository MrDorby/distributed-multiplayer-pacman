package it.unibo.view;

import it.unibo.view.navigation.AppState;
import it.unibo.view.navigation.ScreenRouter;
import it.unibo.view.panels.*;

import javax.swing.*;
import java.awt.*;

public class PacmanClient {
    private final JFrame frame = new JFrame("Pacman");

    public PacmanClient() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLayout(new BorderLayout());
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