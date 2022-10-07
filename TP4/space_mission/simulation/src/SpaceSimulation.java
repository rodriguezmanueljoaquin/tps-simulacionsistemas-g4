import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;

public class SpaceSimulation {
    private double simulationDeltaT;
    private double outputDeltaT;
    private double currentSimulationTime;

    private Particle sun;
    private Map<String ,Pair<Particle,Pair<IntegrationAlgorithmImp,IntegrationAlgorithmImp>>> objects = new HashMap<>();


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
        for (String planetName : planetNames) {
            Pair<Double, Double> position = HorizonResultsReader.getPosition("space_mission/datasets/horizons_results_" + planetName + ".txt");
            Pair<Double, Double> velocity = HorizonResultsReader.getVelocity("space_mission/datasets/horizons_results_" + planetName + ".txt");
            Particle p = new Particle(
                    position.getLeft(),
                    position.getRight(),
                    velocity.getLeft(),
                    velocity.getRight(),
                    planetName.equals(planetNames.get(0)) ? SpaceConstants.EARTH_RADIUS : SpaceConstants.VENUS_RADIUS,
                    planetName.equals(planetNames.get(0)) ? SpaceConstants.EARTH_MASS : SpaceConstants.VENUS_MASS);

            Particle p2 = p;
            p2.setX(p2.getY());
            p2.setxVelocity(p2.getyVelocity());
            objects.put(planetName, new Pair<>(p,new Pair<>( selectMethod(type, p),selectMethod(type,p2))));
        }
        double vAbsEarth = Math.sqrt(Math.pow(objects.get("earth").getLeft().getxVelocity(), 2) + Math.pow(objects.get("earth").getLeft().getyVelocity(), 2));
        double vxVersor = objects.get("earth").getLeft().getxVelocity() / vAbsEarth;
        double vyVersor = objects.get("earth").getLeft().getyVelocity() / vAbsEarth;
        Particle p = new Particle(objects.get("earth").getLeft().getX(),
                objects.get("earth").getLeft().getY() - 1500,
                objects.get("earth").getLeft().getxVelocity() + (8 + 7.12) * vxVersor
                , objects.get("earth").getLeft().getyVelocity() + (8 + 7.12) * vyVersor, 0,
                2 * Math.pow(10, 5));//TODO: VER RADIO
        Particle p2 = p;
        p2.setX(p2.getY());
        p2.setxVelocity(p2.getyVelocity());
        objects.put("spaceship", new Pair<>(p, new Pair<>(selectMethod(type, p),selectMethod(type,p2))));
    }


    private IntegrationAlgorithmImp selectMethod(IntegrationAlgorithmImp.Type type, Particle p){
    switch (type){
        case BEEMAN:
               return new BeemanAlgorithm(simulationDeltaT, outputDeltaT, p);
        case VERLET:
               return  new VerletAlgorithm(simulationDeltaT,outputDeltaT, p);
        default:
               return new GearAlgorithm(simulationDeltaT,outputDeltaT, p);
       }
    }
    public void nextIteration() {
        double iterationTime = 0;
        for(String st : objects.keySet()){
            double newPosition, newVelocity;
            iterationTime = this.currentSimulationTime;
            while(iterationTime <= this.currentSimulationTime + this.outputDeltaT  && iterationTime <= SpaceConstants.FINAL_TIME){
                Particle p = objects.get(st).getLeft();
                IntegrationAlgorithmImp integrationAlgorithmImpX = objects.get(st).getRight().getLeft();
                IntegrationAlgorithmImp integrationAlgorithmImpY = objects.get(st).getRight().getRight();
                newPosition = integrationAlgorithmImpX.getNewPosition();
                newVelocity = integrationAlgorithmImpX.getNewVelocity();
                p.setX(newPosition);
                p.setxVelocity(newVelocity);
                newPosition = integrationAlgorithmImpY.getNewPosition();
                newVelocity = integrationAlgorithmImpY.getNewVelocity();
                p.setY(newPosition);
                p.setyVelocity(newVelocity);
                iterationTime+=this.simulationDeltaT;
            }



        }
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
            writer.write(this.currentSimulationTime +"\n"+ "e " + objects.get("earth").getLeft().getX()  + ";" + objects.get("earth").getLeft().getY() + ";" + objects.get("earth").getLeft().getxVelocity() + ";" + objects.get("earth").getLeft().getyVelocity() +  "\n");
            writer.write("v " + objects.get("venus").getLeft().getX()  + ";" + objects.get("venus").getLeft().getY()  + ";" + objects.get("venus").getLeft().getxVelocity() + ";" + objects.get("venus").getLeft().getyVelocity() +  "\n");
            writer.write( "s " + objects.get("spaceship").getLeft().getX()  + ";" + objects.get("spaceship").getLeft().getY()  + ";" +objects.get("spaceship").getLeft().getxVelocity() + ";" + objects.get("spaceship").getLeft().getyVelocity() +  "\n");
            nextIteration();
        }
        writer.close();

        System.out.println("\tDynamic file successfully created");
    }
}
