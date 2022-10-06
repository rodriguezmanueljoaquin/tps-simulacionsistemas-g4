import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;

public class SpaceSimulation {
    private double simulationDeltaT;
    private double outputDeltaT;
    private double currentSimulationTime;

    private IntegrationAlgorithmImp integrationAlgorithmImp;

    private Particle sun;
    private Map<String ,Particle> objects = new HashMap<>();


    public SpaceSimulation(Double simulationDeltaT, Double outputDeltaT, IntegrationAlgorithmImp.Type type) {
        this.simulationDeltaT = simulationDeltaT;
        this.outputDeltaT = outputDeltaT;
        this.currentSimulationTime = 0.;

        this.sun = new Particle(
                        0,
                        0.,
                        0.,
                        0,
                        SpaceConstants.SUN_RADIUS,
                        SpaceConstants.SUN_MASS);

        List<String> planetNames = new ArrayList<>();
        planetNames.add("earth");
        planetNames.add("venus");
        for (String planetName: planetNames) {
            Pair<Double, Double> position = HorizonResultsReader.getPosition("space_mission/datasets/horizons_results_" + planetName + ".txt");
            Pair<Double, Double> velocity = HorizonResultsReader.getVelocity("space_mission/datasets/horizons_results_" + planetName + ".txt");
            objects.put(planetName,
              new Particle(
                      position.getLeft(),
                      position.getRight(),
                      velocity.getLeft(),
                      velocity.getRight(),
                      planetName.equals(planetNames.get(0))? SpaceConstants.EARTH_RADIUS : SpaceConstants.VENUS_RADIUS,
                      planetName.equals(planetNames.get(0))? SpaceConstants.EARTH_MASS : SpaceConstants.VENUS_MASS)
            );
        }
        double vAbsEarth = Math.sqrt(Math.pow(objects.get("earth").getxVelocity(),2) + Math.pow(objects.get("earth").getyVelocity(),2));
        double vxVersor = objects.get("earth").getxVelocity()/vAbsEarth;
        double vyVersor = objects.get("earth").getyVelocity()/vAbsEarth;
        objects.put("spaceship",new Particle(objects.get("earth").getX(),
                objects.get("earth").getY() - 1500,
                objects.get("earth").getxVelocity() + (8 + 7.12)*vxVersor
                , objects.get("earth").getyVelocity() + (8 + 7.12)*vyVersor ,0,
                2*Math.pow(10,5))); //TODO: VER RADIO


        // un integration algorithm para cada posicion, velocidad, de cada particula?
//        switch (type){
//            case BEEMAN:
//                integrationAlgorithmImp = new BeemanAlgorithm(simulationDeltaT, outputDeltaT, p);
//                break;
//            case VERLET:
//                integrationAlgorithmImp =  new VerletAlgorithm(simulationDeltaT,outputDeltaT, p);
//                break;
//            default:
//                integrationAlgorithmImp =  new GearAlgorithm(simulationDeltaT,outputDeltaT, p);
//        }
    }

    public void nextIteration() {
        double newPosition, newVelocity;
        double iterationTime = this.currentSimulationTime;
   /*     for (;
             iterationTime <= this.currentSimulationTime + this.outputDeltaT  && iterationTime <= SpaceConstants.FINAL_TIME;
             iterationTime += this.simulationDeltaT) {
            newPosition = integrationAlgorithmImp.getNewPosition();
            newVelocity = integrationAlgorithmImp.getNewVelocity();
            this.p.setX(newPosition);
            this.p.setxVelocity(newVelocity);
        }*/

        currentSimulationTime = iterationTime;
    }

    public static void createStaticFile(String outputName, String algorithmName, String outputPath, double simulationDeltaT) throws FileNotFoundException, UnsupportedEncodingException {
        System.out.println("\tCreating static file. . .");

        PrintWriter writer = new PrintWriter(outputPath + outputName + "/static.txt", "UTF-8");
        writer.println(String.format(Locale.ENGLISH, "%s\n%f\n%f\n%d\n%f", algorithmName, Constants.K, Constants.GAMMA, Constants.A, simulationDeltaT));
        writer.close();

        System.out.println("\tStatic file successfully created");
    }

    public void createDynamicFile(String outputName, String outputPath) throws FileNotFoundException, UnsupportedEncodingException {
        System.out.println("\tCreating dynamic file. . .");
        PrintWriter writer = new PrintWriter(outputPath + outputName + "/dynamic" + ".txt", "UTF-8");

        for (double i = 0; i <= SpaceConstants.FINAL_TIME; i += this.outputDeltaT) {
            writer.write(this.currentSimulationTime +"\n"+ "e " + objects.get("earth").getX()  + ";" + objects.get("earth").getY()  + objects.get("earth").getxVelocity() + objects.get("earth").getyVelocity() +  "\n");
            nextIteration();
        }
        writer.close();

        System.out.println("\tDynamic file successfully created");
    }
}
