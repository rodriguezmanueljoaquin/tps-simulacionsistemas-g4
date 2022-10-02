import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class Manager {
    public static void main(String[] args) {
        String RESULTS_PATH = "oscilation/results/";

        new File(RESULTS_PATH).mkdir();

        ArrayList<OscillationParameters> simulationParameters = new ArrayList<>();
        double initialSimDeltaT = 0.01;
        for (double simDeltaT = initialSimDeltaT; Math.abs(simDeltaT-Math.pow(10,-7))>Constants.EPSILON ; simDeltaT /= 10)
            simulationParameters.add(new OscillationParameters(simDeltaT, Constants.OUTPUT_DELTA_T,
                    IntegrationAlgorithm.Type.BEEMAN));

        simulationParameters.forEach(parameters -> {
            Simulation simulation = null;
            try {
                String path = String.format(Locale.ENGLISH, "out_%s_%f", parameters.algorithmType.toString(), parameters.simulationDeltaT);
                new File(RESULTS_PATH + path).mkdir();
                Simulation.createStaticFile(path, parameters.algorithmType.toString(), RESULTS_PATH, parameters.simulationDeltaT);

                Random random = new Random(Constants.RANDOM_SEED);
                String dynamicsPath = path + "/dynamics";
                new File(RESULTS_PATH + dynamicsPath).mkdir();

                simulation = getSimulationFromIntegrationAlgorithmType(parameters.algorithmType,parameters.simulationDeltaT,parameters.outputDeltaT);
                simulation.createDynamicFile(dynamicsPath, RESULTS_PATH);
            } catch (FileNotFoundException | UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }

        });

    }

    private static Simulation getSimulationFromIntegrationAlgorithmType(IntegrationAlgorithm.Type type, double simulationDeltaT, double outputDeltaT){
        switch (type){
            case BEEMAN:
                return new BeemanSimulation(simulationDeltaT, outputDeltaT);

            default:
                return new GearSimulation(simulationDeltaT,outputDeltaT);

        }
    }

    private static class OscillationParameters {
        public double simulationDeltaT;
        public double outputDeltaT;
        public IntegrationAlgorithm.Type algorithmType;

        public OscillationParameters(Double simulationDeltaT, Double outputDeltaT, IntegrationAlgorithm.Type algorithmType) {
            this.simulationDeltaT = simulationDeltaT;
            this.outputDeltaT = outputDeltaT;
            this.algorithmType = algorithmType;
        }
    }
}
