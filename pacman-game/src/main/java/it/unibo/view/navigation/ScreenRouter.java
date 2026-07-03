package it.unibo.view.navigation;

import it.unibo.view.screens.ScreenController;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ScreenRouter implements AppNavigator {
    private final JFrame frame;
    private final Map<AppState, ScreenController> registry = new HashMap<>();
    private ScreenController activeController;

    public ScreenRouter(JFrame frame) {
        this.frame = frame;
    }

    public void register(AppState state, ScreenController controller) {
        registry.put(state, controller);
    }

    @Override
    public void goTo(AppState state) {
        if (!registry.containsKey(state)) {
            throw new IllegalStateException("Missing controller for: " + state);
        }
        SwingUtilities.invokeLater(() -> {
            // Dispose previous screen
            if (activeController != null) {
                activeController.onExit();
                frame.remove(activeController.getPanel());
            }
            // Setup new one
            activeController = registry.get(state);
            activeController.onEnter();
            frame.add(activeController.getPanel(), BorderLayout.CENTER);
            frame.revalidate();
            frame.repaint();
            activeController.getPanel().requestFocusInWindow();
        });
    }
}