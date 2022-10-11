import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;

public class SpaceSimulation {
    private double simulationDeltaT;
    private double outputDeltaT;
    private double currentSimulationTime;
    private double secondsToDeparture;

    private static Particle sun;
    private Map<PlanetType ,Particle> objects = new HashMap<>();

    public SpaceSimulation(Double simulationDeltaT, Double outputDeltaT, Double secondsToDeparture) {
        this.simulationDeltaT = simulationDeltaT;
        this.outputDeltaT = outputDeltaT;
        this.currentSimulationTime = 0.;
        this.secondsToDeparture = secondsToDeparture;

        this.sun = new Particle(
                0,
                0.,
                0.,
                0,
                SpaceConstants.SUN_RADIUS,
                SpaceConstants.SUN_MASS);

        for (int i=1;i<=2;i++) {
            PlanetType planetType = PlanetType.values()[i];
            Pair<Double, Double> position = HorizonResultsReader.getPosition("space_mission/datasets/horizons_results_" + planetType.getPlanetName() + ".txt");
            Pair<Double, Double> velocity = HorizonResultsReader.getVelocity("space_mission/datasets/horizons_results_" + planetType.getPlanetName() + ".txt");
            Particle p = new Particle(
                    position.getLeft(),
                    position.getRight(),
                    velocity.getLeft(),
                    velocity.getRight(),
                    planetType.getPlanetName().equals("earth") ? SpaceConstants.EARTH_RADIUS : SpaceConstants.VENUS_RADIUS,
                    planetType.getPlanetName().equals("earth") ? SpaceConstants.EARTH_MASS : SpaceConstants.VENUS_MASS);

            objects.put(planetType,p);
        }

        //Inicializamos las aceleraciones de los planetas
        initializeParticlesAccelerations();
    }

