import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;

public class Population {
    private Set<Particle> population;
    private Random rand;
    private Double currentTime, circleRadius, zombieDesiredVelocity;
    private Integer initialHumansQty, zombiesQty;

    public Population(Integer initialHumansQty, Double zombieDesiredVelocity, long seed) {
        this.initialHumansQty = initialHumansQty;
        this.circleRadius = Constants.CIRCLE_RADIUS;
        this.population = new HashSet<>();
        this.zombiesQty = 1;
        this.zombieDesiredVelocity = zombieDesiredVelocity;
        this.rand = new Random(seed);
        this.currentTime = 0.;

        //Seteamos las posiciones iniciales de las particulas
        setParticlesInitialPosition();
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
                newX = this.rand.nextDouble() * this.circleRadius;
                newY = this.rand.nextDouble() * this.circleRadius;
                newParticle = new Particle(newX, newY, this.rand.nextDouble() * 2 * Math.PI, ParticleState.HUMAN, Constants.HUMAN_DESIRED_VELOCITY);
                validPosition = true;
                // Revisamos que este a la distancia minima del zombie, y que no se solape con otra particula
                if (newParticle.calculateDistanceTo(zombie) < Constants.INITIAL_MIN_DISTANCE_TO_ZOMBIE)
                    validPosition = false;
                for (Particle other : this.population) {
                    if (newParticle.calculateDistanceTo(other) < 0)
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

    public void nextIteration() {
        // Reviso si alguna infección termino
        for (Particle p : this.population) {
            if (p.getState() == ParticleState.HUMAN_INFECTED || p.getState() == ParticleState.ZOMBIE_INFECTING) {
                if (p.getZombieContactTime() + Constants.INFECTION_DURATION <= this.currentTime) {
                    p.setState(ParticleState.ZOMBIE);
                }
            }
        }

        for (Particle p : this.population) {
            // Actualizo para aquellas que no estan en situación de contacto
            if (p.getState() != ParticleState.HUMAN_INFECTED && p.getState() != ParticleState.ZOMBIE_INFECTING) {
                //Primero, actualizamos sus posiciones
                p.updatePosition(Constants.DELTA_T);
                //Vemos si choco contra otra particula
                boolean collision = false;
                for (Particle other : this.population) {
                    if (!other.equals(p)) {
                        if (p.calculateDistanceTo(other) <= 0) {
                            //En caso de ser choque humano - zombie, establecemos la situacion de contacto
                            checkHumanInfected(p, other);
                            //Registramos que hubo una colision
                            collision = true;
                            p.velocityUpdate(true, other.getX(), other.getY(), null);
                        }
                    }
                }

                // Vemos si choco contra una pared
                if (p.distanceToOrigin() <= this.circleRadius) {
                    collision = true;
                    // other estaria en el mismo eje de acuerdo al origen pero más lejos
                    p.velocityUpdate(true, p.getX() * 2, p.getY() * 2, null);
                }

                if (!collision) {
                    double targetX = 0, targetY = 0;
                    Double velocity = null;
                    if (p.getState() == ParticleState.ZOMBIE) {
                        // Busca al humano mas cercano a menos de 4m
                        Particle other = population.stream()
                                .filter(particle -> particle.getState().equals(ParticleState.HUMAN))
                                .filter(particle -> particle.calculateDistanceTo(p) < Constants.ZOMBIE_SEARCH_RADIUS)
                                .min(Comparator.comparing(particle -> particle.calculateDistanceTo(p))).orElse(null);
                        if (other == null) {
                            // si no hay humano a menos de 4 metros, toma un objetivo random y va hacia allí con velocidad baja
                            targetX = this.rand.nextDouble() * this.circleRadius;
                            targetY = this.rand.nextDouble() * this.circleRadius;
                            velocity = Constants.ZOMBIE_SEARCH_SPEED;
                        } else {
                            targetX = other.getX();
                            targetY = other.getY();
                        }
                    } else {
                        // Busca el zombie más cercano y lo evita
                        Particle other = population.stream()
                                .filter(particle -> particle.getState().equals(ParticleState.ZOMBIE))
                                .min(Comparator.comparing(particle -> particle.calculateDistanceTo(p))).orElse(p);
                        targetX = other.getX();
                        targetY = other.getY();
                    }

                    p.velocityUpdate(false, targetX, targetY, velocity);
                }
            }
        }

        this.currentTime += Constants.DELTA_T;
    }

    private void checkHumanInfected(Particle supposedHuman, Particle other) {
        if (supposedHuman.getState() == ParticleState.HUMAN &&
                (other.getState() == ParticleState.ZOMBIE || other.getState() == ParticleState.ZOMBIE_INFECTING)) {
            supposedHuman.setState(ParticleState.HUMAN_INFECTED);
            supposedHuman.setZombieContactTime(this.currentTime);
            supposedHuman.setXVelocity(0);
            supposedHuman.setYVelocity(0);
            other.setState(ParticleState.ZOMBIE_INFECTING);
            other.setZombieContactTime(this.currentTime);
            other.setXVelocity(0);
            other.setYVelocity(0);
        }
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
