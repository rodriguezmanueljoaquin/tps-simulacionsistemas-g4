import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;

public class Population {
    private Set<Particle> population;
    private Random rand;
    private Double currentTime;
    private Integer initialHumansQty;
    private Double circleRadius;
    private Double zombieDesiredVelocity;

    private Integer zombies;

    public Population(Integer initialHumansQty, Double zombieDesiredVelocity) {
        this.initialHumansQty = initialHumansQty;
        this.circleRadius = Constants.CIRCLE_RADIUS;
        this.population = population;
        this.zombies = 1;
        this.zombieDesiredVelocity = zombieDesiredVelocity;
        this.rand = new Random(Constants.RANDOM_SEED);
        this.currentTime = 0.;

        //Seteamos las posiciones iniciales de las particulas
        setParticlesInitialPosition();
    }

    private void setParticlesInitialPosition(){
        //Seteamos el zombie
        Particle zombie = new Particle(0., 0., rand.nextDouble() * 2 * Math.PI, ParticleState.ZOMBIE, this.zombieDesiredVelocity);


        //Seteamos a los humanos
        boolean validPosition;
        double newX = 0, newY = 0;
        Particle newParticle = null;
        for (int i = 0; i < this.initialHumansQty; i++) {
            validPosition = false;
            while (!validPosition){
                newX = rand.nextDouble() * circleRadius;
                newY = rand.nextDouble() * circleRadius;
                newParticle = new Particle(newX, newY, rand.nextDouble() * 2 * Math.PI, ParticleState.HUMAN, Constants.HUMAN_DESIRED_VELOCITY);
                validPosition = true;
                // Revisamos que este a la distancia minima del zombie, y que no se solape con otra particula
                if(newParticle.calculateDistanceTo(zombie) < Constants.INITIAL_MIN_DISTANCE_TO_ZOMBIE) validPosition = false;
                for (Particle other : population){
                    if(newParticle.calculateDistanceTo(other) < 0)
                        validPosition = false;
                }
            }
            population.add(newParticle);
        }
        population.add(zombie);
    }

    private boolean areAllZombies(){
        return zombies == initialHumansQty + 1;
    }

    public void nextIteration() {
        // Reviso si alguna infección termino
        for (Particle p : population) {
            if (p.getState() == ParticleState.HUMAN_INFECTED || p.getState() == ParticleState.ZOMBIE_INFECTING) {
                if (p.getZombieContactTime() + Constants.INFECTION_DURATION <= currentTime){
                    p.setState(ParticleState.ZOMBIE);
                }
            }
        }

        for (Particle p : population){
            // Actualizo para aquellas que no estan en situación de contacto
            if (p.getState() != ParticleState.HUMAN_INFECTED && p.getState() != ParticleState.ZOMBIE_INFECTING){
                //Primero, actualizamos sus posiciones
                p.updatePosition(Constants.DELTA_T);
                //Vemos si choco contra otra particula
                boolean collision = false;
                for (Particle other : population){
                    if(!other.equals(p)){
                        if(p.calculateDistanceTo(other) <= 0){
                            //En caso de ser choque humano - zombie, establecemos la situacion de contacto
                            checkHumanInfected(p, other);
                            //Registramos que hubo una colision
                            // TODO: Actualizar segun colision con other
                            collision = true;
                        }
                    }
                }

                // Vemos si choco contra una pared
                if(p.distanceToOrigin() <= this.circleRadius){
                    collision = true;
                    // other estaria en el mismo eje de acuerdo al origen pero más lejos
                    p.velocityUpdate(true, p.getX() *2, p.getY() *2);
                }

                if(!collision){
                    //TODO: Buscar zombie más cercano y mandar sus posiciones?
                    p.velocityUpdate(false, 0,0);
                }
            }
        }

        this.currentTime += Constants.DELTA_T;
    }

    private void checkHumanInfected(Particle supposedHuman, Particle other){
        if(supposedHuman.getState()== ParticleState.HUMAN &&
                (other.getState() == ParticleState.ZOMBIE || other.getState() == ParticleState.ZOMBIE_INFECTING)){
            supposedHuman.setState(ParticleState.HUMAN_INFECTED);
            supposedHuman.setZombieContactTime(currentTime);
            supposedHuman.setXVelocity(0);
            supposedHuman.setYVelocity(0);
            other.setState(ParticleState.ZOMBIE_INFECTING);
            other.setZombieContactTime(currentTime);
            other.setXVelocity(0);
            other.setYVelocity(0);
        }
    }

    public static void createStaticFile(String outputName, Integer initialHumansQty, Double zombieDesiredVelocity) throws FileNotFoundException, UnsupportedEncodingException {
        System.out.println("\tCreating static file. . .");

        PrintWriter writer = new PrintWriter("./results/" + outputName + "/static.txt", "UTF-8");
        writer.print(String.format(Locale.ENGLISH, "%d\n%f\n%f\n", initialHumansQty, Constants.CIRCLE_RADIUS, zombieDesiredVelocity));
        writer.close();

        System.out.println("\tStatic file successfully created");
    }

    public void createDynamicFile(String dynamicPath) throws FileNotFoundException, UnsupportedEncodingException {
        System.out.println("\tCreating dynamic file. . .");

        PrintWriter writer = new PrintWriter(dynamicPath, "UTF-8");
        while (this.currentTime < Constants.MAX_TIME && !areAllZombies()){
            writer.println(this.currentTime);
            for(Particle p: population) {
                boolean isZombie = true;
                if(p.getState() == ParticleState.HUMAN || p.getState() == ParticleState.HUMAN_INFECTED)
                    isZombie = false;
                writer.println(String.format(Locale.ENGLISH, "%d;%f;%f;%f;%f;%d",
                        p.getId(), p.getX(), p.getY(), p.getXVelocity(), p.getYVelocity(), isZombie? 1:0));
            }
            nextIteration();
        }
        writer.close();

        System.out.println("\tDynamic file successfully created");
    }
}
