import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class Manager {
    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
        String RESULTS_PATH = "results/";
        new File("results").mkdir();

        ArrayList<SimulationParameters> simulationParameters = new ArrayList<>();
        // variando cantidad de humanos;
//        Integer[] initialHumansQtyArray = new Integer[]{2, 10, 40, 80, 140, 200, 260, 320};
        Integer[] initialHumansQtyArray = new Integer[]{200};
        double zombieDesiredVelocity = 3;
         Pair<Double, Double> zombieAPRange = new Pair<>(1800., 2200.);
         Pair<Double, Double> zombieBPRange = new Pair<>(0.3,0.7);
         Pair<Double, Double> humanAPRange = new Pair<>(400., 600.);
         Pair<Double, Double> humanBPRange = new Pair<>(0.3, 0.7);
         Pair<Double, Double> wallAPRange = new Pair<>(200. , 400.);
         Pair<Double, Double> wallBPRange = new Pair<>(0.3, 0.7);
        for (Integer integer : initialHumansQtyArray)
            simulationParameters.add(new SimulationParameters(integer, zombieDesiredVelocity,
                    zombieAPRange, zombieBPRange, humanAPRange, humanBPRange, wallAPRange, wallBPRange));


        //variando velocidad deseada del zombie
//        int initialHumansQty = 140;
//        for (zombieDesiredVelocity = 1 ; zombieDesiredVelocity <= 5 ; zombieDesiredVelocity+=0.5)
//            simulationParameters.add(new SimulationParameters(initialHumansQty, zombieDesiredVelocity, zombieAPRange, zombieBPRange, humanAPRange, humanBPRange, wallAPRange, wallBPRange));



        System.out.println("Starting simulations");
        simulationParameters.forEach(parameters -> {
            try {
                Random rand = new Random(Constants.RANDOM_SEED);
                String resultsFolderPath = String.format(Locale.ENGLISH, RESULTS_PATH + "%d_%.2f", parameters.initialHumansQty, parameters.zombieDesiredVelocity);
                //Chequeamos si la carpeta con resultados existe, y en caso de que no, la creamos
                if (!Files.exists(Paths.get(resultsFolderPath))) {
                    new File(resultsFolderPath).mkdir();
                }
                for (int i = 0; i < Constants.SIMULATION_REPETITION_TIMES; i++) {
                    System.out.println("Iteration " + i);
                    Population simulation = new Population(parameters.initialHumansQty, parameters.zombieDesiredVelocity, rand.nextLong(),
                            parameters.zombieAPRange, parameters.zombieBPRange, parameters.humanAPRange, parameters.humanBPRange,
                            parameters.wallAPRange, parameters.wallBPRange);
                    Population.createStaticFile(resultsFolderPath, parameters.initialHumansQty, parameters.zombieDesiredVelocity);

                    String dynamicsPath = resultsFolderPath + "/dynamics";
                    new File(dynamicsPath).mkdir();
                    simulation.createDynamicFile(dynamicsPath, "dynamic" + i + ".txt");
                }
            } catch (FileNotFoundException | UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static class SimulationParameters {
        public Integer initialHumansQty;
        public Double zombieDesiredVelocity;
        public Pair<Double, Double> zombieAPRange;
        public Pair<Double, Double> zombieBPRange;
        public Pair<Double, Double> humanAPRange;
        public Pair<Double, Double> humanBPRange;
        public Pair<Double, Double> wallAPRange;
        public Pair<Double, Double> wallBPRange;

        public SimulationParameters(Integer initialHumansQty, Double zombieDesiredVelocity,
                                    Pair<Double, Double> zombieAPRange, Pair<Double, Double> zombieBPRange,
                                    Pair<Double, Double> humanAPRange, Pair<Double, Double> humanBPRange,
                                    Pair<Double, Double> wallAPRange, Pair<Double, Double> wallBPRange) {
            this.initialHumansQty = initialHumansQty;
            this.zombieDesiredVelocity = zombieDesiredVelocity;
            this.zombieAPRange = zombieAPRange;
            this.zombieBPRange = zombieBPRange;
            this.humanAPRange = humanAPRange;
            this.humanBPRange = humanBPRange;
            this.wallAPRange = wallAPRange;
            this.wallBPRange = wallBPRange;
        }
    }
}
