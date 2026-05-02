package it.unibo.model.common;

public class GameConstants {
    // Prevent instantiation
    private GameConstants() {}

    public static final int TILE_SIZE = 38;
    public static final int GAME_DURATION_SECONDS = 180;

    public enum GameEntityFeatures {

        PACMAN(26, 26, 3000, 6),

        GHOST(26, 26, 3000, 8),

        DOT(18, 18, 3000, 0);

        private final int width;
        private final int height;
        private final int timeToRespawn;
        private final int velocity;

        GameEntityFeatures(int width, int height, int timeToRespawn, int velocity) {
            this.width = width;
            this.height = height;
            this.timeToRespawn = timeToRespawn;
            this.velocity = velocity;
        }


        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        public int getTimeToRespawn() {
            return timeToRespawn;
        }

        public int getVelocity() {
            return velocity;
        }
    }
}
