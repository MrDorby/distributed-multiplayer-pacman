package it.unibo.view;

import it.unibo.model.game.GameContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HeadlessView implements GameView {
    private static final Logger logger = LoggerFactory.getLogger(HeadlessView.class);

    @Override
    public void render(GameContext context) {
        logger.trace("Rendering call received.");
    }

    @Override
    public void show() {
        logger.info("Headless view initialized. No window will be displayed.");
    }

    @Override
    public void displayWinView(Runnable onExit) {
        logger.info("Headless winView initialized. No window will be displayed.");
    }

    @Override
    public void displayGameOverView() {
        logger.info("Headless gameOverView initialized. No window will be displayed.");
    }
}
