import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;

public class Population {
    private List<Particle> particles;
    private Random rand;
    private Integer particlesQty;
    private Double gap;
    private Double currentIterationTime;
    private Particle bottomGapParticle;
    private Particle topGapParticle;


    public Population(Integer particlesQty, Double gap, long seed) {
        this.particlesQty = particlesQty;
        this.particles = new ArrayList<>();
        this.rand = new Random(seed);
        this.gap = gap;
        this.currentIterationTime = 0.;
        this.bottomGapParticle = new Particle(Constants.SIMULATION_WIDTH / 2, Constants.SIMULATION_HEIGHT / 2 + gap / 2, 0, 0, Double.MIN_VALUE, Double.MAX_VALUE);
        this.topGapParticle = new Particle(Constants.SIMULATION_WIDTH / 2, Constants.SIMULATION_HEIGHT / 2 - gap / 2, 0, 0, Double.MIN_VALUE, Double.MAX_VALUE);

        for (int i = 0; i < this.particlesQty; i++) {
            boolean validPos = false;
            Particle particle = null;
            while (!validPos) {
                validPos = true;
                particle = new Particle(
                        (rand.nextDouble() * (Constants.SIMULATION_WIDTH / 2 - Constants.PARTICLE_RADIUS * 2)) + Constants.PARTICLE_RADIUS,
                        (rand.nextDouble() * (Constants.SIMULATION_HEIGHT - Constants.PARTICLE_RADIUS * 2)) + Constants.PARTICLE_RADIUS,
                        rand.nextDouble() * 2 * Math.PI,
                        Constants.PARTICLE_VELOCITY,
                        Constants.PARTICLE_RADIUS,
                        Constants.PARTICLE_MASS);
                for (Particle other : particles) {
                    Double d = particle.calculateDistanceTo(other);
                    if (d <= 0) { //TODO VER SI EN 0 PUEDE ARRANCAR
                        validPos = false;
                    }
                }
            }
            particles.add(particle);
        }
    }

    public void nextCollision() {
        Pair<Double, Map<String, List<Pair<Particle, Particle>>>> answer =
                CollisionHelper.getCollisionTimeAndParticles(particles, gap, topGapParticle, bottomGapParticle);
        double timeToNextCollision = answer.getLeft();
        this.currentIterationTime += timeToNextCollision;
        Map<String, List<Pair<Particle, Particle>>> collisionedParticles = answer.getRight();

        // Actualización de posiciones
        for (Particle p : particles) {
            p.setX(p.getX() + p.getxVelocity() * timeToNextCollision);
            p.setY(p.getY() + p.getyVelocity() * timeToNextCollision);
        }

        // Actualización de velocidades
        collisionedParticles.get(Constants.WALL_VERTICAL_COLLISION_KEY).stream().map(Pair::getLeft).forEach(
                particle ->
                    particle.setxVelocity(-particle.getxVelocity())
        );
        collisionedParticles.get(Constants.WALL_HORIZONTAL_COLLISION_KEY).stream().map(Pair::getLeft).forEach(
                particle -> particle.setyVelocity(-particle.getyVelocity())
        );

        collisionedParticles.get(Constants.TOP_GAP_COLLISION_KEY).stream().map(Pair::getLeft).forEach(
                particle -> collideParticleToGapEnd(particle, topGapParticle)
        );

        collisionedParticles.get(Constants.BOTTOM_GAP_COLLISION_KEY).stream().map(Pair::getLeft).forEach(
                particle -> collideParticleToGapEnd(particle, bottomGapParticle)
        );

        for (Pair<Particle, Particle> pair : collisionedParticles.get(Constants.PARTICLES_COLLISION_KEY))
            collideParticles(pair.getLeft(), pair.getRight());
    }

    public static void collideParticleToGapEnd(Particle p1, Particle gapEndParticle){
        double sigma = p1.getRadius() + gapEndParticle.getRadius();
        double deltaX = gapEndParticle.getX() - p1.getX();
        double deltaY = gapEndParticle.getY() - p1.getY();
        double deltaVDotDeltaR =
                (0 - p1.getxVelocity()) * deltaX +
                        (0 - p1.getyVelocity()) * deltaY;

        double j = (2 * p1.getMass() * deltaVDotDeltaR) / (sigma * p1.getMass());
        double jx = (j * deltaX) / (sigma);
        double jy = (j * deltaY) / (sigma);

        p1.setxVelocity(p1.getxVelocity() + (jx / p1.getMass()));
        p1.setyVelocity(p1.getyVelocity() + (jy / p1.getMass()));
    }

    public static void collideParticles(Particle p1, Particle p2) {
        double sigma = p1.getRadius() + p2.getRadius();
        double deltaX = p2.getX() - p1.getX();
        double deltaY = p2.getY() - p1.getY();
        double deltaVDotDeltaR =
                (p2.getxVelocity() - p1.getxVelocity()) * deltaX +
                        (p2.getyVelocity() - p1.getyVelocity()) * deltaY;

        double j = (2 * (p2.getMass() * p1.getMass()) * deltaVDotDeltaR) / (sigma * (p1.getMass() + p2.getMass()));
        double jx = (j * deltaX) / (sigma);
        double jy = (j * deltaY) / (sigma);

        p1.setxVelocity(p1.getxVelocity() + (jx / p1.getMass()));
        p1.setyVelocity(p1.getyVelocity() + (jy / p1.getMass()));
        p2.setxVelocity(p2.getxVelocity() - (jx / p2.getMass()));
        p2.setyVelocity(p2.getyVelocity() - (jy / p2.getMass()));
    }

    public static void createStaticFile(String outputName, Integer particlesQty, Double gap) throws FileNotFoundException, UnsupportedEncodingException {
        System.out.println("\tCreating static file. . .");

        PrintWriter writer = new PrintWriter("./results/" + outputName + "/static.txt", "UTF-8");
        writer.println(String.format(Locale.ENGLISH, "%d\n%f %f\n%f\n%f", particlesQty, Constants.SIMULATION_WIDTH, Constants.SIMULATION_HEIGHT, gap, Constants.PARTICLE_VELOCITY));
        writer.close();

        System.out.println("\tStatic file successfully created");
    }


    public void createDynamicFile(String outputName, String iterName) throws FileNotFoundException, UnsupportedEncodingException {
        System.out.println("\tCreating dynamic file. . .");

        PrintWriter writer = new PrintWriter("./results/" + outputName + "/dynamic" + iterName + ".txt", "UTF-8");

        for (int i = 0; i < Constants.SIMULATION_STEPS; i++) {
            writer.println(this.currentIterationTime);
            for (Particle p : this.particles) {
                writer.println(String.format(Locale.ENGLISH, "%d;%f;%f;%f;%f",
                        p.getId(), p.getX(), p.getY(), p.getxVelocity(), p.getyVelocity()));
            }
            nextCollision();
        }
        writer.close();

        System.out.println("\tDynamic file successfully created");

    }
}
