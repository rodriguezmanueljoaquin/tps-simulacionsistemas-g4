import java.util.*;

public class CollisionHelper {

    public static Pair<Double, Map<String, List<Pair<Particle, Particle>>>> getCollisionTimeAndParticles(List<Particle> particles, double gap, Particle topGapParticle, Particle bottomGapParticle) {
        double timeToNextCollision = Double.MAX_VALUE;
        Map<String, List<Pair<Particle, Particle>>> collisionedParticles = new HashMap<>();
        collisionedParticles.put(Constants.WALL_VERTICAL_COLLISION_KEY, new ArrayList<>());
        collisionedParticles.put(Constants.WALL_HORIZONTAL_COLLISION_KEY, new ArrayList<>());
        collisionedParticles.put(Constants.PARTICLES_COLLISION_KEY, new ArrayList<>());
        collisionedParticles.put(Constants.TOP_GAP_COLLISION_KEY, new ArrayList<>());
        collisionedParticles.put(Constants.BOTTOM_GAP_COLLISION_KEY, new ArrayList<>());

        for (int i = 0; i < particles.size(); i++) {
            for (int j = i + 1; j < particles.size(); j++) {
                double timeToParticleCollision = getTimeToParticleCollision(particles.get(i), particles.get(j));
                if (timeToParticleCollision <= timeToNextCollision) {
                    if (timeToParticleCollision != timeToNextCollision) {
                        // reset
                        timeToNextCollision = timeToParticleCollision;
                        collisionedParticles.replaceAll((key, pairs) -> new ArrayList<>());
                    }
                    collisionedParticles.get(Constants.PARTICLES_COLLISION_KEY).add(new Pair<>(particles.get(i), particles.get(j)));
                }
            }

            Pair<Double, String> wallTimeAndType = timeToWallCollisionAndType(particles.get(i), gap, topGapParticle, bottomGapParticle);
            if (wallTimeAndType.getLeft() <= timeToNextCollision) {
                if (wallTimeAndType.getLeft() != timeToNextCollision) {
                    // reset
                    timeToNextCollision = wallTimeAndType.getLeft();
                    collisionedParticles.replaceAll((key, pairs) -> new ArrayList<>());
                }
                // colisionaron en el mismo tiempo
                collisionedParticles.get(wallTimeAndType.getRight()).add(new Pair<>(particles.get(i), null));
            }
        }

        return new Pair<>(timeToNextCollision, collisionedParticles);
    }

    private static Pair<Double, String> timeToWallCollisionAndType(Particle p, double gap, Particle topGapParticle, Particle bottomGapParticle) {
        double timeToVertical;
        double timeToHorizontal;
        List<Pair<Double, String>> timesAndTypesToCollision = new ArrayList<>();

        if (p.getxVelocity() > 0) {
            double maxX = p.getX() + p.getRadius();
            if (maxX > Constants.SIMULATION_WIDTH / 2) {
                timeToVertical = (Constants.SIMULATION_WIDTH - maxX) / p.getxVelocity();
            } else {
                double timeToMiddle = (Constants.SIMULATION_WIDTH / 2 - maxX) / p.getxVelocity();
                if (Math.abs(p.getY() + p.getyVelocity() * timeToMiddle
                        - Constants.SIMULATION_HEIGHT / 2) > gap / 2)
                    // Hits middle wall
                    timeToVertical = timeToMiddle;
                else
                    // Hits right wall
                    timeToVertical = (Constants.SIMULATION_WIDTH - maxX) / p.getxVelocity();
            }
        } else {
            double minX = p.getX() - p.getRadius();
            if (minX < Constants.SIMULATION_WIDTH / 2) {
                timeToVertical = minX / Math.abs(p.getxVelocity());
            } else {
                double timeToMiddle = (minX - Constants.SIMULATION_WIDTH / 2) / Math.abs(p.getxVelocity());
                if (Math.abs(p.getY() + p.getyVelocity() * timeToMiddle
                        - Constants.SIMULATION_HEIGHT / 2) > gap / 2)
                    // Hits middle wall
                    timeToVertical = timeToMiddle;
                else
                    // Hits Left wall
                    timeToVertical = minX / Math.abs(p.getxVelocity());
            }
        }

        timeToHorizontal = (
                (p.getyVelocity() > 0 ?
                        Constants.SIMULATION_HEIGHT - p.getRadius() : p.getRadius())
                        - p.getY()) / p.getyVelocity();


        timesAndTypesToCollision.add(getTimeAndTypeToGapCollision(p,
                topGapParticle, bottomGapParticle));
        timesAndTypesToCollision.add(new Pair<>(timeToVertical, Constants.WALL_VERTICAL_COLLISION_KEY));
        timesAndTypesToCollision.add(new Pair<>(timeToHorizontal, Constants.WALL_HORIZONTAL_COLLISION_KEY));

        return timesAndTypesToCollision.stream().min(Comparator.comparing(Pair::getLeft)).get();
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

    private static Pair<Double, String> getTimeAndTypeToGapCollision(Particle p, Particle topGapParticle, Particle bottomGapParticle) {
        double timeToCollision = getTimeToParticleCollision(p, topGapParticle);
        String gapCollision = Constants.TOP_GAP_COLLISION_KEY;
        double newTimeToCollision = getTimeToParticleCollision(p, bottomGapParticle);
        if (newTimeToCollision > 0 && newTimeToCollision < timeToCollision) {
            timeToCollision = newTimeToCollision;
            gapCollision = Constants.BOTTOM_GAP_COLLISION_KEY;
        }
        return new Pair<>(timeToCollision, gapCollision);
    }
}
