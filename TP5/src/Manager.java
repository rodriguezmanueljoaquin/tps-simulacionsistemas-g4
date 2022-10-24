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
        Integer[] initialHumansQtyArray = new Integer[]{2};
        double zombieDesiredVelocity = 0.3;
        for (Integer integer : initialHumansQtyArray)
            simulationParameters.add(new SimulationParameters(integer, zombieDesiredVelocity));


        //variando velocidad deseada del zombie
      /*  for (double i = 10 ; i <= 25 ; i+=5)
            simulationParameters.add(new SimulationParameters(2500, 1., i));

       */

        Random rand = new Random(Constants.RANDOM_SEED);
        simulationParameters.forEach(parameters -> {
            try {
                String resultsFolderPath = String.format(Locale.ENGLISH, RESULTS_PATH + "%d_%.2f", parameters.initialHumansQty, parameters.zombieDesiredVelocity);
                //Chequeamos si la carpeta con resultados existe, y en caso de que no, la creamos
                if (!Files.exists(Paths.get(resultsFolderPath))) {
                    new File(resultsFolderPath).mkdir();
                }
                for (int i = 0; i < Constants.SIMULATION_REPETITIONS; i++) {
                    Population simulation = new Population(parameters.initialHumansQty, parameters.zombieDesiredVelocity, rand.nextLong());
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

        public SimulationParameters(Integer initialHumansQty, Double zombieDesiredVelocity) {
            this.initialHumansQty = initialHumansQty;
            this.zombieDesiredVelocity = zombieDesiredVelocity;
        }
    }
}
