import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;

public class Population {
    private List<Particle> population;
    private Random rand;
    private Double currentTime, deltaTOutput, cellHalfLength, zombieDesiredVelocity;
    private Boolean isSquareCell;
    private final Integer initialHumansQty;
    private Integer zombiesQty;
    private static Double DELTA_T = Constants.PARTICLE_MIN_RADIUS / (2 * Constants.HUMAN_DESIRED_VELOCITY); // ve = vdmax
    private Pair<Double, Double> zombieAPRange;
    private Pair<Double, Double> zombieBPRange;
    private Pair<Double, Double> humanAPRange;
    private Pair<Double, Double> humanBPRange;
    private Pair<Double, Double> wallAPRange;
    private Pair<Double, Double> wallBPRange;

    private List<Double> wallAps, wallBps;

    public Population(Integer initialHumansQty, Double zombieDesiredVelocity, long seed,
                      Pair<Double, Double> zombieAPRange, Pair<Double, Double> zombieBPRange,
                      Pair<Double, Double> humanAPRange, Pair<Double, Double> humanBPRange,
                      Pair<Double, Double> wallAPRange, Pair<Double, Double> wallBPRange,
                      Integer deltaTOutputMultiplier, boolean isSquareCell) {
        this.initialHumansQty = initialHumansQty;
        this.cellHalfLength = Constants.CIRCLE_RADIUS;
        this.population = new ArrayList<>();
        this.currentTime = 0.;
        this.zombiesQty = 0;
        this.zombieDesiredVelocity = zombieDesiredVelocity;
        this.rand = new Random(seed);
        this.zombieAPRange = zombieAPRange;
        this.zombieBPRange = zombieBPRange;
        this.humanAPRange = humanAPRange;
        this.humanBPRange = humanBPRange;
        this.wallAPRange = wallAPRange;
        this.wallBPRange = wallBPRange;

        this.deltaTOutput = Population.DELTA_T * deltaTOutputMultiplier;
        this.isSquareCell = isSquareCell;

        //Seteamos las posiciones iniciales de las particulas
        setParticlesInitialPosition();
        setWallsCoefficients();
    }

    private double getRandomDoubleBetweenBounds(Pair<Double, Double> bounds) {
        return this.rand.nextDouble() * (bounds.getRight() - bounds.getLeft()) + bounds.getRight();
    }

    private void setWallsCoefficients() {
        this.wallAps = new ArrayList<>();
        this.wallBps = new ArrayList<>();
        for (int i = 0; i < 360; i++) {
            wallAps.add(getRandomDoubleBetweenBounds(wallAPRange));
            wallBps.add(getRandomDoubleBetweenBounds(wallBPRange));
        }
    }

    private Pair<Double, Double> getRandomPositionInCell() {
        double newX, newY;
        double cellHalfLengthReduced = this.cellHalfLength * 0.95; // para que no aparezca en el borde
        if(this.isSquareCell) {
            newX = (rand.nextDouble() * cellHalfLengthReduced *2) - cellHalfLengthReduced;
            newY = (rand.nextDouble() * cellHalfLengthReduced *2) - cellHalfLengthReduced;
        } else {
            double angle = rand.nextDouble() * Math.PI * 2;
            double randPosition = rand.nextDouble();
            newX = Math.cos(angle) * randPosition * cellHalfLengthReduced;
            newY = Math.sin(angle) * randPosition * cellHalfLengthReduced;
        }

        return new Pair<>(newX, newY);
    }

    private void setParticlesInitialPosition() {
        //Seteamos el zombie
        Particle zombie = new Particle(0., 0., this.rand.nextDouble() * 2 * Math.PI,
                ParticleState.ZOMBIE, this.zombieDesiredVelocity,
                this.getRandomDoubleBetweenBounds(zombieAPRange), this.getRandomDoubleBetweenBounds(zombieBPRange));
        this.zombiesQty++;

        //Seteamos a los humanos
        boolean validPosition;
        Particle newParticle = null;
        for (int i = 0; i < this.initialHumansQty; i++) {
            validPosition = false;
            while (!validPosition) {
                Pair<Double, Double> randPositions = getRandomPositionInCell();
                newParticle = new Particle(randPositions.getLeft(), randPositions.getRight(),
                        this.rand.nextDouble() * 2 * Math.PI, ParticleState.HUMAN, Constants.HUMAN_DESIRED_VELOCITY,
                        this.getRandomDoubleBetweenBounds(humanAPRange), this.getRandomDoubleBetweenBounds(humanBPRange));
                // Revisamos que este a la distancia minima del zombie, y que no se solape con otra particula
                validPosition = !(newParticle.calculateDistanceTo(zombie) < Constants.INITIAL_MIN_DISTANCE_TO_ZOMBIE);
                for (Particle other : this.population) {
                    if (newParticle.calculateDistanceTo(other) <= 0)
                        validPosition = false;
                }
            }
            this.population.add(newParticle);
        }
        this.population.add(zombie);
    }

