import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

public class Manager {
    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
        new File("results").mkdir();

        ArrayList<SimulationParameters> simulationParameters = new ArrayList<>();

        // variando densidad
      /*  for (double i = 10 ; i <= 25 ; i+=5)
            simulationParameters.add(new SimulationParameters(2500, 1., i));

       */

        simulationParameters.forEach(parameters ->{
            Population population = new Population(parameters.particlesQty, parameters.width,parameters.height,parameters.gap);

//            try {
//                population.runSimulation(String.format(Locale.ENGLISH,"out_%d_%.2f_%.2f", parameters.particlesQty, parameters.eta, parameters.boxLength));
//            } catch (FileNotFoundException | UnsupportedEncodingException e) {
//                throw new RuntimeException(e);
//            }
//
        });



    }

    private static class SimulationParameters {
        public Integer particlesQty;
        public Double width, height, gap;

        public SimulationParameters(Integer particlesQty, Double width, Double height, Double gap) {
            this.particlesQty = particlesQty;
            this.width = width;
            this.height = height;
            this.gap = gap;
        }
    }
}
