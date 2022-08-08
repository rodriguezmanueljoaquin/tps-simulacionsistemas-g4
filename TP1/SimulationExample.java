import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class SimulationExample {

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

    private static void createOutputFile(Map<Integer, Set<Particle>> neighboursMap) throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter("neighbours.txt", "UTF-8");
        for (Map.Entry<Integer, Set<Particle>> entry : neighboursMap.entrySet()) {
            writer.println("[ Id = " + entry.getKey() + " ; neighbours = " + entry.getValue().stream().map(p -> p.getId().toString()).collect(Collectors.joining(",")) + "]");
        }
        writer.close();
    }

    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
        int N = 3000; //TODO: HACER VARIABLE
        int boxLength = 50;//TODO: HACER VARIABLE //L
        double neighbourRadius = 0.5; //rc
        int M = 40;

        System.out.println("L/M = " + (double) boxLength/M +"   rc = " + neighbourRadius);

        List<Particle> particles = createRandomParticles(N, boxLength);
        Population population = new Population(particles, neighbourRadius, boxLength);

//        System.out.println(population);

        System.out.println("===== CELL INDEX METHOD =====");

        Pair<Map<Integer, Set<Particle>>, Long> resultsCellIndexMethod = population.getResultsCellIndexMethod(M, false);

        //Neighbours
//        System.out.println(resultsCellIndexMethod.getLeft());

        //Execution time
        System.out.println("Exec time : " + resultsCellIndexMethod.getRight());

        System.out.println("===== BRUTE FORCE METHOD =====");

        Pair<Map<Integer, Set<Particle>>, Long> resultsBruteForceMethod = population.getResultsBruteForceMethod(false);

        //Neighbours
//        System.out.println(resultsCellIndexMethod.getLeft());

        //Execution time
        System.out.println("Exec time : " + resultsBruteForceMethod.getRight());

        System.out.println("Results are " +
                ((resultsBruteForceMethod.getLeft().equals(resultsCellIndexMethod.getLeft()))? "":"not ") +
                "equal");

//        System.out.println(resultsBruteForceMethod.getLeft());
//        System.out.println(resultsCellIndexMethod.getLeft());

        //Create output file
        createOutputFile(resultsCellIndexMethod.getLeft());


    }
}
