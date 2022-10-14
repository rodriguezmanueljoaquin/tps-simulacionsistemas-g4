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
    private double initialVelocityModule;
    private Particle originPlanet;
    private Particle destinyPlanet;
    private Particle spaceship;

    private static Particle sun;
    private Map<PlanetType, Particle> objects = new HashMap<>();

    public SpaceSimulation(Double simulationDeltaT, Double outputDeltaT, Double secondsToDeparture, double initialVelocityModule, PlanetType origin, PlanetType destiny) {
        this.simulationDeltaT = simulationDeltaT;
        this.outputDeltaT = outputDeltaT;
        this.currentSimulationTime = 0.;
        this.secondsToDeparture = secondsToDeparture;
        this.initialVelocityModule = initialVelocityModule;

        this.sun = new Particle(
                0,
                0.,
                0.,
                0,
                SpaceConstants.SUN_RADIUS,
                SpaceConstants.SUN_MASS);

        for (int i = 1; i <= 2; i++) {
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

            objects.put(planetType, p);
        }

        //Inicializamos los planetas origen y destino
        this.originPlanet = objects.get(origin);
        this.destinyPlanet = objects.get(destiny);

        //Inicializamos las aceleraciones de los planetas
        initializeParticlesAccelerations();
    }

    private void initializeParticlesAccelerations() {
        //Primero, armamos una lista de los planetas con el sol
        List<Particle> planetsWithSun = getPlanetsWithSun(new ArrayList<>(objects.values()));
        //Luego, creamos un mapa con las particulas en estado previo
        Map<PlanetType, Particle> previousObjects = new HashMap<>();
        //Por cada particula, calculamos su aceleracion (en x y en y), y sus posiciones y velocidades previas con Euler
        for (PlanetType planetType : objects.keySet()) {
            Particle currentParticle = objects.get(planetType);
            List<Particle> otherParticles = planetsWithSun.stream().filter(p -> !p.equals(currentParticle)).collect(Collectors.toList());
            currentParticle.setXAcceleration(SpaceMissionHelper.totalForceX(currentParticle, otherParticles) / currentParticle.getMass());
            currentParticle.setYAcceleration(SpaceMissionHelper.totalForceY(currentParticle, otherParticles) / currentParticle.getMass());
            double prevXPosition = SpaceMissionHelper.getEulerPosition(currentParticle.getX(), currentParticle.getxVelocity(), currentParticle.getXAcceleration(), -simulationDeltaT, false);
            double prevYPosition = SpaceMissionHelper.getEulerPosition(currentParticle.getY(), currentParticle.getyVelocity(), currentParticle.getYAcceleration(), -simulationDeltaT, false);
            double prevXVelocity = SpaceMissionHelper.getEulerVelocity(currentParticle.getxVelocity(), currentParticle.getXAcceleration(), -simulationDeltaT);
            double prevYVelocity = SpaceMissionHelper.getEulerVelocity(currentParticle.getyVelocity(), currentParticle.getYAcceleration(), -simulationDeltaT);
            previousObjects.put(planetType, new Particle(prevXPosition, prevYPosition, prevXVelocity, prevYVelocity, currentParticle.getRadius(), currentParticle.getMass()));
        }
        //Luego, actualizamos la lista de planetas con el sol para que utlice los planetas con estado previo, y calculamos las aceleraciones previas
        planetsWithSun = getPlanetsWithSun(new ArrayList<>(previousObjects.values()));
        for (PlanetType planetType : objects.keySet()) {
            Particle currentParticle = objects.get(planetType);
            Particle currentPreviousParticle = previousObjects.get(planetType);
            List<Particle> otherPreviousParticles = planetsWithSun.stream().filter(p -> !p.equals(currentPreviousParticle)).collect(Collectors.toList());
            currentParticle.setXPrevAcceleration(SpaceMissionHelper.totalForceX(currentPreviousParticle, otherPreviousParticles) / currentPreviousParticle.getMass());
            currentParticle.setYPrevAcceleration(SpaceMissionHelper.totalForceY(currentPreviousParticle, otherPreviousParticles) / currentPreviousParticle.getMass());
        }
    }

    private List<Particle> getPlanetsWithSun(List<Particle> planets) {
        List<Particle> planetsWithSun = new ArrayList<>(planets);
        planetsWithSun.add(sun);
        return planetsWithSun;
    }

    private void launchSpaceship() {

        //Primero, calculamos la distancia entre el origen y el sol
        double distanceSunToOrigin = sun.calculateDistanceToWithoutRadius(originPlanet);
        //Luego, calculamos los versores correspondientes al vector normal
        double rx = (originPlanet.getX() - sun.getX()) / distanceSunToOrigin;
        double ry = (originPlanet.getY() - sun.getY()) / distanceSunToOrigin;

        // calculamos versores correspondientes al vector tangencial a la orbita de la tierra
        double tx = -ry;
        double ty = rx;

        double distanceSpaceshipToOrigin = SpaceConstants.DISTANCE_SPACE_STATION_TO_ORIGIN + originPlanet.getRadius();
        double spaceshipX = originPlanet.getX() - distanceSpaceshipToOrigin * rx;
        double spaceshipY = originPlanet.getY() - distanceSpaceshipToOrigin * ry;

        double tangentialVelocity = SpaceConstants.VELOCITY_SPACIAL_STATION + -1 * this.initialVelocityModule
                + originPlanet.getxVelocity() * tx + originPlanet.getyVelocity() * ty;
        double spaceshipVX = tangentialVelocity * tx;
        double spaceshipVY = tangentialVelocity * ty;

        spaceship = new Particle(spaceshipX, spaceshipY, spaceshipVX, spaceshipVY, SpaceConstants.SPACESHIP_RADIUS, SpaceConstants.SPACESHIP_MASS);

        objects.put(PlanetType.SPACESHIP, spaceship);

        //Calculamos la aceleracion de la nave
        List<Particle> planetsWithSun = getPlanetsWithSun(new ArrayList<>(objects.values()));
        List<Particle> otherParticles = planetsWithSun.stream().filter(p -> !p.equals(spaceship)).collect(Collectors.toList());
        spaceship.setXAcceleration(SpaceMissionHelper.totalForceX(spaceship, otherParticles) / spaceship.getMass());
        spaceship.setYAcceleration(SpaceMissionHelper.totalForceY(spaceship, otherParticles) / spaceship.getMass());

        //Calculamos la aceleracion previa de la nave
        Map<PlanetType, Particle> previousObjects = new HashMap<>();
        //Por cada particula, calculamos su aceleracion (en x y en y), y sus posiciones y velocidades previas con Euler
        for (PlanetType planetType : objects.keySet()) {
            Particle currentParticle = objects.get(planetType);
            List<Particle> otherPlanets = planetsWithSun.stream().filter(p -> !p.equals(currentParticle)).collect(Collectors.toList());
            currentParticle.setXAcceleration(SpaceMissionHelper.totalForceX(currentParticle, otherPlanets) / currentParticle.getMass());
            currentParticle.setYAcceleration(SpaceMissionHelper.totalForceY(currentParticle, otherPlanets) / currentParticle.getMass());
            double prevXPosition = SpaceMissionHelper.getEulerPosition(currentParticle.getX(), currentParticle.getxVelocity(), currentParticle.getXAcceleration(), -simulationDeltaT, false);
            double prevYPosition = SpaceMissionHelper.getEulerPosition(currentParticle.getY(), currentParticle.getyVelocity(), currentParticle.getYAcceleration(), -simulationDeltaT, false);
            double prevXVelocity = SpaceMissionHelper.getEulerVelocity(currentParticle.getxVelocity(), currentParticle.getXAcceleration(), -simulationDeltaT);
            double prevYVelocity = SpaceMissionHelper.getEulerVelocity(currentParticle.getyVelocity(), currentParticle.getYAcceleration(), -simulationDeltaT);
            previousObjects.put(planetType, new Particle(prevXPosition, prevYPosition, prevXVelocity, prevYVelocity, currentParticle.getRadius(), currentParticle.getMass()));
        }

        planetsWithSun = getPlanetsWithSun(new ArrayList<>(previousObjects.values()));
        Particle previousSpaceship = previousObjects.get(PlanetType.SPACESHIP);
        List<Particle> otherPreviousParticles = planetsWithSun.stream().filter(p -> !p.equals(previousSpaceship)).collect(Collectors.toList());
        spaceship.setXPrevAcceleration(SpaceMissionHelper.totalForceX(previousSpaceship, otherPreviousParticles) / previousSpaceship.getMass());
        spaceship.setYPrevAcceleration(SpaceMissionHelper.totalForceY(previousSpaceship, otherPreviousParticles) / previousSpaceship.getMass());
    }

    private boolean continueIteration(Double timeSinceDeparture) {
        return spaceship == null ||
                (!hasArrived() &&
                        Math.abs(SpaceConstants.MAX_TRIP_TIME - timeSinceDeparture) >= SpaceConstants.EPSILON &&
                        !spaceshipImpactOtherPlanets(spaceship,
                                getPlanetsWithSun(objects.values().stream()
                                        .filter(p ->
                                                !p.equals(destinyPlanet) && !p.equals(spaceship)
                                        ).collect(Collectors.toList()))));
    }

    private boolean spaceshipImpactOtherPlanets(Particle spaceship, List<Particle> otherPlanets) {
        boolean answer = otherPlanets.stream().anyMatch(p -> spaceship.calculateDistanceTo(p) <= 0);
        if (answer) System.out.println("EMBRACE FOR IMPACT!");
        return answer;
    }

    private boolean hasArrived() {
        boolean answer = spaceship.calculateDistanceTo(destinyPlanet) <= SpaceConstants.ARRIVAL_UMBRAL;
        if (answer)
            System.out.println("EMBRACE FOR ARRIVAL! "+ secondsToDeparture);
        return answer;
    }

    public void nextIteration() {

        double iterationTime = this.currentSimulationTime;
        while (Math.abs(iterationTime - (this.currentSimulationTime + this.outputDeltaT)) >= SpaceConstants.EPSILON
                && continueIteration(Math.max(iterationTime - this.secondsToDeparture, 0))) {

            //Primero, chequeamos si es el tiempo de despegue. En dicho caso, creamos la nave
            if (spaceship == null && iterationTime > secondsToDeparture) {
                launchSpaceship();
            }

            //Luego,armamos un mapa con las particulas en el estado siguiente
            Map<PlanetType, Particle> nextObjects = new HashMap<>();
            for (PlanetType planetType : objects.keySet()) {
                Particle p = objects.get(planetType);
                double nextXPosition = SpaceMissionHelper.getBeemanPosition(p.getX(), p.getxVelocity(), p.getXAcceleration(), p.getXPrevAcceleration(), simulationDeltaT);
                double nextYPosition = SpaceMissionHelper.getBeemanPosition(p.getY(), p.getyVelocity(), p.getYAcceleration(), p.getYPrevAcceleration(), simulationDeltaT);
                double nextXVelocity = SpaceMissionHelper.getBeemanPredictedVelocity(p.getxVelocity(), p.getXAcceleration(), p.getXPrevAcceleration(), simulationDeltaT);
                double nextYVelocity = SpaceMissionHelper.getBeemanPredictedVelocity(p.getyVelocity(), p.getYAcceleration(), p.getYPrevAcceleration(), simulationDeltaT);
                nextObjects.put(planetType, new Particle(nextXPosition, nextYPosition, nextXVelocity, nextYVelocity, p.getRadius(), p.getMass()));
            }

            //Luego, actualizamos las posiciones y velocidades de todos los planetas
            for (PlanetType planetType : objects.keySet()) {
                //Tomamos la particula correspondiente
                Particle currentParticle = objects.get(planetType);
                //Actualizamos la posicion (en X e Y)
                currentParticle.setX(nextObjects.get(planetType).getX());
                currentParticle.setY(nextObjects.get(planetType).getY());
                //Calculamos las aceleraciones de los planetas en el estado siguiente
                for (PlanetType pt : nextObjects.keySet()) {
                    Particle currentNextParticle = nextObjects.get(pt);
                    List<Particle> otherNextParticles = getPlanetsWithSun(new ArrayList<>(nextObjects.values())).stream().filter(p -> !p.equals(currentNextParticle)).collect(Collectors.toList());
                    currentNextParticle.setXAcceleration(SpaceMissionHelper.totalForceX(currentNextParticle, otherNextParticles) / currentNextParticle.getMass());
                    currentNextParticle.setYAcceleration(SpaceMissionHelper.totalForceY(currentNextParticle, otherNextParticles) / currentNextParticle.getMass());
                }
                //Actualizamos la velocidad (en X e Y)
                Particle currentParticleNextState = nextObjects.get(planetType);
                currentParticle.setxVelocity(SpaceMissionHelper.getBeemanCorrectedVelocity(currentParticle.getxVelocity(), currentParticleNextState.getXAcceleration(), currentParticle.getXAcceleration(), currentParticle.getXPrevAcceleration(), simulationDeltaT));
                currentParticle.setyVelocity(SpaceMissionHelper.getBeemanCorrectedVelocity(currentParticle.getyVelocity(), currentParticleNextState.getYAcceleration(), currentParticle.getYAcceleration(), currentParticle.getYPrevAcceleration(), simulationDeltaT));
            }
            //Luego, actualizamos las aceleraciones previa y actual (en X e Y) y el tiempo de iteracion
            for (PlanetType planetType : objects.keySet()) {
                //Tomamos la particula correspondiente y la lista de particulas sin ella con el sol
                Particle currentParticle = objects.get(planetType);
                List<Particle> otherParticles = getPlanetsWithSun(new ArrayList<>(objects.values())).stream().filter(p -> !p.equals(currentParticle)).collect(Collectors.toList());
                currentParticle.setXPrevAcceleration(currentParticle.getXAcceleration());
                currentParticle.setYPrevAcceleration(currentParticle.getYAcceleration());
                currentParticle.setXAcceleration(SpaceMissionHelper.totalForceX(currentParticle, otherParticles) / currentParticle.getMass());
                currentParticle.setYAcceleration(SpaceMissionHelper.totalForceY(currentParticle, otherParticles) / currentParticle.getMass());
            }

            iterationTime += this.simulationDeltaT;
        }

        currentSimulationTime = iterationTime;
    }

    public static void createStaticFile(String outputName, String algorithmName, String outputPath, double simulationDeltaT, double departureTime, double initialVelocityModule, PlanetType origin, PlanetType destiny) throws FileNotFoundException, UnsupportedEncodingException {
        System.out.println("\tCreating static file. . .");

        PrintWriter writer = new PrintWriter(outputPath + outputName + "/static.txt", "UTF-8");
        writer.write(String.format(Locale.ENGLISH, "%s\n%f\n%s\n%f\n%f\n", algorithmName, simulationDeltaT,
                SpaceConstants.START_SIMULATION_DATE.toString(), departureTime, initialVelocityModule));
        writer.write(PlanetType.SUN.ordinal() + " " + sun.getX() + ";" + sun.getY() + ";" + sun.getRadius() + "\n");
        writer.write(PlanetType.EARTH.ordinal() + " " + SpaceConstants.EARTH_RADIUS + "\n");
        writer.write(PlanetType.VENUS.ordinal() + " " + SpaceConstants.VENUS_RADIUS + "\n");
        writer.write(PlanetType.SPACESHIP.ordinal() + " " + SpaceConstants.SPACESHIP_RADIUS + "\n");
        writer.write("origin" + " " + origin.getPlanetName() + "\n");
        writer.write("destiny" + " " + destiny.getPlanetName() + "\n");
        writer.close();

        System.out.println("\tStatic file successfully created");
    }

    public void createDynamicFile(String outputName, String outputPath) throws FileNotFoundException, UnsupportedEncodingException {
        System.out.println("\tCreating dynamic file. . .");
        PrintWriter writer = new PrintWriter(outputPath + outputName + "/dynamic" + ".txt", "UTF-8");

        // spaceship departures on start
        if (this.secondsToDeparture == 0) {
            launchSpaceship();
        }
        double currentOutputTime = 0;

        for (;currentOutputTime <= SpaceConstants.MAX_TRIP_TIME + this.secondsToDeparture && continueIteration(Math.max(currentOutputTime - this.secondsToDeparture, 0)); currentOutputTime += this.outputDeltaT) {
            writer.write(this.currentSimulationTime + "\n");
            writer.write(PlanetType.EARTH.ordinal() + " " + objects.get(PlanetType.EARTH).getX() + ";" + objects.get(PlanetType.EARTH).getY() + ";" + objects.get(PlanetType.EARTH).getxVelocity() + ";" + objects.get(PlanetType.EARTH).getyVelocity() + "\n");
            writer.write(PlanetType.VENUS.ordinal() + " " + objects.get(PlanetType.VENUS).getX() + ";" + objects.get(PlanetType.VENUS).getY() + ";" + objects.get(PlanetType.VENUS).getxVelocity() + ";" + objects.get(PlanetType.VENUS).getyVelocity() + "\n");
            if (spaceship != null) {
                writer.write(PlanetType.SPACESHIP.ordinal() + " " + spaceship.getX() + ";" + spaceship.getY() + ";" + spaceship.getxVelocity() + ";" + spaceship.getyVelocity() + "\n");
            } else {
                writer.write("\n");
            }
            nextIteration();
        }
        writer.write(this.currentSimulationTime + "\n");
        writer.write(PlanetType.EARTH.ordinal() + " " + objects.get(PlanetType.EARTH).getX() + ";" + objects.get(PlanetType.EARTH).getY() + ";" + objects.get(PlanetType.EARTH).getxVelocity() + ";" + objects.get(PlanetType.EARTH).getyVelocity() + "\n");
        writer.write(PlanetType.VENUS.ordinal() + " " + objects.get(PlanetType.VENUS).getX() + ";" + objects.get(PlanetType.VENUS).getY() + ";" + objects.get(PlanetType.VENUS).getxVelocity() + ";" + objects.get(PlanetType.VENUS).getyVelocity() + "\n");
        writer.write(PlanetType.SPACESHIP.ordinal() + " " + spaceship.getX() + ";" + spaceship.getY() + ";" + spaceship.getxVelocity() + ";" + spaceship.getyVelocity() + "\n");
        writer.close();

        System.out.println("\tDynamic file successfully created");
    }
}
