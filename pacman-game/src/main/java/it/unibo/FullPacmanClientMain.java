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

public class FullPacmanClientMain {
    private final JFrame frame = new JFrame("Pacman");
    private final static int WIDTH_FRAME = Toolkit.getDefaultToolkit().getScreenSize().width;
    private final static int HEIGHT_FRAME = Toolkit.getDefaultToolkit().getScreenSize().height;

    public FullPacmanClientMain(boolean isDebug) {
        frame.setSize(WIDTH_FRAME, HEIGHT_FRAME);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ScreenRouter router = new ScreenRouter(frame);
        ServiceManager serviceManager = isDebug ? new DummyServiceManager() : new ServiceManagerImpl();
        router.register(AppState.LOGIN, new LoginController(router, serviceManager));
        router.register(AppState.REGISTER, new RegisterController(router, serviceManager));
        router.register(AppState.MAIN_MENU, new MainMenuController(router));
        router.register(AppState.MATCHMAKING, new MatchmakerController(router, serviceManager));
        router.register(AppState.IN_GAME, new GameControllerWithDirectConnection(router, serviceManager));
        router.register(AppState.STATS, new StatsController(router, serviceManager));
        router.goTo(AppState.LOGIN);
    }

    public void start() {
        frame.setVisible(true);
    }

    static void main(String[] args) {
        boolean isDebug = Arrays.stream(args).anyMatch(arg -> arg.equalsIgnoreCase("--debug"));
        SwingUtilities.invokeLater(() -> {
            FullPacmanClientMain app = new FullPacmanClientMain(isDebug);
            app.start();
        });
    }
}