package it.unibo;

import it.unibo.controller.client.services.DummyServiceManager;
import it.unibo.controller.client.services.ServiceManager;
import it.unibo.controller.client.services.ServiceManagerImpl;
import it.unibo.view.navigation.AppState;
import it.unibo.view.navigation.ScreenRouter;
import it.unibo.view.screens.game.GameControllerWithDirectConnection;
import it.unibo.view.screens.loginRegister.LoginController;
import it.unibo.view.screens.loginRegister.RegisterController;
import it.unibo.view.screens.matchmaker.MatchmakerController;
import it.unibo.view.screens.menu.MainMenuController;
import it.unibo.view.screens.stats.StatsController;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

/**
 * Main entry point for the Pacman client application.
 */
public class FullGameClientMain {
    public enum Mode {
        /** Connects to live backend services via real networking implementations. */
        REMOTE,
        /** Uses local mock implementations for testing and offline debugging. */
        LOCAL
    }

    private final JFrame frame = new JFrame("Pacman");

    public FullGameClientMain(Mode mode) {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(new Dimension(1280, 720));
        frame.setLocationRelativeTo(null);
        ScreenRouter router = new ScreenRouter(frame);
        ServiceManager serviceManager = (mode == Mode.LOCAL) ? new DummyServiceManager() : new ServiceManagerImpl();
        router.register(AppState.LOGIN, new LoginController(router, serviceManager));
        router.register(AppState.REGISTER, new RegisterController(router, serviceManager));
        router.register(AppState.MAIN_MENU, new MainMenuController(router, serviceManager));
        router.register(AppState.MATCHMAKING, new MatchmakerController(router, serviceManager));
        router.register(AppState.IN_GAME, new GameControllerWithDirectConnection(router, serviceManager));
        router.register(AppState.STATS, new StatsController(router, serviceManager));
        router.goTo(AppState.LOGIN);
    }

    public void start() {
        frame.setVisible(true);
    }

    /**
     * Pass {@code --local} to run with dummy backend services.
     */
    static void main(String[] args) {
        Mode mode = Arrays.stream(args).anyMatch(arg -> arg.equalsIgnoreCase("--local")) ? Mode.LOCAL : Mode.REMOTE;
        SwingUtilities.invokeLater(() -> {
            FullGameClientMain app = new FullGameClientMain(mode);
            app.start();
        });
    }
}