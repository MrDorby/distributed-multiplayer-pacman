package it.unibo;

import it.unibo.controller.engine.GameEngine;
import it.unibo.controller.engine.GameEngineImpl;
import it.unibo.model.collisions.CollisionManagerImpl;
import it.unibo.model.game.Game;
import it.unibo.model.game.GameContext;
import it.unibo.model.game.GameContextFactory;
import it.unibo.model.game.GameImpl;
import it.unibo.view.GameViewImpl;

import javax.swing.*;

public class StandalonePacmanGame {
    static void main() {
        SwingUtilities.invokeLater(() -> {
            GameContext context = GameContextFactory.getTestContext();
            Game game = new GameImpl(context, new CollisionManagerImpl());
            GameEngine engine = new GameEngineImpl(game);
            GameViewImpl view = new GameViewImpl(engine, game.getContext());
            engine.setView(view);
            JFrame testFrame = new JFrame("Pacman Standalone Test");
            testFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            testFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            JPanel gamePanel = view.getGamePanel();
            testFrame.add(gamePanel);
            testFrame.setVisible(true);
            gamePanel.requestFocusInWindow();
            new Thread(engine::start).start();
        });
    }
}
