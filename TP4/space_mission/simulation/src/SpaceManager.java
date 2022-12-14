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

        //PARAMETERS
        double simulationsQty = 100;

        double lastDay = 1000;

        double initialVelocityUmbral = 0.25;

        // TESTING FOR BEST DEPARTURE DATE
        double hitDestinyDepartureSeconds = 1.96308E7;
        //2.01663E7
//        simulationParameters.add(new SpaceParameters(300., 900.,
//                hitDestinyDepartureSeconds, 8., PlanetType.EARTH, PlanetType.VENUS));

        // TESTING FOR DIFFERENT DEPARTURE DATES
        double secondsInOneDay = 60*60*24;
        double bestDepartureDay = 228;
        double bestDepartureTimeInBestDayInSeconds = 32215.;

//        for(double dayInSeconds = 0; dayInSeconds < lastDay*secondsInOneDay; dayInSeconds += secondsInOneDay*4)
//            simulationParameters.add(new SpaceParameters(300., 900., dayInSeconds,
//                    SpaceConstants.VELOCITY_LAUNCH, PlanetType.EARTH, PlanetType.VENUS));

        // TESTING FOR DIFFERENT RETURNING DATES
        for(double dayInSeconds = 0; dayInSeconds < lastDay*secondsInOneDay; dayInSeconds += secondsInOneDay*4)
            simulationParameters.add(new SpaceParameters(300., 900., dayInSeconds,
                    SpaceConstants.VELOCITY_LAUNCH, PlanetType.VENUS, PlanetType.EARTH));

//        double daysOfTrip = 65;
//        for(double dayInSeconds = (bestDepartureDay+ daysOfTrip +365 +180)*secondsInOneDay;
//            dayInSeconds < (bestDepartureDay + lastDay)*secondsInOneDay;
//            dayInSeconds += secondsInOneDay*1)
//            simulationParameters.add(new SpaceParameters(300., 900., dayInSeconds,
//                    SpaceConstants.VELOCITY_LAUNCH, PlanetType.VENUS, PlanetType.EARTH));

            //dt=15 minutes in 4 days which is the phase of the one before
//        for(double daySeconds = (bestDepartureDay-2)*secondsInOneDay; daySeconds < secondsInOneDay*(bestDepartureDay+2); daySeconds += secondsInOneDay/48)
//            simulationParameters.add(new SpaceParameters(300., 900., daySeconds, SpaceConstants.VELOCITY_LAUNCH,
//                    PlanetType.EARTH, PlanetType.VENUS));

        // TESTING FOR DIFFERENT INITIAL VELOCITIES
//        for(double v0 = SpaceConstants.VELOCITY_LAUNCH - initialVelocityUmbral;
//            v0 < SpaceConstants.VELOCITY_LAUNCH + initialVelocityUmbral;
//            v0 += initialVelocityUmbral*2/simulationsQty){
//            simulationParameters.add(new SpaceParameters(300., 900.,
//                    bestDepartureDay*secondsInOneDay + bestDepartureTimeInBestDayInSeconds, v0, PlanetType.EARTH, PlanetType.VENUS));
//        }

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
