import java.util.ArrayList;
import java.util.List;

public class SimulationExample {
    private static int boxLength;

    private static List<Particle> createRandomParticles(int N, int boxLength) {
        double x, y;
        List<Particle> particles = new ArrayList<>();

        for (int i = 0; i < N; i++) {
            x = Math.random() * boxLength;
            y = Math.random() * boxLength;
            particles.add(new Particle(x, y));
        }

        return particles;
    }

    public static void main(String[] args) {
        int N = 20; //TODO: HACER VARIABLE
        int boxLength = 5;//TODO: HACER VARIABLE
        double neighbourRadius = 0.5;
        int M = 4;

        List<Particle> particles = createRandomParticles(N,boxLength);
//        Population population = new Population(particles, )
    }
}
