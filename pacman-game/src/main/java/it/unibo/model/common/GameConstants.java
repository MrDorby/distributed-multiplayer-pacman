package it.unibo.model.common;

public class GameConstants {
    // Prevent instantiation
    private GameConstants() {}

    public static final int TILE_SIZE = 48;
    public static final int GAME_DURATION_SECONDS = 180;

    public enum GameEntityFeatures {

        PACMAN(16, 3000, 6),

        GHOST(16, 3000, 6),

        DOT(8, 3000, 0);

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
