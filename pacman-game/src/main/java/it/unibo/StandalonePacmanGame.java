package it.unibo;

import it.unibo.controller.input.InputHandler;
import it.unibo.controller.input.PlayerInputHandler;
import it.unibo.controller.engine.GameEngine;
import it.unibo.controller.engine.LocalGameEngine;
import it.unibo.model.entities.GameEntityFactoryImpl;
import it.unibo.model.game.Game;
import it.unibo.model.game.GameContext;
import it.unibo.model.game.GameContextFactory;
import it.unibo.model.game.GameImpl;
import it.unibo.model.map.FourPlayersGameMapFactory;
import it.unibo.view.GameViewImpl;

import javax.swing.*;

public class StandalonePacmanGame {
    static void main() {
        SwingUtilities.invokeLater(() -> {
            String playerName = "LocalPlayer";
            GameContext context = GameContextFactory.createFromMap(new FourPlayersGameMapFactory(new GameEntityFactoryImpl()).fromJSON("maps/map1.json"));
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
