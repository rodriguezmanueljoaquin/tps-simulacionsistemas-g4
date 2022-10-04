import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

public class OscilationSimulation {
    private Particle p;
    private double simulationDeltaT;
    private double outputDeltaT;
    private double currentSimulationTime;

    private IntegrationAlgorithmImp integrationAlgorithmImp;

    public OscilationSimulation(Double simulationDeltaT, Double outputDeltaT, IntegrationAlgorithmImp.Type type) {
        this.p = new Particle(
                        OscilationConstants.INITIAL_X,
                        0.,
                -OscilationConstants.A * Constants.GAMMA/(2* OscilationConstants.PARTICLE_MASS),
                        0,
                        0,
                        OscilationConstants.PARTICLE_MASS);
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
