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


    public Population(Integer particlesQty, Double width, Double height, Double gap) {
        this.particlesQty = particlesQty;
        this.particles = new ArrayList<>();
        this.rand = new Random(Constants.RANDOM_SEED);
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

    public void nextCollision() {
        double minTime = Double.MAX_VALUE;
        //CALCULAR CHOQUES
        for (int i = 0; i < particles.size(); i++) {
            for (int j = i + 1; j < particles.size(); j++) {
                double time = getTimeToCollision(particles.get(i), particles.get(j));
                if (time < minTime) minTime = time;
            }
            double wallTime = getTimeToWall(particles.get(i));
            if (wallTime < minTime) minTime = wallTime;
        }
        //AGARRAR EL 1° Y ACTUALIZAR VELOCIDADES DE LAS PARTICULAS

        for (Particle p : particles) {
            p.setX(p.getX() + p.getxVelocity() * minTime);
            p.setY(p.getY() + p.getyVelocity() * minTime);
        }

        //actualizar las velocidades de las que chocaron


        //VOLVER AL PUNTO 1

    }

    private double getTimeToWall(Particle p1) {
        double timeToVertical;
        double timeToHorizontal;
        if (p1.getxVelocity() > 0) {
            timeToVertical = ((p1.getX() < width / 2  ? width / 2 : width) - p1.getX() - Constants.PARTICLE_RADIUS) / p1.getxVelocity();
        } else {
            timeToVertical = ((p1.getX() < width / 2 ? 0 : width / 2) - p1.getX() + Constants.PARTICLE_RADIUS) / p1.getxVelocity();
        }

        timeToHorizontal = (
                (p1.getyVelocity() > 0 ?
                        height - Constants.PARTICLE_RADIUS : Constants.PARTICLE_RADIUS)
                        - p1.getY()) / p1.getyVelocity();
        return Math.min(timeToVertical, timeToHorizontal);
    }

    private double getTimeToCollision(Particle p1, Particle p2) {
        double deltaRSquared = Math.pow(p1.getX() - p2.getX(), 2) + Math.pow(p1.getY() - p2.getY(), 2);
        double deltaVSquared = Math.pow(p1.getxVelocity() - p2.getxVelocity(), 2) + Math.pow(p1.getyVelocity() - p2.getyVelocity(), 2);
        double deltaVDotDeltaR = (p1.getxVelocity() - p2.getxVelocity()) * (p1.getX() - p2.getX()) + (p1.getyVelocity() - p2.getyVelocity()) * (p1.getY() - p2.getY());
        double d = Math.pow(deltaVDotDeltaR, 2) - deltaVSquared * (deltaRSquared - Math.pow(2 * Constants.PARTICLE_RADIUS, 2));
        if (deltaVDotDeltaR >= 0 || d < 0) {
            return Double.MAX_VALUE;
        }
        return (-1) * (deltaVDotDeltaR + Math.sqrt(d)) / (deltaVSquared);

    }
}
