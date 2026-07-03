package it.unibo;

import it.unibo.controller.shared.input.InputHandler;
import it.unibo.controller.shared.input.PlayerInputHandler;
import it.unibo.controller.shared.engine.GameEngine;
import it.unibo.controller.shared.engine.LocalGameEngine;
import it.unibo.model.entities.GameEntityFactoryImpl;
import it.unibo.model.game.Game;
import it.unibo.model.game.GameContext;
import it.unibo.model.game.GameContextFactory;
import it.unibo.model.game.GameImpl;
import it.unibo.view.GameViewImpl;

import javax.swing.*;

public class SinglePlayerPacmanMain {
    static void main() {
        SwingUtilities.invokeLater(() -> {
            String playerName = "LocalPlayer";
            // Can use SpeculativeEntityFactoryImpl for no collision effects.
            GameContext context = GameContextFactory.createFromMap("maps/map1.json", new GameEntityFactoryImpl());
            Game game = new GameImpl(context);
            GameEngine engine = new LocalGameEngine(game, playerName);
            InputHandler inputHandler = new PlayerInputHandler(engine, playerName);
            GameViewImpl view = new GameViewImpl(inputHandler);
            engine.setView(view);
            JFrame testFrame = new JFrame("Pacman Standalone Test");
            testFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            testFrame.setSize(1200, 800);
            JPanel gamePanel = view.getGamePanel();
            testFrame.add(gamePanel);
            testFrame.setVisible(true);
            gamePanel.requestFocusInWindow();
            new Thread(engine::start).start();
        });
    }
}
