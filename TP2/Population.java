import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;

public class Population {
    private List<Particle> particles;
    private Random rand;
    private Integer time;

    public Population() {
        this.particles = new ArrayList<>();
        this.rand = new Random(Constants.RANDOM_SEED);
        this.time = 0;

        for (int i = 0; i < Constants.PARTICLES_QUANTITY; i++) {
            particles.add(new Particle(rand.nextDouble() * Constants.BOX_LENGTH, rand.nextDouble() * Constants.BOX_LENGTH, rand.nextDouble() * 2 * Math.PI));
        }
    }

    public void nextIteration() {
        double cosAverage = particles.stream().map(Particle::getAngle).mapToDouble(Math::cos).average().getAsDouble();
        double sinAverage = particles.stream().map(Particle::getAngle).mapToDouble(Math::sin).average().getAsDouble();

        double angleAverage = Math.atan2(sinAverage, cosAverage);

        particles.forEach(p -> {
            p.setAngle(angleAverage + (rand.nextDouble() * Constants.NOISE_AMPLITUDE) - Constants.NOISE_AMPLITUDE / 2);
            p.setX(p.getX() + p.getXVelocity() * Constants.DELTA_T);
            p.setY(p.getY() + p.getYVelocity() * Constants.DELTA_T);
        });

        time++;
    }

    public void createOutputFile(String outputName) throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter(outputName + ".txt", "UTF-8");
        writer.println(time);
        for (Particle p : this.particles) {
            writer.println(String.format("%d;%.2f;%.2f;%.2f;%.2f", p.getId(), p.getX(), p.getY(), p.getXVelocity(), p.getYVelocity()));
        }
        writer.close();
    }
}
