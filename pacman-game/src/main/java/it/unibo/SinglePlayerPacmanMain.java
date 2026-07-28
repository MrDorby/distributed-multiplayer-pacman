package it.unibo;

import it.unibo.controller.shared.input.InputHandler;
import it.unibo.controller.shared.input.InputHandlerImpl;
import it.unibo.controller.shared.engine.GameEngine;
import it.unibo.controller.shared.engine.LocalGameEngine;
import it.unibo.model.entities.GameEntityFactoryImpl;
import it.unibo.model.game.Game;
import it.unibo.model.game.GameContext;
import it.unibo.model.game.GameContextFactory;
import it.unibo.model.game.GameImpl;
import it.unibo.view.GameView;
import it.unibo.view.SwingGameView;

import javax.swing.*;

public class SinglePlayerPacmanMain {
    static void main() {
        SwingUtilities.invokeLater(() -> {
            String playerName = "LocalPlayer";
            // Can use SpeculativeEntityFactoryImpl for no collision effects.
            GameContext context = GameContextFactory.createFromMap("maps/map3.json", new GameEntityFactoryImpl());
            Game game = new GameImpl(context);
            GameEngine engine = new LocalGameEngine(game, playerName);
            InputHandler inputHandler = new InputHandlerImpl(playerName);
            inputHandler.setEngine(engine);
            GameView view = new SwingGameView();
            engine.setView(view);
            view.setInputHandler(inputHandler);
            view.getGamePanel().setLocalPlayerId(playerName);
            JFrame frame = new JFrame("Pacman Standalone Test");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1200, 800);
            JPanel gamePanel = view.getGamePanel();
            frame.add(gamePanel);
            view.getGamePanel().onClose(() -> System.exit(0));
            frame.setVisible(true);
            gamePanel.requestFocusInWindow();
            engine.start();
        });
    }
}
