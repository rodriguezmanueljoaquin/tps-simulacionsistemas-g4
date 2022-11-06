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

        double zombieMeanAp = 1400;
        double zombieStdAp = 500;
        double humanMeanAp = 600;
        double humanStdAp = 50;
        double wallMeanAp = 800;
        double wallStdAp = 50;
        Pair<Double, Double> zombieAPRange = new Pair<>(zombieMeanAp - zombieStdAp, zombieMeanAp + zombieStdAp);
        Pair<Double, Double> zombieBPRange = new Pair<>(0.45, 0.55);
        Pair<Double, Double> humanAPRange = new Pair<>(humanMeanAp - humanStdAp, humanMeanAp + humanStdAp);
        Pair<Double, Double> humanBPRange = new Pair<>(0.45, 0.55);
        Pair<Double, Double> wallAPRange = new Pair<>(wallMeanAp - wallStdAp, wallMeanAp + wallStdAp);
        Pair<Double, Double> wallBPRange = new Pair<>(0.45, 0.55);

        extraParametersStr = String.format("%.2f", humanAPRange.getLeft() / zombieAPRange.getLeft());

        ArrayList<SimulationParameters> simulationParameters = new ArrayList<>();
        int deltaTOutputMultiplier = 4 * 20 * 10; // 10s
//        int deltaTOutputMultiplier = 4; // 0.05s

        // variando cantidad de humanos
        Integer[] initialHumansQtyArray = new Integer[]{2, 10, 40, 80, 140, 200, 260, 320, 400};
//        Integer[] initialHumansQtyArray = new Integer[]{320};
//        double zombieDesiredVelocity = 3;
//        for (Integer initialHumansQty : initialHumansQtyArray)
//            simulationParameters.add(new SimulationParameters(initialHumansQty, zombieDesiredVelocity, extraParametersStr,
//                    zombieAPRange, zombieBPRange, humanAPRange, humanBPRange, wallAPRange, wallBPRange));

        //variando velocidad deseada del zombie
//        int initialHumansQty = 140;
//        for (double zombieDesiredVelocity = 1 ; zombieDesiredVelocity <= 5 ; zombieDesiredVelocity+=0.5)
//            simulationParameters.add(new SimulationParameters(initialHumansQty, zombieDesiredVelocity, extraParametersStr,
//                    zombieAPRange, zombieBPRange, humanAPRange, humanBPRange, wallAPRange, wallBPRange));

        // variando el coeficiente Aphumanos/Apzombies, hasta 1.2 por que no tiene sentido que le tenga mas miedo a los humanos que a los zombies
//        for (double AphOverApzCoefficient = 0; AphOverApzCoefficient <= 1.2; AphOverApzCoefficient += 0.4){
//            // ApWall y ApZombies se mantienen como antes pero se cambia el Aphumanos
//            extraParametersStr = String.format("%.2f", AphOverApzCoefficient);
//            simulationParameters.add(new SimulationParameters(80, 3., extraParametersStr,
//                    zombieAPRange, zombieBPRange, new Pair<>(zombieAPRange.getLeft() * AphOverApzCoefficient,
//                    zombieAPRange.getRight() * AphOverApzCoefficient), humanBPRange, wallAPRange, wallBPRange));
//            simulationParameters.add(new SimulationParameters(140, 3., extraParametersStr,
//                    zombieAPRange, zombieBPRange, new Pair<>(zombieAPRange.getLeft() * AphOverApzCoefficient,
//                    zombieAPRange.getRight() * AphOverApzCoefficient), humanBPRange, wallAPRange, wallBPRange));
//            simulationParameters.add(new SimulationParameters(200, 3., extraParametersStr,
//                    zombieAPRange, zombieBPRange, new Pair<>(zombieAPRange.getLeft() * AphOverApzCoefficient,
//                    zombieAPRange.getRight() * AphOverApzCoefficient), humanBPRange, wallAPRange, wallBPRange));
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
                    Population.createStaticFile(resultsFolderPath, parameters.initialHumansQty, parameters.zombieDesiredVelocity, deltaTOutputMultiplier, parameters.extraParametersStr);

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
