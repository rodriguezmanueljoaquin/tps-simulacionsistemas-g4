import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

public class Manager {
    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
        String RESULTS_PATH = "results/";
        new File("results").mkdir();

        ArrayList<SimulationParameters> simulationParameters = new ArrayList<>();
        // variando cantidad de humanos;
        Integer initialHumansQtyArray [] = new Integer[]{2, 10, 40, 80, 140, 200, 260, 320};
        double zombieDesiredVelocity = 0.3;
        for (int i = 0 ; i < initialHumansQtyArray.length ; i++)
            simulationParameters.add(new SimulationParameters(initialHumansQtyArray[i],zombieDesiredVelocity));


        //variando velocidad deseada del zombie
      /*  for (double i = 10 ; i <= 25 ; i+=5)
            simulationParameters.add(new SimulationParameters(2500, 1., i));

       */

        simulationParameters.forEach(parameters -> {
            try {
                String resultsFolderPath = String.format(Locale.ENGLISH, "%d_%.2f", parameters.initialHumansQty, parameters.zombieDesiredVelocity);
                //Chequeamos si la carpeta con resultados existe, y en caso de que no, la creamos
                if(!Files.exists(Paths.get(resultsFolderPath))){
                    new File(resultsFolderPath).mkdir();
                }

                String path = String.format(Locale.ENGLISH, "out_%d_%.2f", parameters.initialHumansQty, parameters.zombieDesiredVelocity);
                Population simulation = new Population(parameters.initialHumansQty, parameters.zombieDesiredVelocity);
                simulation.createStaticFile(path, parameters.initialHumansQty, parameters.zombieDesiredVelocity);

                String dynamicsPath = path + "/dynamics";
                new File(resultsFolderPath + dynamicsPath).mkdir();
                simulation.createDynamicFile(resultsFolderPath + dynamicsPath);
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
