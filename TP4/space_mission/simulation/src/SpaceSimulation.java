import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SpaceSimulation {
    private Particle p;
    private double simulationDeltaT;
    private double outputDeltaT;
    private double currentSimulationTime;

    private IntegrationAlgorithmImp integrationAlgorithmImp;

    private Particle sun;
    private List<Particle> objects;

    public SpaceSimulation(Double simulationDeltaT, Double outputDeltaT, IntegrationAlgorithm.Type type) {
        this.sun = new Particle(
                        0,
                        0.,
                        0.,
                        0,
                        0,
                        4.260 * Math.pow(10,9));

        List<String> planets = new ArrayList<>();
        planets.add("earth");
        planets.add("venus");
        for (String planet: planets) {
            Pair<Double, Double> position = HorizonResultsReader.GetPosition("space_mission/datasets/horizons_results_" + planet + ".txt");
            Pair<Double, Double> velocity = HorizonResultsReader.GetVelocity("space_mission/datasets/horizons_results_" + planet + ".txt");
            objects.add(
              new Particle(
                      position.getLeft(),
                      position.getRight(),
                      velocity.getLeft(),
                      velocity.getRight(),
                      0,
                      0)
            );
        }

        System.out.println(planets);

        this.simulationDeltaT = simulationDeltaT;
        this.outputDeltaT = outputDeltaT;
        this.currentSimulationTime = 0.;

        switch (type){
            case BEEMAN:
                integrationAlgorithmImp = new BeemanAlgorithm(simulationDeltaT, outputDeltaT, p);
                break;
            case VERLET:
                integrationAlgorithmImp =  new VerletAlgorithm(simulationDeltaT,outputDeltaT, p);
                break;
            default:
                integrationAlgorithmImp =  new GearAlgorithm(simulationDeltaT,outputDeltaT, p);

        }
    }

    public void nextIteration() {
        double newPosition, newVelocity;
        double iterationTime = this.currentSimulationTime;
        for (;
             iterationTime <= this.currentSimulationTime + this.outputDeltaT  && iterationTime <= OscilationConstants.FINAL_TIME;
             iterationTime += this.simulationDeltaT) {
            newPosition = integrationAlgorithmImp.getNewPosition();
            newVelocity = integrationAlgorithmImp.getNewVelocity();
            this.p.setX(newPosition);
            this.p.setxVelocity(newVelocity);
        }

        currentSimulationTime = iterationTime;
    }

    public static void createStaticFile(String outputName, String algorithmName, String outputPath, double simulationDeltaT) throws FileNotFoundException, UnsupportedEncodingException {
        System.out.println("\tCreating static file. . .");

        PrintWriter writer = new PrintWriter(outputPath + outputName + "/static.txt", "UTF-8");
        writer.println(String.format(Locale.ENGLISH, "%s\n%f\n%f\n%f\n%d\n%f", algorithmName, OscilationConstants.PARTICLE_MASS, Constants.K, Constants.GAMMA, OscilationConstants.A, simulationDeltaT));
        writer.close();

        System.out.println("\tStatic file successfully created");
    }

    public void createDynamicFile(String outputName, String outputPath) throws FileNotFoundException, UnsupportedEncodingException {
        System.out.println("\tCreating dynamic file. . .");
        PrintWriter writer = new PrintWriter(outputPath + outputName + "/dynamic" + ".txt", "UTF-8");

        for (double i = 0; i <= OscilationConstants.FINAL_TIME; i += this.outputDeltaT) {
            writer.write(this.currentSimulationTime +"\n"+ p.getX() + ";" + p.getxVelocity() + "\n");
            nextIteration();
        }
        writer.close();

        System.out.println("\tDynamic file successfully created");
    }
}
