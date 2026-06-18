package it.unibo.view.screens.game;

import it.unibo.model.common.GameConstants;
import it.unibo.model.game.GameContext;
import it.unibo.model.map.TileType;

import javax.swing.*;
import java.awt.*;

public class GameMapPanel extends JPanel {

    private final static int SPACE_FROM_TOP = 20;
    private GameContext gameContext;
    private JPanel mapContainer;
    private int mapSize;
    private int tileSize;
    private int squareTiles;

    public GameMapPanel(GameContext gameContext, JPanel mapContainer) {
        this.gameContext = gameContext;
        this.mapContainer = mapContainer;
        this.mapSize = getMapContainerMinimunDimension();
        this.setOpaque(false);
        this.setVisible(true);
    }

    public void setGameContext(GameContext gameContext) {
        this.gameContext = gameContext;
    }

    //TODO: Make it more efficient (create a method to reduce the duplication)
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        this.mapSize = getMapContainerMinimunDimension();
        this.squareTiles = (int) Math.sqrt(this.gameContext.getMap().getTiles().size());
        this.tileSize = getProportionSize(GameConstants.TILE_SIZE);
        int startX = startingX();
        this.gameContext.getMap()
                .getTiles()
                .forEach(tile -> {
                    // g.setColor(Color.BLACK);
                    // Graphics2D g2 = (Graphics2D) g;
                    // Stroke oldStroke = g2.getStroke();
                    // g2.setStroke(new BasicStroke(0.2f));
                    // g.drawRect(
                    //     getProportionSize(tile.getCenterPosition().y() + startX),
                    //     getProportionSize(tile.getCenterPosition().x() + SPACE_FROM_TOP),
                    //     tileSize,
                    //     tileSize
                    // );
                    // g2.setStroke(oldStroke);
                    // TODO: Set different colors for every type of Tile
                    if (tile.getTileType() == TileType.WALL) {
                        g.setColor(Color.BLACK);
                    } else {
                        g.setColor(Color.WHITE);
                    }
                    g.fillRect(
                        getProportionSize(tile.getCenterPosition().y() + startX),
                        getProportionSize(tile.getCenterPosition().x() + SPACE_FROM_TOP),
                        tileSize,
                        tileSize
                    );
                });
        int dotSize = getProportionSize(GameConstants.GameEntityFeatures.DOT.getRadius());
        this.gameContext.getDots()
                .forEach(dot -> {
                    g.drawOval(
                        dot.getPosition().y() + startX,
                        dot.getPosition().x() + SPACE_FROM_TOP,
                        dotSize,
                        dotSize
                    );
                    g.setColor(Color.GREEN);
                    g.fillOval(
                        dot.getPosition().y() + startX,
                        dot.getPosition().x() + SPACE_FROM_TOP,
                        dotSize - 1,
                        dotSize - 1
                    );
                });

        int ghostSize = getProportionSize(GameConstants.GameEntityFeatures.GHOST.getRadius());
        this.gameContext.getGhosts()
                .forEach(ghost -> {
                    g.setColor(Color.BLACK);
                    g.drawOval(
                        centreCircles(ghost.getPosition().y() + startX, ghostSize),
                        centreCircles(ghost.getPosition().x() + SPACE_FROM_TOP, ghostSize),
                        ghostSize,
                        ghostSize
                    );
                    g.setColor(Color.RED);
                    g.fillOval(
                        centreCircles(ghost.getPosition().y() + startX, ghostSize),
                        centreCircles(ghost.getPosition().x() + SPACE_FROM_TOP, ghostSize),
                        ghostSize - 1,
                        ghostSize - 1
                    );
                });

        int pacmanSize = getProportionSize(GameConstants.GameEntityFeatures.PACMAN.getRadius());
        this.gameContext.getPacmans()
                .forEach(pacman -> {
                    g.setColor(Color.BLACK);
                    //((Graphics2D) g).setStroke(new BasicStroke(2));
                    g.drawOval(
                            centreCircles(pacman.getPosition().y() + startX, pacmanSize),
                            centreCircles(pacman.getPosition().x() + SPACE_FROM_TOP, pacmanSize),
                            pacmanSize,
                            pacmanSize
                    );
                    //((Graphics2D) g).setStroke(new BasicStroke(1));
                    g.setColor(Color.YELLOW);
                    g.fillOval(
                            centreCircles(pacman.getPosition().y() + startX, pacmanSize),
                            centreCircles(pacman.getPosition().x() + SPACE_FROM_TOP, pacmanSize),
                            pacmanSize - 1,
                            pacmanSize - 1
                    );
                });
    }

    /* 
     * Determines the centre of the circle.
     */
    private int centreCircles(int upperCoordinate, int size) { //TODO: maybe do TOP==BOTTOM || LEFT==RIGHT
        int diff = (this.tileSize / 2) - (size / 2);
        return getProportionSize(upperCoordinate) + diff;
    }

    /*
     * Returns the minimun dimension of the map container.
     */
    private int getMapContainerMinimunDimension() {
        return Math.min(this.mapContainer.getHeight(), this.mapContainer.getWidth()) - SPACE_FROM_TOP;
    }

    /*
     * Adapts the size of the entity from the model to the view.
     */
    private int getProportionSize(int size) {
        //return square * GameConstants.TILE_SIZE / this.mapSize;
        return size * this.mapSize / (this.squareTiles * GameConstants.TILE_SIZE);
    }

    /*
     * Defines the starting point to draw such that the map is centered.
     */
    private int startingX() {
        return (this.mapContainer.getWidth() / 2) - (this.tileSize * this.squareTiles / 2);
    }
}
