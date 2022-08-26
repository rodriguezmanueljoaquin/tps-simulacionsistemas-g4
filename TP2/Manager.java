import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Map;

public class Manager {
    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
        new File("results").mkdir();

        ArrayList<SimulationParameters> simulationParameters = new ArrayList<>();
        simulationParameters.add(new SimulationParameters(500, 2., 10.)); // high density and noise
        simulationParameters.add(new SimulationParameters(125, 0.1, 10.)); // low density and noise
        simulationParameters.add(new SimulationParameters(500, 0.1, 10.)); // high density and low noise
        simulationParameters.add(new SimulationParameters(125, 2., 10.)); // low density and high noise

        simulationParameters.forEach(parameters ->{
            Population population = new Population(parameters.particlesQty, parameters.eta, parameters.boxLength);
            try {
                population.runSimulation(String.format("out_%d_%.2f_%.2f", parameters.particlesQty, parameters.eta, parameters.boxLength));
            } catch (FileNotFoundException | UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }

        });

    }

    private static class SimulationParameters {
        public Integer particlesQty;
        public Double eta, boxLength;
        public SimulationParameters(Integer particlesQty, Double eta, Double boxLength) {
            this.particlesQty = particlesQty;
            this.eta = eta;
            this.boxLength = boxLength;
        }
    }
}
