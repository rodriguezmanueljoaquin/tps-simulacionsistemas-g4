import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

public abstract class Simulation {
    protected Particle p;
    protected double simulationDeltaT;
    private double outputDeltaT;
    protected double currentSimulationTime;

    public Simulation(Double simulationDeltaT, Double outputDeltaT) {
        this.p = new Particle(
                        Constants.INITIAL_X,
                        0.,
                        0.,
                        -Constants.A * Constants.GAMMA/(2*Constants.PARTICLE_MASS), // TODO: SE DEBERIA CALCULAR SEGUN FUNCION DE PPT
                        0,
                        Constants.PARTICLE_MASS);
        this.simulationDeltaT = simulationDeltaT;
        this.outputDeltaT = outputDeltaT;
        this.currentSimulationTime = 0.;
    }

    protected abstract double getNewPosition();

    protected abstract double getNewVelocity();

    protected double getForce(double position, double velocity){
        return (-Constants.K * position - Constants.GAMMA * velocity);
    }

    public void nextIteration(){
        double newPosition, newVelocity;

        for(double iterationTime = this.currentSimulationTime;
            iterationTime <= this.currentSimulationTime + this.outputDeltaT && iterationTime <= Constants.FINAL_TIME;
            iterationTime += this.simulationDeltaT){
            newPosition = getNewPosition();
            newVelocity = getNewVelocity();
            this.p.setX(newPosition);
            this.p.setxVelocity(newVelocity);
        }

        currentSimulationTime += this.outputDeltaT;
    }

    public static void createStaticFile(String outputName, String algorithmName, String outputPath) throws FileNotFoundException, UnsupportedEncodingException {
        System.out.println("\tCreating static file. . .");

        PrintWriter writer = new PrintWriter(outputPath + outputName + "/static.txt", "UTF-8");
        writer.println(String.format(Locale.ENGLISH, "%s\n%f\n%f\n%f\n", algorithmName, Constants.PARTICLE_MASS,Constants.K, Constants.GAMMA));
        writer.close();

        System.out.println("\tStatic file successfully created");
    }

    public void createDynamicFile(String outputName, String outputPath) throws FileNotFoundException, UnsupportedEncodingException {
        System.out.println("\tCreating dynamic file. . .");
        PrintWriter writer = new PrintWriter(outputPath + outputName + "/dynamic" + ".txt", "UTF-8");

        for (double i = 0; i <= Constants.FINAL_TIME; i += this.outputDeltaT) {
            writer.println(this.currentSimulationTime);
            writer.println(String.format(Locale.ENGLISH, "%f;%f",
                    p.getX(), p.getxVelocity()));
            nextIteration();
        }
        writer.close();

        System.out.println("\tDynamic file successfully created");
    }
}
