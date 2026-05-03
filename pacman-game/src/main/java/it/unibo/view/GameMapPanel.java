package it.unibo.view;

import it.unibo.model.common.GameConstants;
import it.unibo.model.game.GameContext;
import it.unibo.model.map.TileType;

import javax.swing.*;
import java.awt.*;

public class GameMapPanel extends JPanel {

    private GameContext gameContext;

    public GameMapPanel(GameContext gameContext) {
        this.gameContext = gameContext;
    }

    public void setGameContext(GameContext gameContext) {
        this.gameContext = gameContext;
    }

    //TODO: Make it more efficient (create a method to reduce the duplication)
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        this.gameContext.getMap()
                .getTiles()
                .forEach(tile -> {
                    if (tile.getTileType() == TileType.SIMPLE) {
                        g.setColor(Color.WHITE);
                    } else {
                        g.setColor(Color.BLACK);
                    }
                    g.drawRect(
                            panelCoordinate(tile.getCenterPosition().x()),
                            panelCoordinate(tile.getCenterPosition().y()),
                            GameConstants.TILE_SIZE - 1,
                            GameConstants.TILE_SIZE - 1
                    );
                    g.fillRect(
                            panelCoordinate(tile.getCenterPosition().x()),
                            panelCoordinate(tile.getCenterPosition().y()),
                            GameConstants.TILE_SIZE,
                            GameConstants.TILE_SIZE - 1);
                });
        this.gameContext.getDots()
                .forEach(dot -> {
                    g.drawOval(
                            panelCoordinate(dot.getPosition().x()),
                            panelCoordinate(dot.getPosition().y()),
                            GameConstants.GameEntityFeatures.DOT.getRadius() - 1,
                            GameConstants.GameEntityFeatures.DOT.getRadius() - 1
                    );
                    g.setColor(Color.YELLOW);
                    g.fillOval(
                            panelCoordinate(dot.getPosition().x()),
                            panelCoordinate(dot.getPosition().y()),
                            GameConstants.GameEntityFeatures.DOT.getRadius(),
                            GameConstants.GameEntityFeatures.DOT.getRadius()
                    );
                });

        this.gameContext.getGhosts()
                .forEach(ghost -> {
                    g.setColor(Color.BLACK);
                    g.drawOval(
                            centreCircles(ghost.getPosition().x(), GameConstants.GameEntityFeatures.GHOST.getRadius()),
                            centreCircles(ghost.getPosition().y(), GameConstants.GameEntityFeatures.GHOST.getRadius()),
                            GameConstants.GameEntityFeatures.GHOST.getRadius() - 1,
                            GameConstants.GameEntityFeatures.GHOST.getRadius() - 1
                    );
                    g.setColor(Color.RED);
                    g.fillOval(
                            centreCircles(ghost.getPosition().x(), GameConstants.GameEntityFeatures.GHOST.getRadius()),
                            centreCircles(ghost.getPosition().y(), GameConstants.GameEntityFeatures.GHOST.getRadius()),
                            GameConstants.GameEntityFeatures.GHOST.getRadius() - 1,
                            GameConstants.GameEntityFeatures.GHOST.getRadius() - 1
                    );
                });

        this.gameContext.getPacmans()
                .forEach(pacman -> {
                    g.setColor(Color.BLACK);
                    g.drawOval(
                            centreCircles(pacman.getPosition().x(), GameConstants.GameEntityFeatures.PACMAN.getRadius()),
                            centreCircles(pacman.getPosition().y(), GameConstants.GameEntityFeatures.PACMAN.getRadius()),
                            GameConstants.GameEntityFeatures.PACMAN.getRadius() - 1,
                            GameConstants.GameEntityFeatures.PACMAN.getRadius() - 1
                    );
                    g.setColor(Color.YELLOW);
                    g.fillOval(
                            centreCircles(pacman.getPosition().x(), GameConstants.GameEntityFeatures.PACMAN.getRadius()),
                            centreCircles(pacman.getPosition().y(), GameConstants.GameEntityFeatures.PACMAN.getRadius()),
                            GameConstants.GameEntityFeatures.PACMAN.getRadius() - 1,
                            GameConstants.GameEntityFeatures.PACMAN.getRadius() - 1
                    );
                });
    }

    // TODO: To remove
    private int panelCoordinate(int centreCoordinate) {
        return centreCoordinate;// - (GameConstants.TILE_SIZE / 2);
    }

    private int centreCircles(int upperCoordinate, int size) {
        int diff = (GameConstants.TILE_SIZE / 2) - (size / 2);
        return upperCoordinate + diff;
    }
}
