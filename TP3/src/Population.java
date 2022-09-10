import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.*;

public class Population {
    private Set<Particle> particles;
    private Random rand;
    private Integer particlesQty;

    private Double height;

    private Double width;

    private Double gap;
    

    public Population(Integer particlesQty, Double width, Double height, Double gap) {
        this.particlesQty = particlesQty;
        this.particles = new HashSet<>();
        this.rand = new Random(Constants.RANDOM_SEED);
        this.width = width;
        this.height = height;
        this.gap = gap;


        for (int i = 0; i < this.particlesQty; i++) {
            particles.add(new Particle(rand.nextDouble() * this.width/2, rand.nextDouble() * this.height, rand.nextDouble() * 2 * Math.PI));
        }
    }
}
