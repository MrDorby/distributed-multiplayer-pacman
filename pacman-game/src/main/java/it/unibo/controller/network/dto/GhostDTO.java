package it.unibo.controller.network.dto;

import it.unibo.model.common.MatrixCoordinates;
import it.unibo.model.common.Vector2D;
import it.unibo.model.entities.Ghost;

public class GhostDTO {
    public String direction;

    public int currentTileRow;
    public int currentTileCol;
    public double x;
    public double y;

    public boolean isAlive;

    public GhostDTO() {}

    public static GhostDTO toDTO(Ghost ghost) {
        GhostDTO dto = new GhostDTO();
        dto.isAlive = ghost.isAlive();
        dto.direction = ghost.getDirection().name();
        Vector2D position = ghost.getPosition();
        dto.x = position.x();
        dto.y = position.y();
        MatrixCoordinates matrixCoordinates = ghost.getMovementManager().currentMatrixCoordinates();
        dto.currentTileRow = matrixCoordinates.row();
        dto.currentTileCol = matrixCoordinates.column();
        return dto;
    }
}