package it.unibo.view.panels;

import javax.swing.*;

/**
 * Manages the lifecycle and UI component of a specific application screen.
 */
public interface ScreenController {
    /**
     * Returns the Swing component to be displayed in the main frame.
     */
    JPanel getPanel();

    /**
     * Called when this screen is about to be displayed.
     * Use this to perform setup operations.
     */
    void onEnter();

    /**
     * Called when the application is navigating away from this screen.
     * Use this perform disposal operations.
     */
    void onExit();
}