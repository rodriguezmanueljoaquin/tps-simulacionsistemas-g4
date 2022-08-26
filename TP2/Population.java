import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;

public class Population {
    private Set<Particle> particles;
    private Random rand;
    private Integer time;
    private Integer particlesQty;
    private Double noiseAmplitude;
    private Double boxLength;
    

    public Population(Integer particlesQty, Double noiseAmplitude, Double boxLength) {
        this.particlesQty = particlesQty;
        this.noiseAmplitude = noiseAmplitude;
        this.boxLength = boxLength;
        this.particles = new HashSet<>();
        this.rand = new Random(Constants.RANDOM_SEED);
        this.time = 0;

        for (int i = 0; i < this.particlesQty; i++) {
            particles.add(new Particle(rand.nextDouble() * this.boxLength, rand.nextDouble() * this.boxLength, rand.nextDouble() * 2 * Math.PI));
        }
    }

    private double doublePositiveMod(double d, double mod){
        if(d > mod)
            return  (d - mod);
        else if (d < 0) {
            return (d + mod);
        }else return d;
    }

    public void nextIteration() {
        Map<Particle, Set<Particle>> particleNeighbours = CellIndexMethod.getNeighboursCellIndexMethod(particles, this.boxLength);

        particleNeighbours.forEach((p, neighbourhood) -> {
            neighbourhood.add(p); // El promedio debe tener en cuenta a la particula analizada, tambien nos asegura de que el getAsDouble no da error
            double cosAverage = neighbourhood.stream().map(Particle::getAngle).mapToDouble(Math::cos).average().getAsDouble();
            double sinAverage = neighbourhood.stream().map(Particle::getAngle).mapToDouble(Math::sin).average().getAsDouble();

            double angleAverage = Math.atan2(sinAverage, cosAverage);
            p.setAngle(angleAverage + (rand.nextDouble() * this.noiseAmplitude) - this.noiseAmplitude / 2);
            p.setX(doublePositiveMod(p.getX() + p.getXVelocity() * Constants.DELTA_T, this.boxLength));
            p.setY(doublePositiveMod(p.getY() + p.getYVelocity() * Constants.DELTA_T, this.boxLength));
        });

        time++;
        this.particles = particleNeighbours.keySet();
    }

    public void runSimulation(String outputName) throws FileNotFoundException, UnsupportedEncodingException {
        File file = new File("./results/" + outputName);

        if(!file.mkdir())
            throw new FileNotFoundException("CARPETA '" + outputName + "' YA EXISTENTE"); // TODO: MEJORAR EXCEPCION

        PrintWriter writer = new PrintWriter("./results/" + outputName + "/static.txt", "UTF-8");
        writer.println(String.format("%.2f\n%d\n%.2f\n%.2f",
                this.noiseAmplitude, this.particlesQty, Constants.PARTICLE_VELOCITY, this.boxLength));
        writer.close();

        writer = new PrintWriter("./results/" + outputName + "/dynamic.txt", "UTF-8");
        for(int i = 0; i < 500; i++){
            writer.println(time);
            for (Particle p : this.particles) {
                writer.println(String.format("%d;%.2f;%.2f;%.2f;%.2f", p.getId(), p.getX(), p.getY(), p.getXVelocity(), p.getYVelocity()));
            }
            nextIteration();
        }
        writer.close();
    }
}