    private boolean areAllZombies() {
        return this.zombiesQty == initialHumansQty + 1;
    }

    private Particle checkWallCollision(Particle p) {
        if(!this.isSquareCell) {
            if (Math.abs(p.distanceToOrigin()) <= this.cellHalfLength) {
                return null;
            }
        } else {
            if (Math.abs(p.getX()) <= this.cellHalfLength && Math.abs(p.getY()) <= this.cellHalfLength) {
                return null;
            }
        }
        // si llego aca es por que impacto contra pared
        // other estaria en el mismo eje de acuerdo al origen pero más lejos
        return new Particle(p.getX() * 2, p.getY() * 2, 0, ParticleState.WALL, 0., 0., 0.);
    }

    private boolean isInInfection(ParticleState state) {
        return state == ParticleState.HUMAN_INFECTED || state == ParticleState.ZOMBIE_INFECTING;
    }

    private void checkInfectionsAndUpdatePositions() {
        for (Particle p : this.population) {
            if (p.getState() == ParticleState.HUMAN_INFECTED || p.getState() == ParticleState.ZOMBIE_INFECTING) {
                if (p.getZombieContactTime() + Constants.INFECTION_DURATION <= this.currentTime) {
//                    System.out.println("Particle " + p.getId() + " ended infection");
                    p.setState(ParticleState.ZOMBIE);
                    p.setVdMax(Constants.ZOMBIE_SEARCH_SPEED);
                    this.zombiesQty++;
                }
            }
            // Actualizo para aquellas que no estan en situación de contacto
            if (!isInInfection(p.getState()))
                p.updatePosition(Population.DELTA_T);
        }
    }

    private boolean isHumanAgainstZombieCollision(Particle supposedHuman, Particle supposedZombie) {
        return supposedHuman.getState() == ParticleState.HUMAN &&
                (supposedZombie.getState() == ParticleState.ZOMBIE || supposedZombie.getState() == ParticleState.ZOMBIE_INFECTING);
    }

    private void findCollisions(List<Particle> freeParticles, Map<CollisionType, List<Pair<Particle, Particle>>> collisions) {
        for (int i = 0; i < this.population.size(); i++) {
            Particle p = this.population.get(i);
            Particle wallCollision = checkWallCollision(p);
            if (wallCollision != null && !isInInfection(p.getState()))
                collisions.get(CollisionType.WALL).add(new Pair<>(p, wallCollision));

            boolean particleCollision = false;
            for (int j = i + 1; j < this.population.size(); j++) {
                Particle other = this.population.get(j);
                if (p.calculateDistanceTo(other) <= 0) {
                    particleCollision = true;
                    if (isHumanAgainstZombieCollision(p, other)) {
                        collisions.get(CollisionType.INFECTION).add(new Pair<>(p, other));
                    } else if (isHumanAgainstZombieCollision(other, p)) {
                        collisions.get(CollisionType.INFECTION).add(new Pair<>(other, p));
                    } else
                        collisions.get(CollisionType.PARTICLE).add(new Pair<>(p, other));
                }
            }

            // chequeamos si la particula que estamos analizando esta involucrada en un choque contra otra particula
            for (List<Pair<Particle, Particle>> collisionsByTpe : collisions.values())
                if (collisionsByTpe.stream().anyMatch(pair -> pair.getLeft().equals(p) || pair.getRight().equals(p)))
                    particleCollision = true;

            if (!isInInfection(p.getState()) && wallCollision == null && !particleCollision) {
                freeParticles.add(p);
            }
        }
    }

    private void updateParticlesInInfection(Particle human, Particle zombie) {
        human.setState(ParticleState.HUMAN_INFECTED);
        human.setZombieContactTime(this.currentTime);
        human.setVdMax(0);
        human.radiusUpdate(true, Population.DELTA_T);

        if (zombie.getState() != ParticleState.ZOMBIE_INFECTING) {
            zombie.setState(ParticleState.ZOMBIE_INFECTING);
            this.zombiesQty--;
        }
        zombie.setZombieContactTime(this.currentTime);
        zombie.setVdMax(0);
        zombie.radiusUpdate(true, Population.DELTA_T);
    }

