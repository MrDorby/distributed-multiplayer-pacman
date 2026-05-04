package it.unibo.model.common;

public class GameConstants {
    // Prevent instantiation
    private GameConstants() {}

    public static final int TILE_SIZE = 44;
    public static final int GAME_DURATION_SECONDS = 180;

    public enum GameEntityFeatures {

        PACMAN(26, 3000, 6),

        GHOST(26, 3000, 8),

        DOT(18, 3000, 0);

        private final int radius;
        private final int timeToRespawn;
        private final int velocity;

        GameEntityFeatures(int radius, int timeToRespawn, int velocity) {
            this.radius = radius;
            this.timeToRespawn = timeToRespawn;
            this.velocity = velocity;
        }


        public int getRadius() {
            return radius;
        }

        public int getTimeToRespawn() {
            return timeToRespawn;
        }

        public int getVelocity() {
            return velocity;
        }
    }
}
