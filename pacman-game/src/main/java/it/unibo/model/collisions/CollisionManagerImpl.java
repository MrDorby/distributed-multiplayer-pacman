package it.unibo.model.collisions;

import it.unibo.model.common.Direction;
import it.unibo.model.entities.Dot;
import it.unibo.model.entities.GameEntity;
import it.unibo.model.entities.Ghost;
import it.unibo.model.entities.Pacman;
import it.unibo.model.game.GameContext;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CollisionManagerImpl implements CollisionManager {

    @Override
    public Map<GameEntity, Set<Collision>> computeCollisions(GameContext context) {
        Map<GameEntity, Set<Collision>> collisions = new HashMap<>();
        Set<Pacman> pacmans = context.getPacmans();
        Set<Ghost> ghosts = context.getGhosts();
        Set<Dot> dots = context.getDots();
        // We only need to check pacmans against ghosts and dots.
        for (Pacman pacman : pacmans) {
            for (Ghost ghost : ghosts) {
                registerCollision(pacman, ghost, collisions);
            }
            for (Dot dot : dots) {
                registerCollision(pacman, dot, collisions);
            }
        }
        return collisions;
    }

    private void registerCollision(GameEntity a, GameEntity b, Map<GameEntity, Set<Collision>> map) {
        if (a.getBoundingBox().collides(b.getBoundingBox())) {
            Direction aToB = calculateDirection(a, b);
            map.computeIfAbsent(a, _ -> new HashSet<>()).add(new CollisionImpl(b, aToB));
            map.computeIfAbsent(b, _ -> new HashSet<>()).add(new CollisionImpl(a, aToB.getOpposite()));
        }
    }

    private Direction calculateDirection(GameEntity self, GameEntity other) {
        double dx = other.getPosition().x() - self.getPosition().x();
        double dy = other.getPosition().y() - self.getPosition().y();
        if (Math.abs(dx) > Math.abs(dy)) {
            return dx > 0 ? Direction.RIGHT : Direction.LEFT;
        } else {
            return dy > 0 ? Direction.UP : Direction.DOWN;
        }
    }
}
