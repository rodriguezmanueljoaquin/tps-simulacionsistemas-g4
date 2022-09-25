import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.Random;

public class Simulation {
    private Particle p;
    private Double simulationDeltaT;
    private IntegrationAlgorithm algorithm;
    private Double currentSimulationTime;
    private Double prevPos;

    public Simulation(IntegrationAlgorithm algorithm, Double simulationDeltaT) {
        this.p = new Particle(
                        Constants.INITIAL_X,
                        0.,
                        0.,
                        1, // TODO: SE DEBERIA CALCULAR SEGUN FUNCION DE PPT
                        0,
                        Constants.PARTICLE_MASS);
        this.algorithm = algorithm;
        this.simulationDeltaT = simulationDeltaT;
        this.currentSimulationTime = 0.;
        this.prevPos = 0.; // TODO: SE DEBERIA CALCULAR CON EULER SOBRE -DeltaT ?
    }

    public void nextIteration(){
        Pair<Double, Double> newPositions;
        Pair<Double, Double> newVelocities;
        for(Double iterationTime = this.currentSimulationTime;
            iterationTime <= this.currentSimulationTime + Constants.DELTA_T_OUTPUT_FILE;
            iterationTime += this.simulationDeltaT){
            newPositions = algorithm.getPosition(iterationTime, this.simulationDeltaT, p.getX(), this.prevPos);
            newVelocities = algorithm.getVelocity(iterationTime, this.simulationDeltaT);
            this.prevPos = p.getX();
            this.p.setX(newPositions.getLeft());
            this.p.setxVelocity(newVelocities.getLeft());
        }
    }

    public static void createStaticFile(String outputName, String algorithmName) throws FileNotFoundException, UnsupportedEncodingException {
        System.out.println("\tCreating static file. . .");

        PrintWriter writer = new PrintWriter("./results/" + outputName + "/static.txt", "UTF-8");
        writer.println(String.format(Locale.ENGLISH, "%s\n%f\n%f\n%f\n", algorithmName, Constants.PARTICLE_MASS,Constants.K, Constants.GAMMA));
        writer.close();

        System.out.println("\tStatic file successfully created");
    }

    public void createDynamicFile(String outputName) throws FileNotFoundException, UnsupportedEncodingException {
        System.out.println("\tCreating dynamic file. . .");

        PrintWriter writer = new PrintWriter("./results/" + outputName + "/dynamic" + ".txt", "UTF-8");

        for (int i = 0; i <= Constants.FINAL_TIME; i+=Constants.DELTA_T_OUTPUT_FILE) {
            writer.println(this.currentSimulationTime);
            writer.println(String.format(Locale.ENGLISH, "%f;%f;%f;%f",
                    p.getX(), p.getY(), p.getxVelocity(), p.getyVelocity()));
            nextIteration();
        }
        writer.close();

        System.out.println("\tDynamic file successfully created");
    }
}
