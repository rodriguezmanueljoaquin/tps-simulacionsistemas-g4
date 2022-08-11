import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class SimulationExample {
    static int particlesQuantity;
    static int boxLength;
    static double time = 0;

    private static void createOutputFile(Map<Integer, Set<Particle>> neighboursMap, String outputName) throws FileNotFoundException, UnsupportedEncodingException {
        PrintWriter writer = new PrintWriter(outputName + ".txt", "UTF-8");
        for (Map.Entry<Integer, Set<Particle>> entry : neighboursMap.entrySet()) {
            writer.println(entry.getKey() + ";" + entry.getValue().stream().map(p -> p.getId().toString()).collect(Collectors.joining(",")));
        }
        writer.close();
    }

    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
        AdministrationFile administrationFile = new AdministrationFile();
        administrationFile.generatorFile();

        List<Particle> particles = processFileParticles();
        //System.out.println("L/M = " + boxLength + "/" + Constants.CELLS_QUANTITY + " = " + (double) boxLength / Constants.CELLS_QUANTITY + " > 2*" + PARTICLE_RADIUS + " + rc (rc = " + neighbourRadius + ")    N = " + particles.size());
        Population population = new Population(particles, Constants.NEIGHBOUR_RADIUS, boxLength);

        System.out.println("===== CELL INDEX METHOD =====");
        Pair<Map<Integer, Set<Particle>>, Long> resultsCellIndexMethod = population.getResultsCellIndexMethod(Constants.CELLS_QUANTITY, Constants.PERIODIC_CONDITIONS);

        //Execution time
        System.out.println("Exec time : " + resultsCellIndexMethod.getRight());
        //System.out.println(resultsCellIndexMethod.getLeft());


        System.out.println("===== BRUTE FORCE METHOD =====");
        Pair<Map<Integer, Set<Particle>>, Long> resultsBruteForceMethod = population.getResultsBruteForceMethod(Constants.PERIODIC_CONDITIONS);

        //Execution time
        System.out.println("Exec time : " + resultsBruteForceMethod.getRight());
        //System.out.println(resultsBruteForceMethod.getLeft());

        System.out.println("\nResults are " +
                ((resultsBruteForceMethod.getLeft().equals(resultsCellIndexMethod.getLeft())) ? "" : "not ") + "equal");

        //Create output file
        createOutputFile(resultsCellIndexMethod.getLeft(), "cellIndex_neighbours");
        createOutputFile(resultsBruteForceMethod.getLeft(), "bruteForce_neighbours");
    }


    public static List<Particle> processFileParticles() {
        List<Particle> particles = new ArrayList<>();
        try {
            Scanner dynamicScanner = new Scanner(new File("Dynamic.txt"));
            Scanner staticScanner = new Scanner(new File("Static.txt"));
            time = Double.parseDouble(dynamicScanner.next());
            particlesQuantity = Integer.parseInt(staticScanner.next());
            boxLength = Integer.parseInt(staticScanner.next());


            while (dynamicScanner.hasNext() && staticScanner.hasNext()) {
                particles.add(new Particle(Double.valueOf(dynamicScanner.next()),Double.valueOf(dynamicScanner.next()),Double.valueOf(staticScanner.next())));
                staticScanner.next();
            }

            dynamicScanner.close();
            staticScanner.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return particles;
    }
}