    private void updateCollisionParticles(Map<CollisionType, List<Pair<Particle, Particle>>> collisions) {
        // Infectar en colisiones humano - zombie
        for (Pair<Particle, Particle> particles : collisions.get(CollisionType.INFECTION)) {
            updateParticlesInInfection(particles.getLeft(), particles.getRight());
        }

        // Choques contra pared
        for (Pair<Particle, Particle> particles : collisions.get(CollisionType.WALL)) {
            Particle wall = particles.getRight();
            Particle p = particles.getLeft();
            p.radiusUpdate(true, DELTA_T);
            p.velocityUpdate(true, wall.getX(), wall.getY());
        }

        // Choques entre particulas
        for (Pair<Particle, Particle> particles : collisions.get(CollisionType.PARTICLE)) {
            Particle p1 = particles.getLeft();
            Particle p2 = particles.getRight();

            int nearZombiesQty = population.stream()
                    .filter(particle -> particle.getState().equals(ParticleState.ZOMBIE) || particle.getState().equals(ParticleState.ZOMBIE_INFECTING))
                    .filter(particle -> particle.calculateDistanceTo(p1) <= Constants.HUMAN_SEARCH_RADIUS
                            || particle.calculateDistanceTo(p2) <= Constants.HUMAN_SEARCH_RADIUS)
                    .collect(Collectors.toList()).size();
//            if (p1.getState().equals(ParticleState.ZOMBIE) && p2.getState().equals(ParticleState.ZOMBIE) &&
//                    p2.getXVelocity() == 0.0 && p1.getYVelocity() == 0.0 &&
//                    p2.getXVelocity() == 0.0 && p2.getYVelocity() == 0.0) {
//                p1.radiusUpdate(true, DELTA_T);
//                p1.radiusUpdate(false, DELTA_T);
//                p1.velocityUpdate(true, p2.getX(), p2.getY());
//                p2.radiusUpdate(true, DELTA_T);
//            } else {
            // Actualizar solo las que no estan en infección y o tienen un zombie cerca o no estan quietas
            if (!isInInfection(p1.getState()) && (nearZombiesQty > 0 || (p1.getXVelocity() != 0 || p1.getYVelocity() != 0))) {
                p1.radiusUpdate(true, DELTA_T);
                p1.velocityUpdate(true, p2.getX(), p2.getY());

            }
            if (!isInInfection(p2.getState()) && (nearZombiesQty > 0 || (p2.getXVelocity() != 0 || p2.getYVelocity() != 0))) {
                p2.radiusUpdate(true, DELTA_T);
                p2.velocityUpdate(true, p1.getX(), p1.getY());
            }
//            }
        }
    }

    private void updateFreeParticles(List<Particle> freeParticles) {
        for (Particle p : freeParticles) {
            p.radiusUpdate(false, Population.DELTA_T);
            double targetX, targetY;

            if (p.getState() == ParticleState.ZOMBIE) {
                // Busca al humano mas cercano a menos de 4m
                Particle other = population.stream()
                        .filter(particle -> particle.getState().equals(ParticleState.HUMAN))
                        .filter(particle -> particle.calculateDistanceTo(p) <= Constants.ZOMBIE_SEARCH_RADIUS)
                        .min(Comparator.comparing(particle -> particle.calculateDistanceTo(p))).orElse(null);

                // Si no hay humano cercano, va hacia el target fijo que ya tenia en caso de que lo haya calculado
                if (other == null) {
                    if (!p.hasWanderTarget() || ((p.getXVelocity() * 1.0) == 0.0 && (p.getYVelocity() * 1.0) == 0.0) || p.changeWanderTarget(this.currentTime)) {
                        // si no hay humano a menos de 4 metros, toma un objetivo random y va hacia allí con velocidad baja
                        Pair<Double, Double> randPositions = getRandomPositionInCell();
                        p.setWanderTarget(randPositions.getLeft(), randPositions.getRight(), this.currentTime);
                    }
                    targetX = p.getWanderTargetX();
                    targetY = p.getWanderTargetY();
                    p.setVdMax(Constants.ZOMBIE_SEARCH_SPEED);
                } else {
                    targetX = other.getX();
                    targetY = other.getY();
                    p.setVdMax(this.zombieDesiredVelocity);
                    p.setWanderTarget(null, null, this.currentTime);
                }
            } else {
                int nearZombiesQty = population.stream()
                        .filter(particle -> particle.getState().equals(ParticleState.ZOMBIE))
                        .filter(particle -> particle.calculateDistanceTo(p) <= Constants.HUMAN_SEARCH_RADIUS)
                        .collect(Collectors.toList()).size();
                if (nearZombiesQty == 0) {
                    // si no hay zombies cerca no se mueve
                    p.setVdMax(0);
                    targetX = targetY = 0.;
                } else {
                    Pair<Double, Double> target = calculateTargetHeuristic(p);
                    targetX = target.getLeft();
                    targetY = target.getRight();
                    p.setVdMax(Constants.HUMAN_DESIRED_VELOCITY);
                }
            }
            p.velocityUpdate(false, targetX, targetY);
        }

    }

