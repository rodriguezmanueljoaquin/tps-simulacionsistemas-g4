import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class SpaceManager {
    public static void main(String[] args) {
        String RESULTS_PATH = "space_mission/results/";

        new File(RESULTS_PATH).mkdir();
        ArrayList<SpaceParameters> simulationParameters = new ArrayList<>();

        // TESTING FOR SPECIFIC DATE
        LocalDateTime departureDate = LocalDateTime.parse("2023-07-18T00:00:00");
        double seconds = (int) ChronoUnit.SECONDS.between(SpaceConstants.START_SIMULATION_DATE, departureDate);
//        simulationParameters.add(new SpaceParameters(300., 900., seconds));

        //PARAMETERS
        double simulationsQty = 300;

        double lastDay = 1000;

        double initialVelocityUmbral = 1.5;
        double dateToDepartureInSeconds = 19872000.;

        // TESTING FOR DIFFERENT DEPARTURE DATES
//        double secondsInOneDay = 60*60*24;
//        for(double i = 0; i < secondsInOneDay*lastDay ; i += Math.floor(secondsInOneDay*lastDay/simulationsQty))
//            simulationParameters.add(new SpaceParameters(300., 900., i, SpaceConstants.VELOCITY_LAUNCH, PlanetType.EARTH, PlanetType.VENUS));

        // TESTING FOR DIFFERENT INITIAL VELOCITIES
        for(double v0 = SpaceConstants.VELOCITY_LAUNCH - initialVelocityUmbral;
            v0 < SpaceConstants.VELOCITY_LAUNCH + initialVelocityUmbral ;
            v0 += initialVelocityUmbral*2/simulationsQty){
            simulationParameters.add(new SpaceParameters(300., 900., dateToDepartureInSeconds, v0, PlanetType.EARTH, PlanetType.VENUS));
        }

        simulationParameters.forEach(parameters -> {
            try {
                String resultsFolderPath = RESULTS_PATH + "/" + parameters.origin.getPlanetName() + "_to_" + parameters.destiny.getPlanetName() + "/";
                //Chequeamos si la carpeta con resultados existe, y en caso de que no, la creamos
                if(!Files.exists(Paths.get(resultsFolderPath))){
                    new File(resultsFolderPath).mkdir();
                }
                String path = String.format(Locale.ENGLISH, "out_%.0f_%.0f_%.3f", parameters.secondsToDeparture, parameters.simulationDeltaT, parameters.initialVelocityModule, parameters.secondsToDeparture, parameters.simulationDeltaT);
                SpaceSimulation simulation = new SpaceSimulation(parameters.simulationDeltaT,parameters.outputDeltaT,parameters.secondsToDeparture, parameters.initialVelocityModule, parameters.origin, parameters.destiny);
                new File(resultsFolderPath + path).mkdir();
                SpaceSimulation.createStaticFile(path, IntegrationAlgorithmImp.Type.BEEMAN.toString(),
                        resultsFolderPath, parameters.simulationDeltaT, parameters.secondsToDeparture,
                        parameters.initialVelocityModule, parameters.origin, parameters.destiny);

                String dynamicsPath = path + "/dynamics";
                new File(resultsFolderPath + dynamicsPath).mkdir();

                simulation.createDynamicFile(dynamicsPath, resultsFolderPath);
            } catch (FileNotFoundException | UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static class SpaceParameters {
        public double simulationDeltaT;
        public double outputDeltaT;
        public double secondsToDeparture;
        public PlanetType origin;
        public PlanetType destiny;
        public double initialVelocityModule;

        public SpaceParameters(Double simulationDeltaT, Double outputDeltaT, Double secondsToDeparture, Double initialVelocityModule, PlanetType origin, PlanetType destiny) {
            this.simulationDeltaT = simulationDeltaT;
            this.outputDeltaT = outputDeltaT;
            this.secondsToDeparture = secondsToDeparture;
            this.origin = origin;
            this.destiny = destiny;
            this.initialVelocityModule = initialVelocityModule;
        }
    }
}
