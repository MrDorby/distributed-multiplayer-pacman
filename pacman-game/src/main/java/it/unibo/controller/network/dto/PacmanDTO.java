package it.unibo.controller.network.dto;

import it.unibo.model.common.MatrixCoordinates;
import it.unibo.model.common.Vector2D;
import it.unibo.model.entities.Pacman;

public class PacmanDTO {
    public String id;
    public int score;
    public int lives;
    public boolean controlledByPlayer;
    public boolean canEatGhosts;
    public long specialDotEatenTime;
    public int currentTileRow;
    public int currentTileCol;
    public double x;
    public double y;
    public boolean isAlive;

    public PacmanDTO() {}

    public static PacmanDTO toDTO(Pacman pacman) {
        PacmanDTO dto = new PacmanDTO();
        dto.id = pacman.getId();
        dto.score = pacman.getScore();
        dto.lives = pacman.getLives();
        dto.controlledByPlayer = pacman.isPlayer();
        dto.canEatGhosts = pacman.canEatGhost();
        dto.isAlive = pacman.isAlive();
        dto.specialDotEatenTime = pacman.getSpecialDotEatenTime();
        Vector2D position = pacman.getPosition();
        dto.x = position.x();
        dto.y = position.y();
        MatrixCoordinates matrixCoordinates = pacman.getMovementManager().currentMatrixCoordinates();
        dto.currentTileRow = matrixCoordinates.row();
        dto.currentTileCol = matrixCoordinates.column();
        return dto;
    }
}
