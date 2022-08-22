import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;

public class Population {
    private Set<Particle> particles;
    private Random rand;
    private Integer time;

    public Population() {
        this.particles = new HashSet<>();
        this.rand = new Random(Constants.RANDOM_SEED);
        this.time = 0;

        for (int i = 0; i < Constants.PARTICLES_QUANTITY; i++) {
            particles.add(new Particle(rand.nextDouble() * Constants.BOX_LENGTH, rand.nextDouble() * Constants.BOX_LENGTH, rand.nextDouble() * 2 * Math.PI));
        }
    }

    private double doublePositiveMod(double d, int mod){
        if(d > mod)
            return  (d - mod);
        else if (d < 0) {
            return (d + mod);
        }else return d;
    }

    public void nextIteration() {
        Map<Particle, Set<Particle>> particleNeighbours = CellIndexMethod.getNeighboursCellIndexMethod(particles);

        particleNeighbours.forEach((p, neighbourhood) -> {
            neighbourhood.add(p); // El promedio debe tener en cuenta a la particula analizada, tambien nos asegura de que el getAsDouble no da error
            double cosAverage = neighbourhood.stream().map(Particle::getAngle).mapToDouble(Math::cos).average().getAsDouble();
            double sinAverage = neighbourhood.stream().map(Particle::getAngle).mapToDouble(Math::sin).average().getAsDouble();

            double angleAverage = Math.atan2(sinAverage, cosAverage);
            p.setAngle(angleAverage + (rand.nextDouble() * Constants.NOISE_AMPLITUDE) - Constants.NOISE_AMPLITUDE / 2);
            p.setX(doublePositiveMod(p.getX() + p.getXVelocity() * Constants.DELTA_T, Constants.BOX_LENGTH));
            p.setY(doublePositiveMod(p.getY() + p.getYVelocity() * Constants.DELTA_T, Constants.BOX_LENGTH));
        });

        time++;
        this.particles = particleNeighbours.keySet();
    }

    public void runSimulation(String outputName) throws FileNotFoundException, UnsupportedEncodingException {
        File file = new File("./results/" + outputName);

        if(!file.mkdir())
            throw new FileNotFoundException("CARPETA '" + outputName + "' YA EXISTENTE"); // TODO: MEJORAR EXCEPCION

        PrintWriter writer = new PrintWriter("./results/" + outputName + "/static.txt", "UTF-8");
        writer.println(String.format("%.2f\n%d\n%.2f\n%d\n",
                Constants.NOISE_AMPLITUDE, Constants.PARTICLES_QUANTITY, Constants.PARTICLE_VELOCITY, Constants.BOX_LENGTH));
        writer.close();

        writer = new PrintWriter("./results/" + outputName + "/dynamic.txt", "UTF-8");
        for(int i = 0; i < 1000 ;i++){
            writer.println(time);
            for (Particle p : this.particles) {
                writer.println(String.format("%d;%.2f;%.2f;%.2f;%.2f", p.getId(), p.getX(), p.getY(), p.getXVelocity(), p.getYVelocity()));
            }
            writer.println();
            nextIteration();
        }
        writer.close();
    }
}
