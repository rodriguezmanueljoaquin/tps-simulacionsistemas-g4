import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class SpaceManager {
    public static void main(String[] args) {
        String RESULTS_PATH = "space_mission/results/";

        new File(RESULTS_PATH).mkdir();

        ArrayList<SpaceParameters> simulationParameters = new ArrayList<>();
        double initialSimDeltaT = 0.01;
    /*    for (double simDeltaT = initialSimDeltaT; Math.abs(simDeltaT-Math.pow(10,-7))> SpaceConstants.EPSILON ; simDeltaT /= 10)
            simulationParameters.add(new SpaceParameters(simDeltaT, SpaceConstants.OUTPUT_DELTA_T,
                    IntegrationAlgorithmImp.Type.BEEMAN));
        for (double simDeltaT = initialSimDeltaT; Math.abs(simDeltaT-Math.pow(10,-7))> SpaceConstants.EPSILON ; simDeltaT /= 10)
            simulationParameters.add(new SpaceParameters(simDeltaT, SpaceConstants.OUTPUT_DELTA_T,
                    IntegrationAlgorithmImp.Type.VERLET));*/
//        for (double simDeltaT = initialSimDeltaT; Math.abs(simDeltaT-Math.pow(10,-7))> SpaceConstants.EPSILON ; simDeltaT /= 10)
//            simulationParameters.add(new SpaceParameters(simDeltaT, SpaceConstants.OUTPUT_DELTA_T,
//                    IntegrationAlgorithmImp.Type.GEAR));

//        simulationParameters.add(new SpaceParameters(800., 800.,
//                IntegrationAlgorithmImp.Type.BEEMAN));
        simulationParameters.add(new SpaceParameters(800., 800.,
                0.0));

        simulationParameters.forEach(parameters -> {

            try {
//                String path = String.format(Locale.ENGLISH, "out_%s_%f", parameters.algorithmType.toString(), parameters.simulationDeltaT);
                String path = String.format(Locale.ENGLISH, "out_%f_%f", parameters.secondsToDeparture, parameters.simulationDeltaT);
                SpaceSimulation simulation = new SpaceSimulation(parameters.simulationDeltaT,parameters.outputDeltaT,parameters.secondsToDeparture);
                new File(RESULTS_PATH + path).mkdir();
                SpaceSimulation.createStaticFile(path, IntegrationAlgorithmImp.Type.BEEMAN.toString(), RESULTS_PATH, parameters.simulationDeltaT);

                String dynamicsPath = path + "/dynamics";
                new File(RESULTS_PATH + dynamicsPath).mkdir();



                simulation.createDynamicFile(dynamicsPath, RESULTS_PATH);
            } catch (FileNotFoundException | UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static class SpaceParameters {
        public double simulationDeltaT;
        public double outputDeltaT;
//        public IntegrationAlgorithmImp.Type algorithmType;
        public double secondsToDeparture;

//        public SpaceParameters(Double simulationDeltaT, Double outputDeltaT, IntegrationAlgorithmImp.Type algorithmType) {
//            this.simulationDeltaT = simulationDeltaT;
//            this.outputDeltaT = outputDeltaT;
//            this.algorithmType = algorithmType;
//        }
        public SpaceParameters(Double simulationDeltaT, Double outputDeltaT, Double secondsToDeparture) {
            this.simulationDeltaT = simulationDeltaT;
            this.outputDeltaT = outputDeltaT;
            this.secondsToDeparture = secondsToDeparture;
        }
    }
}
