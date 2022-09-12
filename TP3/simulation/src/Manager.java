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

        // variando densidad
        simulationParameters.add(new SimulationParameters(20, 0.24, 0.09, 0.01));
        simulationParameters.forEach(parameters -> {
            Population population = null;
            try {
                String path = String.format(Locale.ENGLISH, "out_%d_0-0%.0f", parameters.particlesQty, parameters.gap*100);
                new File("results/" + path).mkdir();
                Population.createStaticFile(path, parameters.particlesQty, parameters.width, parameters.height, parameters.gap);

                Random random = new Random(Constants.RANDOM_SEED);
                String dynamicsPath = path + "/dynamics";
                new File("results/" + dynamicsPath).mkdir();

                for (int i = 0 ; i < Constants.SIMULATION_REPETITION_TIMES ; i++){
                    population = new Population(parameters.particlesQty, parameters.width, parameters.height, parameters.gap, random.nextLong());
                    population.createDynamicFile(dynamicsPath, String.valueOf(i+1));
                }
            } catch (FileNotFoundException | UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }

        });
    }

    private static class SimulationParameters {
        public Integer particlesQty;
        public Double width, height, gap;

        public SimulationParameters(Integer particlesQty, Double width, Double height, Double gap) {
            this.particlesQty = particlesQty;
            this.width = width;
            this.height = height;
            this.gap = gap;
        }
    }
}
