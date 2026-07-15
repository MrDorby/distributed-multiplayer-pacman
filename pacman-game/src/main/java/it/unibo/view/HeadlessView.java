package it.unibo.view;

import it.unibo.controller.shared.engine.GameLifecycleEvent;
import it.unibo.controller.shared.input.InputHandler;
import it.unibo.model.game.GameContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

public class HeadlessView implements GameView {
    private static final Logger logger = LoggerFactory.getLogger(HeadlessView.class);

    @Override
    public void render(GameContext context) {
        logger.trace("Rendering call received.");
    }

    @Override
    public void setInputHandler(InputHandler inputHandler) {}

    @Override
    public void onLifecycleEvent(GameLifecycleEvent event) {}

    @Override
    public JPanel getGamePanel() {
        return null;
    }
}
