import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class SpaceManager {
    public static void main(String[] args) {
        String RESULTS_PATH = "space_mission/results/";

        new File(RESULTS_PATH).mkdir();

        // TESTING
        LocalDateTime departureDate = LocalDateTime.parse("2023-07-18T00:00:00");
        int seconds = (int) ChronoUnit.SECONDS.between(SpaceConstants.START_SIMULATION_DATE, departureDate);

        ArrayList<SpaceParameters> simulationParameters = new ArrayList<>();
        for(double i = 0; i < 60*60*24*1000 ; i += 60*60*24*10)
            simulationParameters.add(new SpaceParameters(300., 900., i));
//        simulationParameters.add(new SpaceParameters(300., 900., (double) 60*60*24*257));

        simulationParameters.forEach(parameters -> {
            try {
                String path = String.format(Locale.ENGLISH, "out_%.0f_%.0f", parameters.secondsToDeparture, parameters.simulationDeltaT);
                SpaceSimulation simulation = new SpaceSimulation(parameters.simulationDeltaT,parameters.outputDeltaT,parameters.secondsToDeparture);
                new File(RESULTS_PATH + path).mkdir();
                SpaceSimulation.createStaticFile(path, IntegrationAlgorithmImp.Type.BEEMAN.toString(),
                        RESULTS_PATH, parameters.simulationDeltaT, parameters.secondsToDeparture);

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
        public double secondsToDeparture;

        public SpaceParameters(Double simulationDeltaT, Double outputDeltaT, Double secondsToDeparture) {
            this.simulationDeltaT = simulationDeltaT;
            this.outputDeltaT = outputDeltaT;
            this.secondsToDeparture = secondsToDeparture;
        }
    }
}
