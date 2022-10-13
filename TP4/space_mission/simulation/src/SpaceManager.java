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

        // TESTING
        LocalDateTime departureDate = LocalDateTime.parse("2023-07-18T00:00:00");
        int seconds = (int) ChronoUnit.SECONDS.between(SpaceConstants.START_SIMULATION_DATE, departureDate);


        ArrayList<SpaceParameters> simulationParameters = new ArrayList<>();
        for(double i = 0; i < 60*60*24*1000 ; i += 60*60*24*10)
            simulationParameters.add(new SpaceParameters(300., 900., i, PlanetType.EARTH, PlanetType.VENUS));
//        simulationParameters.add(new SpaceParameters(300., 900., (double) 60*60*24*257));

        simulationParameters.forEach(parameters -> {
            try {
                String resultsFolderPath = RESULTS_PATH + "/" + parameters.origin.getPlanetName() + "_to_" + parameters.destiny.getPlanetName() + "/";
                //Chequeamos si la carpeta con resultados existe, y en caso de que no, la creamos
                if(!Files.exists(Paths.get(resultsFolderPath))){
                    new File(resultsFolderPath).mkdir();
                }
                String path = String.format(Locale.ENGLISH, "out_%.0f_%.0f", parameters.secondsToDeparture, parameters.simulationDeltaT);
                SpaceSimulation simulation = new SpaceSimulation(parameters.simulationDeltaT,parameters.outputDeltaT,parameters.secondsToDeparture, parameters.origin, parameters.destiny);
                new File(resultsFolderPath + path).mkdir();
                SpaceSimulation.createStaticFile(path, IntegrationAlgorithmImp.Type.BEEMAN.toString(),
                        resultsFolderPath, parameters.simulationDeltaT, parameters.secondsToDeparture,parameters.origin,parameters.destiny);

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

        public SpaceParameters(Double simulationDeltaT, Double outputDeltaT, Double secondsToDeparture, PlanetType origin, PlanetType destiny) {
            this.simulationDeltaT = simulationDeltaT;
            this.outputDeltaT = outputDeltaT;
            this.secondsToDeparture = secondsToDeparture;
            this.origin = origin;
            this.destiny = destiny;
        }
    }
}
