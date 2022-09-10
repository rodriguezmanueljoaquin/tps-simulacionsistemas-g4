import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

public class Manager {
    public static void main(String[] args) {
        new File("results").mkdir();

        ArrayList<SimulationParameters> simulationParameters = new ArrayList<>();

        // variando densidad
        simulationParameters.add(new SimulationParameters(20, 0.24, 0.09, 0.01));

        simulationParameters.forEach(parameters ->{
            Population population = new Population(parameters.particlesQty, parameters.width,parameters.height,parameters.gap);

            try {
                for (int i = 1 ; i <= 25 ; i++)
                    population.runSimulation(String.format(Locale.ENGLISH,"out_%d_%d_%f", i, parameters.particlesQty, parameters.gap));
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
