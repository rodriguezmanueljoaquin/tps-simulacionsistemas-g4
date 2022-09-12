import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;

public class Population {
    private List<Particle> particles;
    private Random rand;
    private Integer particlesQty;
    private Double height;
    private Double width;
    private Double gap;

    private static final String WALL_VERTICAL_COLLISION_KEY = "WALL_VERTICAL";
    private static final String WALL_HORIZONTAL_COLLISION_KEY = "WALL_HORIZONTAL";
    private static final String PARTICLES_COLLISION_KEY = "PARTICLES";

    public Population(Integer particlesQty, Double width, Double height, Double gap, long seed) {
        this.particlesQty = particlesQty;
        this.particles = new ArrayList<>();
        this.rand = new Random(seed);
        this.width = width;
        this.height = height;
        this.gap = gap;


        for (int i = 0; i < this.particlesQty; i++) {
            boolean validPos = false;
            Particle particle = null;
            while (!validPos) {
                validPos = true;
                particle = new Particle((rand.nextDouble() * (this.width / 2 - Constants.PARTICLE_RADIUS * 2)) + Constants.PARTICLE_RADIUS, (rand.nextDouble() * (this.height - Constants.PARTICLE_RADIUS * 2)) + Constants.PARTICLE_RADIUS, rand.nextDouble() * 2 * Math.PI);
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

    public Pair<Double, Map<String, List<Pair<Particle, Particle>>>> getCollisionTimeAndParticles() {
        double timeToNextCollision = Double.MAX_VALUE;
        Map<String, List<Pair<Particle, Particle>>> collisionedParticles = new HashMap<>();
        collisionedParticles.put(WALL_VERTICAL_COLLISION_KEY, new ArrayList<>());
        collisionedParticles.put(WALL_HORIZONTAL_COLLISION_KEY, new ArrayList<>());
        collisionedParticles.put(PARTICLES_COLLISION_KEY, new ArrayList<>());

        for (int i = 0; i < particles.size(); i++) {
            for (int j = i + 1; j < particles.size(); j++) {
                double timeToParticleCollision = getTimeToParticleCollision(particles.get(i), particles.get(j));
                if (timeToParticleCollision <= timeToNextCollision) {
                    if (timeToParticleCollision != timeToNextCollision) {
                        timeToNextCollision = timeToParticleCollision;
                        collisionedParticles.replaceAll((key, pairs) -> new ArrayList<>());
                    }
                    collisionedParticles.get(PARTICLES_COLLISION_KEY).add(new Pair<>(particles.get(i), particles.get(j)));
                }
            }

            Pair<Double, String> wallTimeAndType = timeToWallCollisionAndType(particles.get(i));
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

    public void nextCollision() {
        Pair<Double, Map<String, List<Pair<Particle, Particle>>>> answer = getCollisionTimeAndParticles();
        double timeToNextCollision = answer.getLeft();
        Map<String, List<Pair<Particle, Particle>>> collisionedParticles = answer.getRight();

        // Actualización de posiciones
        for (Particle p : particles) {
            p.setX(p.getX() + p.getxVelocity() * timeToNextCollision);
            p.setY(p.getY() + p.getyVelocity() * timeToNextCollision);
        }

        // Actualización de velocidades
        collisionedParticles.get(WALL_VERTICAL_COLLISION_KEY).stream().map(Pair::getLeft).forEach(
                particle -> particle.setxVelocity(-particle.getxVelocity())
        );
        collisionedParticles.get(WALL_HORIZONTAL_COLLISION_KEY).stream().map(Pair::getLeft).forEach(
                particle -> particle.setyVelocity(-particle.getyVelocity())
        );

        for (Pair<Particle, Particle> pair : collisionedParticles.get(PARTICLES_COLLISION_KEY)) {
            Particle p1 = pair.getLeft();
            Particle p2 = pair.getRight();
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
    }

    private Pair<Double, String> timeToWallCollisionAndType(Particle p) {
        double timeToVertical;
        double timeToHorizontal;

        if (p.getxVelocity() > 0) {
            if (p.getX() > width / 2 ||
                    Math.abs(p.getY() + p.getyVelocity() * p.getxVelocity() / (width / 2 - p.getX()) - width / 2) < gap / 2)
                // que este en la caja derecha o pase por el gap hacia esa caja
                timeToVertical = (width - p.getX() - p.getRadius()) / p.getxVelocity();
            else {
                timeToVertical = (width / 2 - p.getX() - p.getRadius()) / p.getxVelocity();
            }
        } else {
            if (p.getX() < width / 2 ||
                    Math.abs(p.getY() + p.getyVelocity() * p.getxVelocity() / (width / 2 - p.getX()) - width / 2) < gap / 2)
                timeToVertical = (-p.getX() + p.getRadius()) / p.getxVelocity();
            else {
                timeToVertical = (width / 2 - p.getX() + p.getRadius()) / p.getxVelocity();
            }
        }

        timeToHorizontal = (
                (p.getyVelocity() > 0 ?
                        height - p.getRadius() : p.getRadius())
                        - p.getY()) / p.getyVelocity();

        if (timeToVertical < timeToHorizontal)
            return new Pair<>(timeToVertical, WALL_VERTICAL_COLLISION_KEY);
        else return new Pair<>(timeToHorizontal, WALL_HORIZONTAL_COLLISION_KEY);
    }

    private double getTimeToParticleCollision(Particle p1, Particle p2) {
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

    public static void createStaticFile(String outputName, Integer particlesQty, Double width, Double height, Double gap) throws FileNotFoundException, UnsupportedEncodingException {
        System.out.println("\tCreating static file. . .");

        PrintWriter writer = new PrintWriter("./results/" + outputName + "/static.txt", "UTF-8");
        writer.println(String.format(Locale.ENGLISH, "%d\n%f %f\n%f\n%f", particlesQty, width, height, gap, Constants.PARTICLE_VELOCITY));
        writer.close();

        System.out.println("\tStatic file successfully created");
    }


    public void createDynamicFile(String outputName, String iterName) throws FileNotFoundException, UnsupportedEncodingException {
        System.out.println("\tCreating dynamic file. . .");

        PrintWriter writer = new PrintWriter("./results/" + outputName + "/dynamic" + iterName + ".txt", "UTF-8");

        for (int i = 0; i < Constants.SIMULATION_STEPS; i++) {
            writer.println(i);
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
