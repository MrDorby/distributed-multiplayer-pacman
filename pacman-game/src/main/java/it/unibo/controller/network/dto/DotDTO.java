package it.unibo.controller.network.dto;

import it.unibo.model.common.Vector2D;
import it.unibo.model.entities.Dot;

public class DotDTO {
    public boolean isSpecial;
    public long lastTimeEaten;
    public double x;
    public double y;
    public boolean isAlive;

    public DotDTO() {}

    public static DotDTO toDTO(Dot dot) {
        DotDTO dto = new DotDTO();
        dto.isSpecial = dot.isSpecial();
        dto.lastTimeEaten = dot.getLastTimeEaten();
        dto.isAlive = dot.isAlive();
        Vector2D position = dot.getPosition();
        dto.x = position.x();
        dto.y = position.y();
        return dto;
    }
}