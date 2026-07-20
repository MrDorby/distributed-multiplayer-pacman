package it.unibo.view;

import it.unibo.controller.shared.input.InputHandler;
import it.unibo.view.viewmodel.GameContextViewModel;

import javax.swing.*;

/**
 * The visual output for the game.
 */
public interface GameView {
    /**
     * Updates the screen with the latest game data.
     */
    void render(GameContextViewModel context);

    void setInputHandler(InputHandler inputHandler);

    /**
     * Return the JPanel used for rendering the game view
     */
    JPanel getGamePanel();
}