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


    public static List<Particle> processFileParticles(Pair<String,String> filepaths) {
        List<Particle> particles = new ArrayList<>();
        try {
            Scanner dynamicScanner = new Scanner(new File(filepaths.getRight()));
            Scanner staticScanner = new Scanner(new File(filepaths.getLeft()));
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

    private static Pair<String,String> getFilepaths(String[] args){

        Pair<String,String> filepaths;

        switch (args.length) {
            case 0:
                filepaths = new Pair<>("Static.txt", "Dynamic.txt");
                break;
            case 1:
                filepaths = new Pair<>(args[0], "Dynamic.txt");
                break;
            case 2:
                filepaths = new Pair<>(args[0], args[1]);
                break;
            default:
                throw new IllegalArgumentException("Only two arguments are allowed!");
        };

        return filepaths;

    }

    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {

        Pair<String,String> filepaths = getFilepaths(args);

        if(args.length!=2){
            AdministrationFile administrationFile = new AdministrationFile();
            administrationFile.generatorFile(args.length == 0);
        }

        List<Particle> particles = processFileParticles(filepaths);
        //System.out.println("L/M = " + boxLength + "/" + Constants.CELLS_QUANTITY + " = " + (double) boxLength / Constants.CELLS_QUANTITY + " > 2*" + PARTICLE_RADIUS + " + rc (rc = " + neighbourRadius + ")    N = " + particles.size());
        Population population = new Population(particles, Constants.NEIGHBOUR_RADIUS, boxLength);

        double maxParticleRadius = particles.stream().map(Particle::getRadius).max(Comparator.naturalOrder()).get();

        if((double) boxLength/ Constants.CELLS_QUANTITY <= Constants.NEIGHBOUR_RADIUS + 2*maxParticleRadius)
            System.out.println("WARNING: Condition L/M > RC + 2*maxParticleRadius is not being fullfilled, the CellIndexMethod may not work correctly");

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

}