    private Pair<Double, Double> heuristicAgainstSquareWall(Particle p) {
        double nx = 0., ny = 0., distance, absDistanceX, absDistanceY, distanceX = 0, distanceY = 0;
        Integer coeffIndex;
        absDistanceX = Math.abs(p.getX() + Constants.HUMAN_SEARCH_RADIUS);
        absDistanceY = Math.abs(p.getY() + Constants.HUMAN_SEARCH_RADIUS);

        if(absDistanceX >= this.cellHalfLength || absDistanceY >= this.cellHalfLength) {
            // llega almenos una pared
            if(Math.abs(p.getX()) > Math.abs(p.getY())) {
                coeffIndex = (int) ((absDistanceX/11)*90);
                // esta cerca de la pared superior o la inferior
                if(p.getX() > 0) {
                  //pared superior, toma una de las primeros 90 coeficientes, no hago nada
                    distanceX = this.cellHalfLength - p.getX();
                } else {
                    //pared inferior, toma entre 180 y 270
                    coeffIndex += 180;
                    distanceX = -this.cellHalfLength - p.getX();
                }
            } else {
                coeffIndex = (int) ((absDistanceY/11)*90);
                if(p.getY() > 0) {
                    // toma entre 90 y 180
                    coeffIndex += 90;
                    distanceY = this.cellHalfLength - p.getY();
                } else {
                    // toma entre 270 y 360
                    coeffIndex += 270;
                    distanceY = -this.cellHalfLength - p.getY();
                }
            }
            distance = Math.sqrt(Math.pow(absDistanceX, 2) + Math.pow(absDistanceY, 2));
            double wallWeight = wallAps.get(coeffIndex) *
                    Math.exp(-distance / wallBps.get(coeffIndex));

            // uno de los versores vale cero, que es el mas lejano a la pared
            double ex = (distanceX) / distance;
            nx += ex * wallWeight;
            double ey = (distanceY) / distance;
            ny += ey * wallWeight;
        }

        return new Pair<>(nx, ny);
    }

    private Pair<Double, Double> heuristicAgainstCircularWall(Particle p) {
        double nx = 0., ny = 0.;
        double distanceToOrigin = p.distanceToOrigin();
        if (this.cellHalfLength - distanceToOrigin < Constants.HUMAN_SEARCH_RADIUS) {
            // se encuentra cerca de la pared, debe alejarse
            double closestWallX = (p.getX() / distanceToOrigin) * this.cellHalfLength;
            double closestWallY = (p.getY() / distanceToOrigin) * this.cellHalfLength;
            double distanceToWall = p.calculateDistanceToWithoutRadius(closestWallX, closestWallY);
            // busco los coeficientes asociados al punto mas cercano en la pared
            int wallCoefficientIndex = (int) (Math.toDegrees(Math.atan2(closestWallX, closestWallY))) + 180;

            double wallWeight = wallAps.get(wallCoefficientIndex) *
                    Math.exp(-distanceToWall / wallBps.get(wallCoefficientIndex));

            double xDiff = p.getX() - closestWallX;
            double ex = (xDiff) / distanceToWall;
            nx += ex * wallWeight;

            double yDiff = p.getY() - closestWallY;
            double ey = (yDiff) / distanceToWall;
            ny += ey * wallWeight;
        }

        return new Pair<>(nx, ny);
    }

