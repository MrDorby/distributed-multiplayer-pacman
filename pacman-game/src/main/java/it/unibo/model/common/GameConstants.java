package it.unibo.model.common;

public class GameConstants {
    private GameConstants() {}

    public static final int TILE_SIZE = 48;
    public static final int GAME_DURATION_IN_SECONDS = 180;
    public static final int GAME_DURATION_IN_MILLIS = GAME_DURATION_IN_SECONDS * 1000;

    public enum GameEntityFeatures {

        PACMAN(16, 3000, 2),

        GHOST(16, 3000, 2),

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
