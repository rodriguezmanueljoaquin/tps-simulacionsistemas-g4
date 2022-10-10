import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;

public class SpaceSimulation {
    private double simulationDeltaT;
    private double outputDeltaT;
    private double currentSimulationTime;

    private static Particle sun;
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

            objects.put(planetName,p);
        }
        Particle earth = objects.get("earth");
        double vAbsEarth = Math.sqrt(Math.pow(earth.getxVelocity(), 2) + Math.pow(earth.getyVelocity(), 2));
        double tx = earth.getxVelocity() / vAbsEarth;
        double ty = earth.getyVelocity() / vAbsEarth;
        Particle p = new Particle(earth.getX(),
                earth.getY() - 1500,
                earth.getxVelocity() + (8 + 7.12) * tx
                , earth.getyVelocity() + (8 + 7.12) * ty, 0,
                2 * Math.pow(10, 5));//TODO: VER RADIO
//        objects.put("spaceship", p);
//    }
        //Inicializamos las aceleraciones de los planetas
        initializeParticlesAccelerations();
    }

    private void initializeParticlesAccelerations(){
        //Primero, armamos una lista de los planetas con el sol
        List<Particle> planetsWithSun = getPlanetsWithSun(new ArrayList<>(objects.values()));
        //Luego, creamos un mapa con las particulas en estado previo
        Map<String,Particle> previousObjects = new HashMap<>();
        //Por cada particula, calculamos su aceleracion (en x y en y), y sus posiciones y velocidades previas con Euler
        for (String objectName : objects.keySet()){
            Particle currentParticle = objects.get(objectName);
            List<Particle> otherParticles = planetsWithSun.stream().filter(p->!p.equals(currentParticle)).collect(Collectors.toList());
            currentParticle.setXAcceleration(SpaceMissionHelper.totalForceX(currentParticle,otherParticles)/currentParticle.getMass());
            currentParticle.setYAcceleration(SpaceMissionHelper.totalForceY(currentParticle,otherParticles)/currentParticle.getMass());
            double prevXPosition = SpaceMissionHelper.getEulerPosition(currentParticle.getX(),currentParticle.getxVelocity(), currentParticle.getXAcceleration(),-simulationDeltaT,false);
            double prevYPosition = SpaceMissionHelper.getEulerPosition(currentParticle.getY(),currentParticle.getyVelocity(), currentParticle.getYAcceleration(),-simulationDeltaT,false);
            double prevXVelocity = SpaceMissionHelper.getEulerVelocity(currentParticle.getxVelocity(),currentParticle.getXAcceleration(),-simulationDeltaT);
            double prevYVelocity = SpaceMissionHelper.getEulerVelocity(currentParticle.getyVelocity(),currentParticle.getYAcceleration(),-simulationDeltaT);
            previousObjects.put(objectName,new Particle(prevXPosition,prevYPosition,prevXVelocity,prevYVelocity,currentParticle.getRadius(),currentParticle.getMass()));
        }
        //Luego, actualizamos la lista de planetas con el sol para que utlice los planetas con estado previo, y calculamos las aceleraciones previas
        planetsWithSun= getPlanetsWithSun(new ArrayList<>(previousObjects.values()));
        for (String objectName : objects.keySet()){
            Particle currentParticle = objects.get(objectName);
            Particle currentPreviousParticle = previousObjects.get(objectName);
            List<Particle> otherPreviousParticles = planetsWithSun.stream().filter(p->!p.equals(currentPreviousParticle)).collect(Collectors.toList());
            currentParticle.setXPrevAcceleration(SpaceMissionHelper.totalForceX(currentPreviousParticle,otherPreviousParticles)/currentPreviousParticle.getMass());
            currentParticle.setYPrevAcceleration(SpaceMissionHelper.totalForceY(currentPreviousParticle,otherPreviousParticles)/currentPreviousParticle.getMass());
        }
    }

    private List<Particle> getPlanetsWithSun(List<Particle> planets){
        List<Particle> planetsWithSun = new ArrayList<>(planets);
        planetsWithSun.add(sun);
        return planetsWithSun;
    }


    public void nextIteration() {

        double iterationTime = this.currentSimulationTime;
//        while(iterationTime <= this.currentSimulationTime + this.outputDeltaT  && iterationTime <= SpaceConstants.FINAL_TIME){
        while(Math.abs(iterationTime - (this.currentSimulationTime + this.outputDeltaT))>=SpaceConstants.EPSILON  && Math.abs(iterationTime - SpaceConstants.FINAL_TIME)>=SpaceConstants.EPSILON){
            //Primero,armamos un mapa con las particulas en el estado siguiente
            Map<String,Particle> nextObjects = new HashMap<>();
            for(String planetName : objects.keySet()){
                Particle p = objects.get(planetName);
                double nextXPosition = SpaceMissionHelper.getBeemanPosition(p.getX(),p.getxVelocity(),p.getXAcceleration(),p.getXPrevAcceleration(),simulationDeltaT);
                double nextYPosition = SpaceMissionHelper.getBeemanPosition(p.getY(),p.getyVelocity(),p.getYAcceleration(),p.getYPrevAcceleration(),simulationDeltaT);
                double nextXVelocity = SpaceMissionHelper.getBeemanPredictedVelocity(p.getxVelocity(),p.getXAcceleration(),p.getXPrevAcceleration(),simulationDeltaT);
                double nextYVelocity = SpaceMissionHelper.getBeemanPredictedVelocity(p.getyVelocity(),p.getYAcceleration(),p.getYPrevAcceleration(),simulationDeltaT);
                nextObjects.put(planetName,new Particle(nextXPosition,nextYPosition,nextXVelocity,nextYVelocity,p.getRadius(),p.getMass()));
            }
            //Luego, actualizamos las posiciones y velocidades de todos los planetas
            for(String st : objects.keySet()){
                //Tomamos la particula correspondiente
                Particle currentParticle = objects.get(st);
                //Actualizamos la posicion (en X e Y)
                currentParticle.setX(nextObjects.get(st).getX());
                currentParticle.setY(nextObjects.get(st).getY());
                //Calculamos las aceleraciones de los planetas en el estado siguiente
                for(String planetName : nextObjects.keySet()){
                    Particle currentNextParticle = nextObjects.get(planetName);
                    List<Particle> otherNextParticles = getPlanetsWithSun(new ArrayList<>(nextObjects.values())).stream().filter(p->!p.equals(currentNextParticle)).collect(Collectors.toList());
                    currentNextParticle.setXAcceleration(SpaceMissionHelper.totalForceX(currentNextParticle,otherNextParticles)/currentNextParticle.getMass());
                    currentNextParticle.setYAcceleration(SpaceMissionHelper.totalForceY(currentNextParticle,otherNextParticles)/currentNextParticle.getMass());
                }
                //Actualizamos la velocidad (en X e Y)
                Particle currentParticleNextState = nextObjects.get(st);
                currentParticle.setxVelocity(SpaceMissionHelper.getBeemanCorrectedVelocity(currentParticle.getxVelocity(),currentParticleNextState.getXAcceleration(),currentParticle.getXAcceleration(),currentParticle.getXPrevAcceleration(),simulationDeltaT));
                currentParticle.setyVelocity(SpaceMissionHelper.getBeemanCorrectedVelocity(currentParticle.getyVelocity(),currentParticleNextState.getYAcceleration(),currentParticle.getYAcceleration(),currentParticle.getYPrevAcceleration(),simulationDeltaT));
            }
            //Luego, actualizamos las aceleraciones previa y actual (en X e Y) y el tiempo de iteracion
            for(String st : objects.keySet()){
                //Tomamos la particula correspondiente y la lista de particulas sin ella con el sol
                Particle currentParticle = objects.get(st);
                List<Particle> otherParticles = getPlanetsWithSun(new ArrayList<>(objects.values())).stream().filter(p->!p.equals(currentParticle)).collect(Collectors.toList());
                currentParticle.setXPrevAcceleration(currentParticle.getXAcceleration());
                currentParticle.setYPrevAcceleration(currentParticle.getYAcceleration());
                currentParticle.setXAcceleration(SpaceMissionHelper.totalForceX(currentParticle,otherParticles)/currentParticle.getMass());
                currentParticle.setYAcceleration(SpaceMissionHelper.totalForceY(currentParticle,otherParticles)/currentParticle.getMass());
            }

            iterationTime+=this.simulationDeltaT;
        }

        currentSimulationTime = iterationTime;
    }

    public static void createStaticFile(String outputName, String algorithmName, String outputPath, double simulationDeltaT) throws FileNotFoundException, UnsupportedEncodingException {
        System.out.println("\tCreating static file. . .");

        PrintWriter writer = new PrintWriter(outputPath + outputName + "/static.txt", "UTF-8");
        writer.println(String.format(Locale.ENGLISH, "%s\n%f", algorithmName, simulationDeltaT));
        writer.write("sun " + sun.getX()  + ";" + sun.getY() + ";" + sun.getRadius() +  "\n");
        writer.write("e " + SpaceConstants.EARTH_RADIUS +  "\n");
        writer.write("v " + SpaceConstants.VENUS_RADIUS + "\n");
        writer.write("s " + 1 +  "\n"); //TODO RADIO SPACESHIP?
        writer.close();

        System.out.println("\tStatic file successfully created");
    }

    public void createDynamicFile(String outputName, String outputPath) throws FileNotFoundException, UnsupportedEncodingException {
        System.out.println("\tCreating dynamic file. . .");
        PrintWriter writer = new PrintWriter(outputPath + outputName + "/dynamic" + ".txt", "UTF-8");

        for (double i = 0; i <= SpaceConstants.FINAL_TIME; i += this.outputDeltaT) {
            writer.write(this.currentSimulationTime +"\n"+ "e " + objects.get("earth").getX()  + ";" + objects.get("earth").getY() + ";" + objects.get("earth").getxVelocity() + ";" + objects.get("earth").getyVelocity() + "\n");
            writer.write("v " + objects.get("venus").getX()  + ";" + objects.get("venus").getY()  + ";" + objects.get("venus").getxVelocity() + ";" + objects.get("venus").getyVelocity()  +  "\n");
//            writer.write("s " + objects.get("spaceship").getX()  + ";" + objects.get("spaceship").getY()  + ";" + objects.get("spaceship").getxVelocity() + ";" + objects.get("spaceship").getyVelocity() +  "\n");
            nextIteration();
        }
        writer.close();

        System.out.println("\tDynamic file successfully created");
    }
}