    private Pair<Double, Double> calculateTargetHeuristic(Particle p) {
        double nx = 0., ny = 0.;

        Pair<Double, Double> n;
        // contra pared
        if(this.isSquareCell) {
            n = heuristicAgainstSquareWall(p);
        }else {
            n = heuristicAgainstCircularWall(p);
        }
        nx += n.getLeft();
        ny += n.getRight();

        // contra vecinos
        List<Particle> neighbours = population.stream()
                .filter(other -> p.calculateDistanceTo(other) < Constants.HUMAN_SEARCH_RADIUS && !p.equals(other))
                .collect(Collectors.toList());

        for (Particle neighbour : neighbours) {
            double distanceToNeighbour = p.calculateDistanceToWithoutRadius(neighbour.getX(), neighbour.getY());

            double neighbourWeight = neighbour.getAP() * Math.exp(-distanceToNeighbour / neighbour.getBP());
            double xDiff = p.getX() - neighbour.getX();
            double ex = (xDiff) / distanceToNeighbour;
            nx += ex * neighbourWeight;

            double yDiff = p.getY() - neighbour.getY();
            double ey = (yDiff) / distanceToNeighbour;
            ny += ey * neighbourWeight;
        }
        double abs = Math.sqrt(nx * nx + ny * ny);
        nx /= abs;
        ny /= abs;

        if ((!this.isSquareCell && Math.sqrt(Math.pow(p.getX() + nx, 2) + Math.pow(p.getY() + ny, 2)) > this.cellHalfLength) ||
                (this.isSquareCell && (Math.abs(p.getX()) > this.cellHalfLength || Math.abs(p.getY()) > this.cellHalfLength))) {
            // el target esta fuera del recinto, lo roto para que no se choque con la pared
            double angle = -Math.PI / 2;
            nx = (nx * Math.cos(angle)) - (ny * Math.sin(angle));
            ny = (nx * Math.sin(angle)) + (ny * Math.cos(angle));
        }

        return new Pair<>(nx + p.getX(), ny + p.getY());
    }

    public void nextIteration() {
        double endIterationTime = this.currentTime + this.deltaTOutput;
        while (this.currentTime <= endIterationTime && this.currentTime < Constants.MAX_TIME) {
            // Reviso si alguna infección termino y actualizo posiciones
            checkInfectionsAndUpdatePositions();

            // Veo colisiones existentes
            List<Particle> freeParticles = new ArrayList<>(); // particulas que no estan en colisión ni en infección
            Map<CollisionType, List<Pair<Particle, Particle>>> collisions = new HashMap<>();
            collisions.put(CollisionType.INFECTION, new ArrayList<>());
            collisions.put(CollisionType.PARTICLE, new ArrayList<>());
            collisions.put(CollisionType.WALL, new ArrayList<>());

            findCollisions(freeParticles, collisions);

            updateCollisionParticles(collisions);
            updateFreeParticles(freeParticles);

            this.currentTime += Population.DELTA_T;
        }
    }

    public static void createStaticFile(String outputPath, Integer initialHumansQty, Double zombieDesiredVelocity, Integer deltaTOutputMultiplier, String humanApOverZombieApCoeff) throws FileNotFoundException, UnsupportedEncodingException {
        System.out.println("\tCreating static file. . .");

        PrintWriter writer = new PrintWriter(outputPath + "/static.txt", "UTF-8");
        writer.print(String.format(Locale.ENGLISH, "%d\n%f\n%f\n%f\n%s\n%d\n", initialHumansQty, Constants.CIRCLE_RADIUS, zombieDesiredVelocity, Population.DELTA_T * deltaTOutputMultiplier, humanApOverZombieApCoeff,Constants.SIMULATION_REPETITION_TIMES));
        writer.close();

        System.out.println("\tStatic file successfully created");
    }

    private void writeOutput(PrintWriter writer) {
        writer.println(this.currentTime);
//        writer.println(Math.round(this.currentTime));
        for (Particle p : this.population) {
            writer.println(String.format(Locale.ENGLISH, "%d;%f;%f;%f;%f;%f;%d",
                    p.getId(), p.getX(), p.getY(), p.getXVelocity(), p.getYVelocity(), p.getRadius(), p.getState().ordinal()));
        }
    }

    public void createDynamicFile(String dynamicPath, String outputName) throws FileNotFoundException, UnsupportedEncodingException {
        System.out.println("\tCreating dynamic file. . .");

        PrintWriter writer = new PrintWriter(dynamicPath + "/" + outputName, "UTF-8");
        while (this.currentTime < Constants.MAX_TIME && !areAllZombies()) {
            writeOutput(writer);
            nextIteration();
//            System.out.println("\t\t" + this.zombiesQty + "/" + (this.initialHumansQty + 1) + " free zombies at " + this.currentTime);
        }

        // last iteration
        writeOutput(writer);

        writer.close();
        System.out.println("\tDynamic file successfully created, finished with " + this.zombiesQty + "/" + (this.initialHumansQty + 1) + " zombies at " + this.currentTime);
    }
}
