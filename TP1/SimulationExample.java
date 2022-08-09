import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class SimulationExample {
    static int N;
    static int boxLength;
    static double neighbourRadius = 0.1; //rc
    static int cellsQuantity = 4;
    static double time = 0;
    static boolean periodicConditions = false;
    static double PARTICLE_RADIUS = 0.25;


    private static List<Particle> createRandomParticles(int N, int boxLength) {
        double x, y;
        List<Particle> particles = new ArrayList<>();

        for (int i = 0; i < N; i++) {
            x = Math.random() * boxLength;
            y = Math.random() * boxLength;
            particles.add(new Particle(x, y, PARTICLE_RADIUS));
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
       /*
        int N = 3000; //TODO: HACER VARIABLE
        int boxLength = 50;//TODO: HACER VARIABLE //L
        double neighbourRadius = 0.5; //rc
        int M = 40;


        System.out.println("L/M = " + (double) boxLength/M +"   rc = " + neighbourRadius);

        List<Particle> particles = createRandomParticles(N, boxLength);

        */

        AdministrationFile administrationFile = new AdministrationFile();
        administrationFile.generatorFile();

        List<Particle> particles = processFileParticles();
        System.out.println("L/M = " + boxLength + "/" + cellsQuantity + " = " + (double) boxLength / cellsQuantity + " > 2*" + PARTICLE_RADIUS + " + rc (rc = " + neighbourRadius + ")    N = " + particles.size());
        //particles = particles.subList(0, 2); //TESTING
        Population population = new Population(particles, neighbourRadius, boxLength);

//        System.out.println(population);

        System.out.println("===== CELL INDEX METHOD =====");

        Pair<Map<Integer, Set<Particle>>, Long> resultsCellIndexMethod = population.getResultsCellIndexMethod(cellsQuantity, periodicConditions);

        //Execution time
        System.out.println("Exec time : " + resultsCellIndexMethod.getRight());


        System.out.println("===== BRUTE FORCE METHOD =====");
        Pair<Map<Integer, Set<Particle>>, Long> resultsBruteForceMethod = population.getResultsBruteForceMethod(periodicConditions);

        //Execution time
        System.out.println("Exec time : " + resultsBruteForceMethod.getRight());


        //Neighbours
/*        System.out.println(resultsCellIndexMethod.getLeft());
        System.out.println(resultsBruteForceMethod.getLeft());*/

        System.out.println("Results are " +
                ((resultsBruteForceMethod.getLeft().equals(resultsCellIndexMethod.getLeft())) ? "" : "not ") +
                "equal");

        //Create output file
        createOutputFile(resultsCellIndexMethod.getLeft());


    }


    public static List<Particle> processFileParticles() {
        List<Particle> particles = new ArrayList<>();
        try {
            Scanner dynamicScanner = new Scanner(new File("Dynamic.txt"));
            Scanner staticScanner = new Scanner(new File("Static.txt"));
            time = Double.parseDouble(dynamicScanner.next());
            N = Integer.parseInt(staticScanner.next());
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
