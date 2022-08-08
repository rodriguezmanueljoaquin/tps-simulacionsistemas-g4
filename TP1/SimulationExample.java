import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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

    private static void createOutputFile(Map<Integer,Set<Particle>> neighboursMap) throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter("neighbours.txt", "UTF-8");
        for(Map.Entry<Integer,Set<Particle>> entry : neighboursMap.entrySet()){
            writer.println("[ Id = "+entry.getKey()+" ; neighbours = "+entry.getValue().stream().map(p -> p.getId().toString()).collect(Collectors.joining(", "))+"]");
        }
        writer.close();
    }

    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
        int N = 20; //TODO: HACER VARIABLE
        int boxLength = 5;//TODO: HACER VARIABLE //L
        double neighbourRadius = 0.5; //rc
        int M = 4;


        List<Particle> particles = createRandomParticles(N,boxLength);
        Population population = new Population(particles,M,boxLength,neighbourRadius,true );

        System.out.println(population);

        Pair<Map<Integer, Set<Particle>>, Long> results = population.getResults();

        //Neighbours
        System.out.println(results.getLeft());

        //Execution time
        System.out.println("Exec time : "+results.getRight());

        //Create output file
        createOutputFile(results.getLeft());


    }
}
