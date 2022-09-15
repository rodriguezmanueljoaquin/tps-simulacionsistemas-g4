import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CollisionHelper {

    public static Pair<Double, Map<String, List<Pair<Particle, Particle>>>> getCollisionTimeAndParticles(List<Particle> particles, double gap) {
        double timeToNextCollision = Double.MAX_VALUE;
        Map<String, List<Pair<Particle, Particle>>> collisionedParticles = new HashMap<>();
        collisionedParticles.put(Constants.WALL_VERTICAL_COLLISION_KEY, new ArrayList<>());
        collisionedParticles.put(Constants.WALL_HORIZONTAL_COLLISION_KEY, new ArrayList<>());
        collisionedParticles.put(Constants.PARTICLES_COLLISION_KEY, new ArrayList<>());

        for (int i = 0; i < particles.size(); i++) {
            for (int j = i + 1; j < particles.size(); j++) {
                double timeToParticleCollision = getTimeToParticleCollision(particles.get(i), particles.get(j));
                if (timeToParticleCollision <= timeToNextCollision) {
                    if (timeToParticleCollision != timeToNextCollision) {
                        timeToNextCollision = timeToParticleCollision;
                        collisionedParticles.replaceAll((key, pairs) -> new ArrayList<>());
                    }
                    collisionedParticles.get(Constants.PARTICLES_COLLISION_KEY).add(new Pair<>(particles.get(i), particles.get(j)));
                }
            }

            Pair<Double, String> wallTimeAndType = timeToWallCollisionAndType(particles.get(i), gap);
            if (wallTimeAndType.getLeft() <= timeToNextCollision) {
                if (wallTimeAndType.getLeft() != timeToNextCollision) {
                    timeToNextCollision = wallTimeAndType.getLeft();
                    collisionedParticles.replaceAll((key, pairs) -> new ArrayList<>());
                }
                // colisionaron en el mismo tiempo
                collisionedParticles.get(wallTimeAndType.getRight()).add(new Pair<>(particles.get(i), null));
            }
        }

        return new Pair<>(timeToNextCollision, collisionedParticles);
    }

    private static Pair<Double, String> timeToWallCollisionAndType(Particle p, double gap) {
        double timeToVertical;
        double timeToHorizontal;

        if (p.getxVelocity() > 0) {
            double maxX = p.getX() + p.getRadius();
            if (maxX > Constants.SIMULATION_WIDTH / 2) {
                timeToVertical = (Constants.SIMULATION_WIDTH - maxX) / p.getxVelocity();
            } else {
                if (Math.abs(p.getY() + p.getyVelocity() * (Constants.SIMULATION_WIDTH / 2 - maxX) / p.getxVelocity()
                        - Constants.SIMULATION_HEIGHT / 2) < gap / 2)
                    // esta en la caja izquierda pero pasara a la derecha
                    timeToVertical = (Constants.SIMULATION_WIDTH - maxX) / p.getxVelocity();
                else
                    timeToVertical = (Constants.SIMULATION_WIDTH / 2 - maxX) / p.getxVelocity();
            }
        } else {
            double minX = p.getX() - p.getRadius();
            if (minX < Constants.SIMULATION_WIDTH / 2) {
                timeToVertical = Math.abs((minX) / p.getxVelocity());
            } else {
                // esta en la caja derecha pero pasara a la izquierda
                if (Math.abs(p.getY() + p.getyVelocity() * (minX - Constants.SIMULATION_WIDTH / 2) / p.getxVelocity()
                        - Constants.SIMULATION_HEIGHT / 2) < gap / 2)
                    timeToVertical = Math.abs((minX) / p.getxVelocity());
                else
                    timeToVertical = Math.abs(((minX) - Constants.SIMULATION_WIDTH / 2) / -p.getxVelocity());
            }
        }

        timeToHorizontal = (
                (p.getyVelocity() > 0 ?
                        Constants.SIMULATION_HEIGHT - p.getRadius() : p.getRadius())
                        - p.getY()) / p.getyVelocity();

        if (timeToVertical < timeToHorizontal)
            return new Pair<>(timeToVertical, Constants.WALL_VERTICAL_COLLISION_KEY);
        else return new Pair<>(timeToHorizontal, Constants.WALL_HORIZONTAL_COLLISION_KEY);
    }

    private static double getTimeToParticleCollision(Particle p1, Particle p2) {
        double deltaX = p2.getX() - p1.getX();
        double deltaY = p2.getY() - p1.getY();
        double deltaxV = p2.getxVelocity() - p1.getxVelocity();
        double deltayV = p2.getyVelocity() - p1.getyVelocity();

        double deltaRSquared = Math.pow(deltaX, 2) + Math.pow(deltaY, 2);
        double deltaVSquared = Math.pow(deltaxV, 2) + Math.pow(deltayV, 2);
        double deltaVDotDeltaR = (deltaxV) * (deltaX) + (deltayV) * (deltaY);
        double d = Math.pow(deltaVDotDeltaR, 2) - deltaVSquared * (deltaRSquared - Math.pow(p1.getRadius() + p2.getRadius(), 2));

        if (deltaVDotDeltaR >= 0 || d < 0)
            return Double.MAX_VALUE;
        else return (-1) * (deltaVDotDeltaR + Math.sqrt(d)) / (deltaVSquared);
    }
}
