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

    private static Integer getOptimusCellsQuantity(Population population, double maxParticleRadius){
        double cellsQuantityLimit = boxLength/(Constants.NEIGHBOUR_RADIUS+2*maxParticleRadius); //c
        int currentCellsQuantity = (int) Math.floor(cellsQuantityLimit);
        Pair<Integer,Long> optimusResult = new Pair<>(currentCellsQuantity,null);
        Map<Integer,Long> cellsQuantityTimesMap = new HashMap<>();
        //If c is an int, M = c - 1
        if(cellsQuantityLimit == Math.floor(cellsQuantityLimit) && !Double.isInfinite(cellsQuantityLimit)){
            currentCellsQuantity = (int) cellsQuantityLimit - 1;
        }
        //We try all M's until M == 0
        for(int M = currentCellsQuantity;M>0;M--){;
            Pair<Map<Integer, Set<Particle>>, Long> resultsCellIndexMethod = population.getResultsCellIndexMethod(M, Constants.PERIODIC_CONDITIONS);
            //Check if the execution time has improved
            if(optimusResult.getRight()==null || resultsCellIndexMethod.getRight()<optimusResult.getRight()){
                optimusResult.setLeft(M);
                optimusResult.setRight(resultsCellIndexMethod.getRight());
            }
            //Add M and his current execution time to the map
            cellsQuantityTimesMap.put(M, resultsCellIndexMethod.getRight());
        }

        System.out.println("---All cells quantity times---");
        System.out.println(cellsQuantityTimesMap);

        System.out.println("---Optimus time---");
        System.out.println("Optimus time is : "+optimusResult.getRight()+" ns");

        return optimusResult.getLeft();
    }

    private static int getMaxCellsQuantity(double maxParticleRadius){
        double cellsQuantityLimit = boxLength/(Constants.NEIGHBOUR_RADIUS+2*maxParticleRadius); //c
        int currentCellsQuantity = (int) Math.floor(cellsQuantityLimit);
        //If c is an int, M = c - 1
        if(cellsQuantityLimit == Math.floor(cellsQuantityLimit) && !Double.isInfinite(cellsQuantityLimit)){
            currentCellsQuantity = (int) cellsQuantityLimit - 1;
        }
        return currentCellsQuantity;
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

//        int optimusCellsQuantity = getOptimusCellsQuantity(population,maxParticleRadius);
//
//        System.out.println("---Optimus M---");
//        System.out.println("The optimus M is : "+optimusCellsQuantity);
//        System.out.println("------------------------");

        int cellsQuantity = getMaxCellsQuantity(maxParticleRadius);

        System.out.println("The current M is : "+cellsQuantity);

        if((double) boxLength/ cellsQuantity <= Constants.NEIGHBOUR_RADIUS + 2*maxParticleRadius)
            System.out.println("WARNING: Condition L/M > RC + 2*maxParticleRadius is not being fullfilled, the CellIndexMethod may not work correctly");

        System.out.println("===== CELL INDEX METHOD =====");
        Pair<Map<Integer, Set<Particle>>, Long> resultsCellIndexMethod = population.getResultsCellIndexMethod(cellsQuantity, Constants.PERIODIC_CONDITIONS);

        //Execution time
        System.out.println("Exec time : " + resultsCellIndexMethod.getRight()+" ns");
        //System.out.println(resultsCellIndexMethod.getLeft());


        System.out.println("===== BRUTE FORCE METHOD =====");
        Pair<Map<Integer, Set<Particle>>, Long> resultsBruteForceMethod = population.getResultsBruteForceMethod(Constants.PERIODIC_CONDITIONS);

        //Execution time
        System.out.println("Exec time : " + resultsBruteForceMethod.getRight()+" ns");
        //System.out.println(resultsBruteForceMethod.getLeft());

        System.out.println("\nResults are " +
                ((resultsBruteForceMethod.getLeft().equals(resultsCellIndexMethod.getLeft())) ? "" : "not ") + "equal");

        //Create output file
        createOutputFile(resultsCellIndexMethod.getLeft(), "cellIndex_neighbours");
        createOutputFile(resultsBruteForceMethod.getLeft(), "bruteForce_neighbours");
    }

}
