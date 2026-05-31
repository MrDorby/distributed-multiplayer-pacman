package it.unibo.view.screens.game;

import it.unibo.model.common.GameConstants;
import it.unibo.model.game.GameContext;
import it.unibo.model.map.TileType;

import javax.swing.*;
import java.awt.*;

public class GameMapPanel extends JPanel {

    private GameContext gameContext;
    private JPanel mapContainer;
    private int mapSize;

    public GameMapPanel(GameContext gameContext, JPanel mapContainer) {
        this.gameContext = gameContext;
        this.mapContainer = mapContainer;
        this.mapSize = getMapContainerMinimunDimension();
        this.setPreferredSize(new Dimension(mapSize, mapSize));
        //this.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 255), 5));
        this.setBorder(BorderFactory.createLineBorder(Color.BLACK, 5));
    }

    public void setGameContext(GameContext gameContext) {
        this.gameContext = gameContext;
    }

    //TODO: Make it more efficient (create a method to reduce the duplication)
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        mapSize = getMapContainerMinimunDimension();
        int tileSize = getTileSize(this.gameContext.getMap().getTiles().size());
        this.gameContext.getMap()
                .getTiles()
                .forEach(tile -> {
                    // TODO: Set different colors for every type of Tile
                    if (tile.getTileType() == TileType.WALL) {
                        g.setColor(Color.BLACK);
                    } else {
                        g.setColor(Color.WHITE);
                    }
                    //g.drawRect(
                    //        tile.getCenterPosition().y(),
                    //        tile.getCenterPosition().x(),
                    //        GameConstants.TILE_SIZE - 1,
                    //        GameConstants.TILE_SIZE - 1
                    //);
                    g.fillRect(
                            tile.getCenterPosition().y(),
                            tile.getCenterPosition().x(),
                            tileSize,
                            tileSize);
                });
        this.gameContext.getDots()
                .forEach(dot -> {
                    g.drawOval(
                            dot.getPosition().y(),
                            dot.getPosition().x(),
                            GameConstants.GameEntityFeatures.DOT.getRadius(),
                            GameConstants.GameEntityFeatures.DOT.getRadius()
                    );
                    g.setColor(Color.YELLOW);
                    g.fillOval(
                            dot.getPosition().y(),
                            dot.getPosition().x(),
                            GameConstants.GameEntityFeatures.DOT.getRadius() - 1,
                            GameConstants.GameEntityFeatures.DOT.getRadius() - 1
                    );
                });

        this.gameContext.getGhosts()
                .forEach(ghost -> {
                    g.setColor(Color.BLACK);
                    g.drawOval(
                            centreCircles(ghost.getPosition().y(), GameConstants.GameEntityFeatures.GHOST.getRadius()),
                            centreCircles(ghost.getPosition().x(), GameConstants.GameEntityFeatures.GHOST.getRadius()),
                            GameConstants.GameEntityFeatures.GHOST.getRadius(),
                            GameConstants.GameEntityFeatures.GHOST.getRadius()
                    );
                    g.setColor(Color.RED);
                    g.fillOval(
                            centreCircles(ghost.getPosition().y(), GameConstants.GameEntityFeatures.GHOST.getRadius()),
                            centreCircles(ghost.getPosition().x(), GameConstants.GameEntityFeatures.GHOST.getRadius()),
                            GameConstants.GameEntityFeatures.GHOST.getRadius() - 1,
                            GameConstants.GameEntityFeatures.GHOST.getRadius() - 1
                    );
                });

        this.gameContext.getPacmans()
                .forEach(pacman -> {
                    g.setColor(Color.BLACK);
                    //((Graphics2D) g).setStroke(new BasicStroke(2));
                    g.drawOval(
                            centreCircles(pacman.getPosition().y(), GameConstants.GameEntityFeatures.PACMAN.getRadius()),
                            centreCircles(pacman.getPosition().x(), GameConstants.GameEntityFeatures.PACMAN.getRadius()),
                            GameConstants.GameEntityFeatures.PACMAN.getRadius(),
                            GameConstants.GameEntityFeatures.PACMAN.getRadius()
                    );
                    //((Graphics2D) g).setStroke(new BasicStroke(1));
                    g.setColor(Color.YELLOW);
                    g.fillOval(
                            centreCircles(pacman.getPosition().y(), GameConstants.GameEntityFeatures.PACMAN.getRadius()),
                            centreCircles(pacman.getPosition().x(), GameConstants.GameEntityFeatures.PACMAN.getRadius()),
                            GameConstants.GameEntityFeatures.PACMAN.getRadius() - 1,
                            GameConstants.GameEntityFeatures.PACMAN.getRadius() - 1
                    );
                });
    }

    /* 
     * Determines the centre of the circle.
     */
    private int centreCircles(int upperCoordinate, int size) {
        int diff = (GameConstants.TILE_SIZE / 2) - (size / 2);
        return upperCoordinate + diff;
    }

    /*
     * Returns the minimun dimension of the map container.
     */
    private int getMapContainerMinimunDimension() {
        return Math.min(this.mapContainer.getHeight(), this.mapContainer.getWidth());
    }

    private int getTileSize(int numberTiles) {
        int square = (int) Math.sqrt(numberTiles);
        return this.mapSize / square;
    }

    private int getSizeConvertedToView(float modelSize, int tileSize) {
        return (int) (tileSize * modelSize);
    }
}
