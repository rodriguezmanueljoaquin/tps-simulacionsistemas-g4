import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        int boxLength = 5;//TODO: HACER VARIABLE //L
        double neighbourRadius = 0.5; //rc
        int M = 4;


        List<Particle> particles = createRandomParticles(N,boxLength);
        Population population = new Population(particles,M,boxLength,neighbourRadius,true );

        System.out.println(population);

        Pair<Map<Integer, List<Particle>>, Long> results = population.getResults();

        //Neighbours
        System.out.println(results.getLeft());

        //Execution time
        System.out.println("Exec time : "+results.getRight());


    }
}
