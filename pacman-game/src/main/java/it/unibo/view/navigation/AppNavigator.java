package it.unibo.view.navigation;

import it.unibo.view.screens.ScreenController;

/**
 * This provides a decoupled way for individual {@link ScreenController}s to request
 * a screen change without needing a direct reference to the main application view.
 */
@FunctionalInterface
public interface AppNavigator {
    void goTo(AppState nextState);
}