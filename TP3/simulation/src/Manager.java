import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class Manager {
    public static void main(String[] args) {
        new File("results").mkdir();

        ArrayList<SimulationParameters> simulationParameters = new ArrayList<>();

        // variando cantidad de particulas
        //for (int i = 25; i <= 200 ; i+=25)
          //  simulationParameters.add(new SimulationParameters(i, 0.01));
        simulationParameters.add(new SimulationParameters(150, 0.01));

        simulationParameters.forEach(parameters -> {
            Population population = null;
            try {
                String path = String.format(Locale.ENGLISH, "out_%d_0-0%.0f", parameters.particlesQty, parameters.gap*100);
                new File("results/" + path).mkdir();
                Population.createStaticFile(path, parameters.particlesQty, parameters.gap);

                Random random = new Random(Constants.RANDOM_SEED);
                String dynamicsPath = path + "/dynamics";
                new File("results/" + dynamicsPath).mkdir();

                for (int i = 0 ; i < Constants.SIMULATION_REPETITION_TIMES ; i++){
                    population = new Population(parameters.particlesQty, parameters.gap, random.nextLong());
                    population.createDynamicFile(dynamicsPath, String.valueOf(i+1));
                }
            } catch (FileNotFoundException | UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }

        });
    }

    private static class SimulationParameters {
        public Integer particlesQty;
        public Double  gap;

        public SimulationParameters(Integer particlesQty,  Double gap) {
            this.particlesQty = particlesQty;
            this.gap = gap;
        }
    }
}
