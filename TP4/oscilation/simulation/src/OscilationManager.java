import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class OscilationManager {
    public static void main(String[] args) {
        String RESULTS_PATH = "oscilation/results/";

        new File(RESULTS_PATH).mkdir();

        ArrayList<OscillationParameters> simulationParameters = new ArrayList<>();
        double initialSimDeltaT = 0.01;
        for (double simDeltaT = initialSimDeltaT; Math.abs(simDeltaT-Math.pow(10,-7))> OscilationConstants.EPSILON ; simDeltaT /= 10)
            simulationParameters.add(new OscillationParameters(simDeltaT, OscilationConstants.OUTPUT_DELTA_T,
                    IntegrationAlgorithmImp.Type.BEEMAN));
        for (double simDeltaT = initialSimDeltaT; Math.abs(simDeltaT-Math.pow(10,-7))> OscilationConstants.EPSILON ; simDeltaT /= 10)
            simulationParameters.add(new OscillationParameters(simDeltaT, OscilationConstants.OUTPUT_DELTA_T,
                    IntegrationAlgorithmImp.Type.VERLET));
        for (double simDeltaT = initialSimDeltaT; Math.abs(simDeltaT-Math.pow(10,-7))> OscilationConstants.EPSILON ; simDeltaT /= 10)
            simulationParameters.add(new OscillationParameters(simDeltaT, OscilationConstants.OUTPUT_DELTA_T,
                    IntegrationAlgorithmImp.Type.GEAR));

        simulationParameters.forEach(parameters -> {
            OscilationSimulation simulation = null;
            try {
                String path = String.format(Locale.ENGLISH, "out_%s_%f", parameters.algorithmType.toString(), parameters.simulationDeltaT);
                new File(RESULTS_PATH + path).mkdir();
                OscilationSimulation.createStaticFile(path, parameters.algorithmType.toString(), RESULTS_PATH, parameters.simulationDeltaT);

                String dynamicsPath = path + "/dynamics";
                new File(RESULTS_PATH + dynamicsPath).mkdir();

                simulation =new OscilationSimulation(parameters.simulationDeltaT,parameters.outputDeltaT,parameters.algorithmType);

                simulation.createDynamicFile(dynamicsPath, RESULTS_PATH);
            } catch (FileNotFoundException | UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }

        });
    }

    private static class OscillationParameters {
        public double simulationDeltaT;
        public double outputDeltaT;
        public IntegrationAlgorithmImp.Type algorithmType;

        public OscillationParameters(Double simulationDeltaT, Double outputDeltaT, IntegrationAlgorithmImp.Type algorithmType) {
            this.simulationDeltaT = simulationDeltaT;
            this.outputDeltaT = outputDeltaT;
            this.algorithmType = algorithmType;
        }
    }
}
