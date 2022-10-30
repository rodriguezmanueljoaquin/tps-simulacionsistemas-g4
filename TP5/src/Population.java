import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;

public class Population {
    private List<Particle> population;
    private Random rand;
    private Double currentTime, circleRadius, zombieDesiredVelocity;
    private Integer initialHumansQty, zombiesQty;

    public Population(Integer initialHumansQty, Double zombieDesiredVelocity, long seed) {
        this.initialHumansQty = initialHumansQty;
        this.circleRadius = Constants.CIRCLE_RADIUS;
        this.population = new ArrayList<>();
        this.zombiesQty = 1;
        this.zombieDesiredVelocity = zombieDesiredVelocity;
        this.rand = new Random(seed);
        this.currentTime = 0.;

        //Seteamos las posiciones iniciales de las particulas
        setParticlesInitialPosition();
    }

    private Pair<Double, Double> getRandomPositionInCircle() {
        double angle = rand.nextDouble() * Math.PI * 2;
        double newX = Math.cos(angle) * rand.nextDouble() * this.circleRadius;
        double newY = Math.sin(angle) * rand.nextDouble() * this.circleRadius;

        return new Pair<>(newX, newY);
    }

    private void setParticlesInitialPosition() {
        //Seteamos el zombie
        Particle zombie = new Particle(0., 0., this.rand.nextDouble() * 2 * Math.PI, ParticleState.ZOMBIE, this.zombieDesiredVelocity);

        //Seteamos a los humanos
        boolean validPosition;
        double newX = 0, newY = 0;
        Particle newParticle = null;
        for (int i = 0; i < this.initialHumansQty; i++) {
            validPosition = false;
            while (!validPosition) {
                Pair<Double, Double> randPositions = getRandomPositionInCircle();
                newParticle = new Particle(randPositions.getLeft(), randPositions.getRight(), this.rand.nextDouble() * 2 * Math.PI, ParticleState.HUMAN, Constants.HUMAN_DESIRED_VELOCITY);
                validPosition = true;
                // Revisamos que este a la distancia minima del zombie, y que no se solape con otra particula
                if (newParticle.calculateDistanceTo(zombie) < Constants.INITIAL_MIN_DISTANCE_TO_ZOMBIE)
                    validPosition = false;
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
        if (Math.abs(p.distanceToOrigin()) <= this.circleRadius) {
            // actualizamos velocidad
            return null;
        }
        // other estaria en el mismo eje de acuerdo al origen pero más lejos
        return new Particle(p.getX() * 2, p.getY() * 2,0,ParticleState.WALL, 0.);
    }

    private boolean isInInfection(ParticleState state) {
        return state == ParticleState.HUMAN_INFECTED || state == ParticleState.ZOMBIE_INFECTING;
    }

    private void checkInfections() {
        for (Particle p : this.population) {
            if (p.getState() == ParticleState.HUMAN_INFECTED || p.getState() == ParticleState.ZOMBIE_INFECTING) {
                if (p.getZombieContactTime() + Constants.INFECTION_DURATION <= this.currentTime) {
                    System.out.println("Particle " + p.getId() + " ended infection");
                    p.setState(ParticleState.ZOMBIE);
                }
            }
        }
    }

    private boolean isHumanAgainstZombieCollision(Particle supposedHuman, Particle supposedZombie) {
        return (supposedHuman.getState() == ParticleState.HUMAN &&
                (supposedZombie.getState() == ParticleState.ZOMBIE || supposedZombie.getState() == ParticleState.ZOMBIE_INFECTING));
    }

    private void findCollisions(List<Particle> freeParticles, Map<CollisionType, List<Pair<Particle, Particle>>> collisions) {
        for (int i = 0; i < this.population.size(); i++) {
            Particle p = this.population.get(i);
            Particle wallCollision = checkWallCollision(p);
            if(wallCollision != null && !isInInfection(p.getState()))
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
                    }else
                        collisions.get(CollisionType.PARTICLE). add(new Pair<>(p, other));
                }
            }
            if(wallCollision == null && !particleCollision && !isInInfection(p.getState()))
                freeParticles.add(p);
        }
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
            p.velocityUpdate(true, wall.getX(), wall.getY(), null);
            p.radiusUpdate(true);
        }

        // Choques entre particulas
        for (Pair<Particle, Particle> particles : collisions.get(CollisionType.PARTICLE)) {
            Particle p1 = particles.getLeft();
            Particle p2 = particles.getRight();

            // Actualizar solo las que no estan en infección
            if(!isInInfection(p1.getState())) {
                p1.velocityUpdate(true, p2.getX(), p2.getY(), null);
                p1.radiusUpdate(true);
            }
            if(!isInInfection(p2.getState())) {
                p2.velocityUpdate(true, p1.getX(), p1.getY(), null);
                p2.radiusUpdate(true);
            }
        }
    }
    
    private void updateFreeParticles(List<Particle> freeParticles) {
        for (Particle p : freeParticles) {
            Double velocity = null, targetX, targetY;

            if (p.getState() == ParticleState.ZOMBIE) {
                // Busca al humano mas cercano a menos de 4m
                Particle other = population.stream()
                        .filter(particle -> particle.getState().equals(ParticleState.HUMAN))
                        .filter(particle -> particle.calculateDistanceTo(p) <= Constants.ZOMBIE_SEARCH_RADIUS)
                        .min(Comparator.comparing(particle -> particle.calculateDistanceTo(p))).orElse(null);

                // Si no hay humano cercano, va hacia el target fijo que ya tenia en caso de que lo haya calculado
                if (other == null) {
                    if (!p.hasWanderTarget() || p.reachedWanderTarget()) {
                        // si no hay humano a menos de 4 metros, toma un objetivo random y va hacia allí con velocidad baja
                        velocity = Constants.ZOMBIE_SEARCH_SPEED;
                        Pair<Double, Double> randPositions = getRandomPositionInCircle();
                        p.setWanderTarget(randPositions.getLeft(), randPositions.getRight());
                    }
                    targetX = p.getWanderTargetX();
                    targetY = p.getWanderTargetY();
                } else {
                    targetX = other.getX();
                    targetY = other.getY();
                    p.setWanderTarget(null, null);
                }
            } else {
                // Busca el zombie más cercano y lo evita
                Particle other = population.stream()
                        .filter(particle -> particle.getState().equals(ParticleState.ZOMBIE) || particle.getState().equals(ParticleState.ZOMBIE_INFECTING))
                        .min(Comparator.comparing(particle -> particle.calculateDistanceTo(p))).orElse(p);
                targetX = other.getX();
                targetY = other.getY();
            }
            p.velocityUpdate(false, targetX, targetY, velocity);
            p.radiusUpdate(false);
        }

    }
    
    public void nextIteration() {
        // Reviso si alguna infección termino
        checkInfections();

        // Actualizo posiciones
        for (Particle p : this.population)
            // Actualizo para aquellas que no estan en situación de contacto
            if (!isInInfection(p.getState()))
                p.updatePosition(Constants.DELTA_T);

        // Veo colisiones existentes
        List<Particle> freeParticles = new ArrayList<>(); // particulas que no estan en colisión ni en infección
        Map<CollisionType, List<Pair<Particle, Particle>>> collisions = new HashMap<>();
        collisions.put(CollisionType.INFECTION, new ArrayList<>());
        collisions.put(CollisionType.PARTICLE, new ArrayList<>());
        collisions.put(CollisionType.WALL, new ArrayList<>());
        
        findCollisions(freeParticles, collisions);
        
        updateCollisionParticles(collisions);
        updateFreeParticles(freeParticles);

        this.currentTime += Constants.DELTA_T;
    }

    private void updateParticlesInInfection(Particle human, Particle zombie) {
        human.setState(ParticleState.HUMAN_INFECTED);
        human.setZombieContactTime(this.currentTime);
        human.setXVelocity(0);
        human.setYVelocity(0);
        zombie.setState(ParticleState.ZOMBIE_INFECTING);
        zombie.setZombieContactTime(this.currentTime);
        zombie.setXVelocity(0);
        zombie.setYVelocity(0);
    }

    public static void createStaticFile(String outputPath, Integer initialHumansQty, Double zombieDesiredVelocity) throws FileNotFoundException, UnsupportedEncodingException {
        System.out.println("\tCreating static file. . .");

        PrintWriter writer = new PrintWriter(outputPath + "/static.txt", "UTF-8");
        writer.print(String.format(Locale.ENGLISH, "%d\n%f\n%f\n", initialHumansQty, Constants.CIRCLE_RADIUS, zombieDesiredVelocity));
        writer.close();

        System.out.println("\tStatic file successfully created");
    }

    public void createDynamicFile(String dynamicPath, String outputName) throws FileNotFoundException, UnsupportedEncodingException {
        System.out.println("\tCreating dynamic file. . .");

        PrintWriter writer = new PrintWriter(dynamicPath + "/" + outputName, "UTF-8");
        while (this.currentTime < Constants.MAX_TIME && !areAllZombies()) {
            writer.println(this.currentTime);
            for (Particle p : this.population) {
                boolean isZombie = p.getState() != ParticleState.HUMAN && p.getState() != ParticleState.HUMAN_INFECTED;
                writer.println(String.format(Locale.ENGLISH, "%d;%f;%f;%f;%f;%f;%d",
                        p.getId(), p.getX(), p.getY(), p.getXVelocity(), p.getYVelocity(), p.getRadius(), isZombie ? 1 : 0));
            }
            nextIteration();
        }
        writer.close();

        System.out.println("\tDynamic file successfully created");
    }
}
