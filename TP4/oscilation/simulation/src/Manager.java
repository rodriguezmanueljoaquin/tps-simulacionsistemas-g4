import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class Manager {
    public static void main(String[] args) {
        // FIXME

        new File("results").mkdir();

        ArrayList<OscillationParameters> simulationParameters = new ArrayList<>();
        double initialSimDeltaT = 0.1;
        for (double simDeltaT = initialSimDeltaT; simDeltaT > Math.pow(10,-6) ; simDeltaT /= 10)
            simulationParameters.add(new OscillationParameters(simDeltaT, initialSimDeltaT,
                    IntegrationAlgorithm.Type.BEEMAN));

        simulationParameters.forEach(parameters -> {
            Simulation simulation = null;
            try {
                String path = String.format(Locale.ENGLISH, "out_%s_%.6f", parameters.algorithmType.toString(), parameters.simulationDeltaT);
                new File("results/" + path).mkdir();
                Simulation.createStaticFile(path, parameters.algorithmType.toString());

                Random random = new Random(Constants.RANDOM_SEED);
                String dynamicsPath = path + "/dynamics";
                new File("results/" + dynamicsPath).mkdir();

//                FIXME
                simulation = new BeemanSimulation(parameters.simulationDeltaT, parameters.outputDeltaT);
                System.out.println(parameters.outputDeltaT);
                simulation.createDynamicFile(dynamicsPath);
            } catch (FileNotFoundException | UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }

        });

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