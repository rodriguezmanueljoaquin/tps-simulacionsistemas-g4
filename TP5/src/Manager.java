import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class Manager {
    public static void main(String[] args) {
        String RESULTS_PATH = "results/";
        String extraParametersStr = "";
        new File("results").mkdir();
        Pair<Double, Double> zombieAPRange = new Pair<>(1000., 2000.);
        Pair<Double, Double> zombieBPRange = new Pair<>(0.25, .75);
        Pair<Double, Double> humanAPRange = new Pair<>(750., 1000.);
        Pair<Double, Double> humanBPRange = new Pair<>(0.25, .75);
        Pair<Double, Double> wallAPRange = new Pair<>(100., 1500.);
        Pair<Double, Double> wallBPRange = new Pair<>(0.25, .75);

        ArrayList<SimulationParameters> simulationParameters = new ArrayList<>();
        int deltaTOutputMultiplier = 4 * 20 * 10; // 10s
//        int deltaTOutputMultiplier = 4; // 0.05s

        // variando cantidad de humanos
        Integer[] initialHumansQtyArray = new Integer[]{2, 10, 40, 80, 140, 200, 260, 320};
//        Integer[] initialHumansQtyArray = new Integer[]{80};
        double zombieDesiredVelocity = 3;
        for (Integer initialHumansQty : initialHumansQtyArray)
            simulationParameters.add(new SimulationParameters(initialHumansQty, zombieDesiredVelocity, extraParametersStr,
                    zombieAPRange, zombieBPRange, humanAPRange, humanBPRange, wallAPRange, wallBPRange));

        //variando velocidad deseada del zombie
//        int initialHumansQty = 200;
//        for (double zombieDesiredVelocity = 1 ; zombieDesiredVelocity <= 5 ; zombieDesiredVelocity+=0.5)
//            simulationParameters.add(new SimulationParameters(initialHumansQty, zombieDesiredVelocity, extraParametersStr,
//                    zombieAPRange, zombieBPRange, humanAPRange, humanBPRange, wallAPRange, wallBPRange));

        // variando el coeficiente Aphumanos/Apzombies
//        for (double AphOverApzCoefficient = 0; AphOverApzCoefficient < 2. ; AphOverApzCoefficient += 0.25){
//            // ApWall y ApZombies se mantienen como antes pero se cambia el Aphumanos
//            humanAPRange.setNewValues(zombieAPRange.getLeft() * AphOverApzCoefficient, zombieAPRange.getRight() * AphOverApzCoefficient);
//            extraParameters = String.valueOf(AphOverApzCoefficient);
//            simulationParameters.add(new SimulationParameters(140, zombieDesiredVelocity, extraParametersStr,
//                    zombieAPRange, zombieBPRange, humanAPRange, humanBPRange, wallAPRange, wallBPRange));
//        }


        System.out.println("Starting simulations");
        simulationParameters.forEach(parameters -> {
            try {
                Random rand = new Random(Constants.RANDOM_SEED);
                String resultsFolderPath = String.format(Locale.ENGLISH, RESULTS_PATH + "%d_%.2f_%s", parameters.initialHumansQty, parameters.zombieDesiredVelocity, parameters.extraParametersStr);
                //Chequeamos si la carpeta con resultados existe, y en caso de que no, la creamos
                if (!Files.exists(Paths.get(resultsFolderPath))) {
                    new File(resultsFolderPath).mkdir();
                }
                for (int i = 0; i < Constants.SIMULATION_REPETITION_TIMES; i++) {
                    System.out.println("Iteration " + i + " for nH:" + parameters.initialHumansQty + " vz:" + parameters.zombieDesiredVelocity + " extra parameters:" + parameters.extraParametersStr);
                    Population simulation = new Population(parameters.initialHumansQty, parameters.zombieDesiredVelocity, rand.nextLong(),
                            parameters.zombieAPRange, parameters.zombieBPRange, parameters.humanAPRange, parameters.humanBPRange,
                            parameters.wallAPRange, parameters.wallBPRange, deltaTOutputMultiplier);
                    Population.createStaticFile(resultsFolderPath, parameters.initialHumansQty, parameters.zombieDesiredVelocity, deltaTOutputMultiplier);

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
        public String extraParametersStr;

        public SimulationParameters(Integer initialHumansQty, Double zombieDesiredVelocity, String extraParametersStr,
                                    Pair<Double, Double> zombieAPRange, Pair<Double, Double> zombieBPRange,
                                    Pair<Double, Double> humanAPRange, Pair<Double, Double> humanBPRange,
                                    Pair<Double, Double> wallAPRange, Pair<Double, Double> wallBPRange) {
            this.initialHumansQty = initialHumansQty;
            this.zombieDesiredVelocity = zombieDesiredVelocity;
            this.extraParametersStr = extraParametersStr;
            this.zombieAPRange = zombieAPRange;
            this.zombieBPRange = zombieBPRange;
            this.humanAPRange = humanAPRange;
            this.humanBPRange = humanBPRange;
            this.wallAPRange = wallAPRange;
            this.wallBPRange = wallBPRange;
        }
    }
}
