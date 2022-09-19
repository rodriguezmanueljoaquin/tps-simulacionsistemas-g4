import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.util.*;

public class Manager {
    public static void main(String[] args) {
        new File("results").mkdir();

        ArrayList<SimulationParameters> simulationParameters = new ArrayList<>();

        // variando cantidad de particulas
//        for (int i = 25; i <= 200 ; i+=25)
//            simulationParameters.add(new SimulationParameters(i, 0.01, Constants.PARTICLE_VELOCITY));

//        // variando gap
//        Integer [] particlesQtyArray = new Integer[]{100,150,200};
//        for (Integer particlesQty : particlesQtyArray) {
//            for (double i = 0.01; i <= 0.1; i += 0.02)
//                simulationParameters.add(new SimulationParameters(particlesQty, i, Constants.PARTICLE_VELOCITY));
//        }

        //variando velocidad
        Double [] velocities = new Double[]{0.01,0.02,0.04};
        for (Double velocity : velocities){
            simulationParameters.add(new SimulationParameters(100,0.01,velocity));
        }

        simulationParameters.forEach(parameters -> {
            Population population = null;
            try {
                String path = String.format(Locale.ENGLISH, "out_%d_0-0%.0f_0-0%.0f", parameters.particlesQty, parameters.gap*100,parameters.velocity*100);
                new File("results/" + path).mkdir();
                Population.createStaticFile(path, parameters.particlesQty, parameters.gap, parameters.velocity);

                Random random = new Random(Constants.RANDOM_SEED);
                String dynamicsPath = path + "/dynamics";
                new File("results/" + dynamicsPath).mkdir();

                for (int i = 0 ; i < Constants.SIMULATION_REPETITION_TIMES ; i++){
                    population = new Population(parameters.particlesQty, parameters.gap, random.nextLong(),parameters.velocity);
                    population.createDynamicFile(dynamicsPath, String.valueOf(i+1));
                }
            } catch (FileNotFoundException | UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }

        });
    }

    private static class SimulationParameters {
        public Integer particlesQty;
        public Double  gap;
        public Double velocity;

        public SimulationParameters(Integer particlesQty,  Double gap, Double velocity) {
            this.particlesQty = particlesQty;
            this.gap = gap;
            this.velocity = velocity;
        }

        public SimulationParameters(Integer particlesQty,  Double gap) {
            this.particlesQty = particlesQty;
            this.gap = gap;
            this.velocity = Constants.PARTICLE_VELOCITY;
        }
    }
}