    private void initializeParticlesAccelerations(){
        //Primero, armamos una lista de los planetas con el sol
        List<Particle> planetsWithSun = getPlanetsWithSun(new ArrayList<>(objects.values()));
        //Luego, creamos un mapa con las particulas en estado previo
        Map<PlanetType,Particle> previousObjects = new HashMap<>();
        //Por cada particula, calculamos su aceleracion (en x y en y), y sus posiciones y velocidades previas con Euler
        for (PlanetType planetType : objects.keySet()){
            Particle currentParticle = objects.get(planetType);
            List<Particle> otherParticles = planetsWithSun.stream().filter(p->!p.equals(currentParticle)).collect(Collectors.toList());
            currentParticle.setXAcceleration(SpaceMissionHelper.totalForceX(currentParticle,otherParticles)/currentParticle.getMass());
            currentParticle.setYAcceleration(SpaceMissionHelper.totalForceY(currentParticle,otherParticles)/currentParticle.getMass());
            double prevXPosition = SpaceMissionHelper.getEulerPosition(currentParticle.getX(),currentParticle.getxVelocity(), currentParticle.getXAcceleration(),-simulationDeltaT,false);
            double prevYPosition = SpaceMissionHelper.getEulerPosition(currentParticle.getY(),currentParticle.getyVelocity(), currentParticle.getYAcceleration(),-simulationDeltaT,false);
            double prevXVelocity = SpaceMissionHelper.getEulerVelocity(currentParticle.getxVelocity(),currentParticle.getXAcceleration(),-simulationDeltaT);
            double prevYVelocity = SpaceMissionHelper.getEulerVelocity(currentParticle.getyVelocity(),currentParticle.getYAcceleration(),-simulationDeltaT);
            previousObjects.put(planetType,new Particle(prevXPosition,prevYPosition,prevXVelocity,prevYVelocity,currentParticle.getRadius(),currentParticle.getMass()));
        }
        //Luego, actualizamos la lista de planetas con el sol para que utlice los planetas con estado previo, y calculamos las aceleraciones previas
        planetsWithSun= getPlanetsWithSun(new ArrayList<>(previousObjects.values()));
        for (PlanetType planetType : objects.keySet()){
            Particle currentParticle = objects.get(planetType);
            Particle currentPreviousParticle = previousObjects.get(planetType);
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

    private void launchSpaceship(){
        Particle earth = objects.get(PlanetType.EARTH);
        double vAbsEarth = Math.sqrt(Math.pow(earth.getxVelocity(), 2) + Math.pow(earth.getyVelocity(), 2));
        double tx = earth.getxVelocity() / vAbsEarth;
        double ty = earth.getyVelocity() / vAbsEarth;
        double distanceSpaceshipToEarth = SpaceConstants.DISTANCE_SPACE_STATION_TO_EARTH + SpaceConstants.EARTH_RADIUS;
        Particle p = new Particle(earth.getX() - distanceSpaceshipToEarth*tx,
                earth.getY() - distanceSpaceshipToEarth*ty,
                earth.getxVelocity() + (SpaceConstants.VELOCITY_LAUNCH + SpaceConstants.VELOCITY_SPACIAL_STATION) * tx
                , earth.getyVelocity() + (SpaceConstants.VELOCITY_LAUNCH + SpaceConstants.VELOCITY_SPACIAL_STATION) * ty, 1,
                2 * Math.pow(10, 5));//TODO: VER RADIO"spaceship"
        objects.put(PlanetType.SPACESHIP, p);
    }

    private boolean continueIteration(Particle destiny, Double timeSinceDeparture){
        return !objects.containsKey(PlanetType.SPACESHIP) ||
                (!hasArrived(objects.get(PlanetType.SPACESHIP), destiny) &&
                        Math.abs(SpaceConstants.MAX_TRIP_TIME - timeSinceDeparture) >= SpaceConstants.EPSILON);
    }

    private boolean hasArrived(Particle spaceship, Particle destiny){
        boolean hasArrived = spaceship.calculateDistanceTo(destiny) < SpaceConstants.ARRIVAL_UMBRAL;
        if(hasArrived)
            System.out.println("SHIP HAS ARRIVED DEPARTING AT " + this.secondsToDeparture);
        return hasArrived;
    }

    public void nextIteration() {

        double iterationTime = this.currentSimulationTime;
//        while(iterationTime <= this.currentSimulationTime + this.outputDeltaT  && iterationTime <= SpaceConstants.FINAL_TIME){
        while(Math.abs(iterationTime - (this.currentSimulationTime + this.outputDeltaT))>=SpaceConstants.EPSILON
                && continueIteration(objects.get(PlanetType.VENUS), Math.max(iterationTime - this.secondsToDeparture, 0))){

            //Primero, chequeamos si es el tiempo de despegue. En dicho caso, creamos la nave
            if(!objects.containsKey(PlanetType.SPACESHIP) && Math.abs(iterationTime-this.secondsToDeparture) < SpaceConstants.EPSILON){
                launchSpaceship();
            }

            //Luego,armamos un mapa con las particulas en el estado siguiente
            Map<PlanetType,Particle> nextObjects = new HashMap<>();
            for(PlanetType planetType : objects.keySet()){
                Particle p = objects.get(planetType);
                double nextXPosition = SpaceMissionHelper.getBeemanPosition(p.getX(),p.getxVelocity(),p.getXAcceleration(),p.getXPrevAcceleration(),simulationDeltaT);
                double nextYPosition = SpaceMissionHelper.getBeemanPosition(p.getY(),p.getyVelocity(),p.getYAcceleration(),p.getYPrevAcceleration(),simulationDeltaT);
                double nextXVelocity = SpaceMissionHelper.getBeemanPredictedVelocity(p.getxVelocity(),p.getXAcceleration(),p.getXPrevAcceleration(),simulationDeltaT);
                double nextYVelocity = SpaceMissionHelper.getBeemanPredictedVelocity(p.getyVelocity(),p.getYAcceleration(),p.getYPrevAcceleration(),simulationDeltaT);
                nextObjects.put(planetType,new Particle(nextXPosition,nextYPosition,nextXVelocity,nextYVelocity,p.getRadius(),p.getMass()));
            }
            //Luego, actualizamos las posiciones y velocidades de todos los planetas
            for(PlanetType planetType : objects.keySet()){
                //Tomamos la particula correspondiente
                Particle currentParticle = objects.get(planetType);
                //Actualizamos la posicion (en X e Y)
                currentParticle.setX(nextObjects.get(planetType).getX());
                currentParticle.setY(nextObjects.get(planetType).getY());
                //Calculamos las aceleraciones de los planetas en el estado siguiente
                for(PlanetType pt : nextObjects.keySet()){
                    Particle currentNextParticle = nextObjects.get(pt);
                    List<Particle> otherNextParticles = getPlanetsWithSun(new ArrayList<>(nextObjects.values())).stream().filter(p->!p.equals(currentNextParticle)).collect(Collectors.toList());
                    currentNextParticle.setXAcceleration(SpaceMissionHelper.totalForceX(currentNextParticle,otherNextParticles)/currentNextParticle.getMass());
                    currentNextParticle.setYAcceleration(SpaceMissionHelper.totalForceY(currentNextParticle,otherNextParticles)/currentNextParticle.getMass());
                }
                //Actualizamos la velocidad (en X e Y)
                Particle currentParticleNextState = nextObjects.get(planetType);
                currentParticle.setxVelocity(SpaceMissionHelper.getBeemanCorrectedVelocity(currentParticle.getxVelocity(),currentParticleNextState.getXAcceleration(),currentParticle.getXAcceleration(),currentParticle.getXPrevAcceleration(),simulationDeltaT));
                currentParticle.setyVelocity(SpaceMissionHelper.getBeemanCorrectedVelocity(currentParticle.getyVelocity(),currentParticleNextState.getYAcceleration(),currentParticle.getYAcceleration(),currentParticle.getYPrevAcceleration(),simulationDeltaT));
            }
            //Luego, actualizamos las aceleraciones previa y actual (en X e Y) y el tiempo de iteracion
            for(PlanetType planetType : objects.keySet()){
                //Tomamos la particula correspondiente y la lista de particulas sin ella con el sol
                Particle currentParticle = objects.get(planetType);
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

    public static void createStaticFile(String outputName, String algorithmName, String outputPath, double simulationDeltaT, double departureTime) throws FileNotFoundException, UnsupportedEncodingException {
        System.out.println("\tCreating static file. . .");

        PrintWriter writer = new PrintWriter(outputPath + outputName + "/static.txt", "UTF-8");
        writer.write(String.format(Locale.ENGLISH, "%s\n%f\n%s\n%f\n", algorithmName, simulationDeltaT,
                SpaceConstants.START_SIMULATION_DATE.toString(), departureTime));
        writer.write(PlanetType.SUN.ordinal() + " "  + sun.getX()  + ";" + sun.getY() + ";" + sun.getRadius() +  "\n");
        writer.write(PlanetType.EARTH.ordinal() + " " + SpaceConstants.EARTH_RADIUS +  "\n");
        writer.write(PlanetType.VENUS.ordinal() + " " + SpaceConstants.VENUS_RADIUS + "\n");
        writer.write(PlanetType.SPACESHIP.ordinal() + " " + 1 +  "\n"); //TODO RADIO SPACESHIP?
        writer.close();

        System.out.println("\tStatic file successfully created");
    }

    public void createDynamicFile(String outputName, String outputPath) throws FileNotFoundException, UnsupportedEncodingException {
        System.out.println("\tCreating dynamic file. . .");
        PrintWriter writer = new PrintWriter(outputPath + outputName + "/dynamic" + ".txt", "UTF-8");

        for (double i = 0; i <= SpaceConstants.MAX_TRIP_TIME + this.secondsToDeparture; i += this.outputDeltaT) {
            writer.write(this.currentSimulationTime +"\n"+ PlanetType.EARTH.ordinal() + " " + objects.get(PlanetType.EARTH).getX()  + ";" + objects.get(PlanetType.EARTH).getY() + ";" + objects.get(PlanetType.EARTH).getxVelocity() + ";" + objects.get(PlanetType.EARTH).getyVelocity() + "\n");
            writer.write( PlanetType.VENUS.ordinal() + " " + objects.get(PlanetType.VENUS).getX()  + ";" + objects.get(PlanetType.VENUS).getY()  + ";" + objects.get(PlanetType.VENUS).getxVelocity() + ";" + objects.get(PlanetType.VENUS).getyVelocity()  +  "\n");
            if(objects.containsKey(PlanetType.SPACESHIP)){
                writer.write(PlanetType.SPACESHIP.ordinal() + " " + objects.get(PlanetType.SPACESHIP).getX()  + ";" + objects.get(PlanetType.SPACESHIP).getY()  + ";" + objects.get(PlanetType.SPACESHIP).getxVelocity() + ";" + objects.get(PlanetType.SPACESHIP).getyVelocity() +  "\n");
            }
            else{
                writer.write("\n");
            }
            nextIteration();
        }
        writer.close();

        System.out.println("\tDynamic file successfully created");
    }
}
