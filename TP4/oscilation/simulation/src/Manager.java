import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class Manager {
    public static void main(String[] args) {
        new File("results").mkdir();

        ArrayList<OscillationParameters> simulationParameters = new ArrayList<>();
        for (double i = 0.25; i <= 1; i += 0.25)
            simulationParameters.add(new OscillationParameters(i*Constants.DELTA_T_OUTPUT_FILE, new VerletAlgorithm()));

        simulationParameters.forEach(parameters -> {
            Simulation simulation = null;
            try {
                String path = String.format(Locale.ENGLISH, "out_%s_%.2f", parameters.algorithm.getName(), parameters.simulationDeltaT);
                new File("results/" + path).mkdir();
                Simulation.createStaticFile(path, parameters.algorithm.getName());

                Random random = new Random(Constants.RANDOM_SEED);
                String dynamicsPath = path + "/dynamics";
                new File("results/" + dynamicsPath).mkdir();

                simulation = new Simulation(parameters.algorithm, parameters.simulationDeltaT);
                simulation.createDynamicFile(dynamicsPath);
            } catch (FileNotFoundException | UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }

        });

    }


    private static class OscillationParameters {
        public Double simulationDeltaT;
        public IntegrationAlgorithm algorithm;

        public OscillationParameters(Double simulationDeltaT, IntegrationAlgorithm algorithm) {
            this.simulationDeltaT = simulationDeltaT;
            this.algorithm = algorithm;
        }
    }
}
