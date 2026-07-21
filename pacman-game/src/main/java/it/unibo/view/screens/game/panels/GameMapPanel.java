package it.unibo.view.screens.game.panels;

import it.unibo.model.common.GameConstants;
import it.unibo.model.common.Vector2D;
import it.unibo.model.map.TileType;
import it.unibo.view.viewmodel.GameContextViewModel;
import it.unibo.view.viewmodel.PacmanViewModel;

import javax.swing.*;
import java.awt.*;

public class GameMapPanel extends JPanel {
    private final static int TOP_PADDING = 20;
    private final static int BOTTOM_PADDING = 20;

    private GameContextViewModel context;
    private int mapPixelWidth;   // Total pixel width of the rendered map
    private int mapPixelHeight;  // Total pixel height of the rendered map
    private int tilePixelSize;   // Pixel size of a single tile (tiles are square)

    private String localPlayerId;

    public GameMapPanel() {
        this.setOpaque(false);
        this.setVisible(true);
    }

    public void setGameContext(GameContextViewModel context) {
        this.context = context;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (this.context == null) return;
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int columns = this.context.map().col();
        int rows = this.context.map().row();
        int availableWidth = this.getWidth();
        int availableHeight = this.getHeight() - TOP_PADDING - BOTTOM_PADDING;
        int maxTileWidth = availableWidth / columns;
        int maxTileHeight = availableHeight / rows;
        this.tilePixelSize = Math.min(maxTileWidth, maxTileHeight);
        this.mapPixelWidth = this.tilePixelSize * columns;
        this.mapPixelHeight = this.tilePixelSize * rows;
        int startX = startingX();
        int startY = startingY();
        this.context.map()
                .tiles()
                .forEach(tile -> {
                    if (tile.type() == TileType.WALL) {
                        g2d.setColor(Color.BLACK);
                    } else {
                        g2d.setColor(Color.WHITE);
                    }
                    int tileX = startX + (tile.matrixPosition().column() * this.tilePixelSize);
                    int tileY = startY + (tile.matrixPosition().row() * this.tilePixelSize);
                    g2d.fillRect(tileX, tileY, tilePixelSize, tilePixelSize);
                });

        this.context.dots().forEach(dot -> {
            if (dot.isAlive()) {
                boolean isSpecial = dot.isSpecial();
                int size = getProportionalSize(GameConstants.GameEntityFeatures.DOT.getRadius()) * 2;
                Color dotColor = isSpecial ? Color.ORANGE : Color.GREEN;
                drawGameEntity(g2d, dot.position(), size, dotColor, startX, startY);
            }
        });

        this.context.ghosts().forEach(ghost -> {
            int size = getProportionalSize(GameConstants.GameEntityFeatures.GHOST.getRadius()) * 2;
            drawGameEntity(g2d, ghost.position(), size, Color.RED, startX, startY);
        });

        this.context.pacmans().stream().filter(PacmanViewModel::isAlive).forEach(pacman -> {
            // TODO: modify the view of pacman when is invincible (different tone of the color or grey)
            // maybe  wait until the pacman has the color attribute in its class.
            int size = (int) (pacman.canEatGhosts()
                    ? getProportionalSize(GameConstants.GameEntityFeatures.PACMAN.getRadius()) * 2.5
                    : getProportionalSize(GameConstants.GameEntityFeatures.PACMAN.getRadius()) * 2);
            Color color = this.localPlayerId.equals(pacman.id()) ? Color.BLUE : Color.YELLOW;
            drawGameEntity(g2d, pacman.position(), size, color, startX, startY);
        });
    }

    private void drawGameEntity(Graphics2D g2d, Vector2D position, int size, Color color, int startX, int startY) {
        int screenX = getProportionalSize(position.x()) + startX;
        int screenY = getProportionalSize(position.y()) + startY;
        int entityX = screenX - (size / 2);
        int entityY = screenY - (size / 2);
        g2d.setColor(Color.BLACK);
        g2d.drawOval(entityX, entityY, size, size);
        g2d.setColor(color);
        g2d.fillOval(entityX, entityY, size, size);
    }

    /**
     * Adapts the size of the entity from the model to the view.
     */
    private int getProportionalSize(int modelSize) {
        return modelSize * this.tilePixelSize / GameConstants.TILE_SIZE;
    }

    /**
     * Defines the x starting point to draw such that the map is centered.
     */
    private int startingX() {
        int availableWidth = this.getWidth();
        return (availableWidth - this.mapPixelWidth) / 2;
    }

    /**
     * Defines the y starting point to draw such that the map is centered.
     */
    private int startingY() {
        int availableHeight = this.getHeight() - TOP_PADDING - BOTTOM_PADDING;
        return TOP_PADDING + (availableHeight - this.mapPixelHeight) / 2;
    }

    public void setLocalPlayerId(String localPlayerId) {
        this.localPlayerId = localPlayerId;
    }
}