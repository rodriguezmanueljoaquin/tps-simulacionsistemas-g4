import sun.java2d.xr.GrowableByteArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Population {
    private int cellsQuantity, boxLength;
    private Double neighbourRadius;
    private long startExecutionTime;
    private List<List<List<Particle>>> matrix;

    public Population(List<Particle> particles, int cellsQuantity, int boxLength, Double neighbourRadius) {
        this.startExecutionTime = System.currentTimeMillis();
        this.cellsQuantity = cellsQuantity;
        this.boxLength = boxLength;
        this.neighbourRadius = neighbourRadius;

        this.matrix = new ArrayList<>();
        particles.forEach(particle -> {

        });
    }

    public Pair<Map<Particle, List<Particle>>, Long> getNeighbours(){

        Map<Particle, List<Particle>> neighbours = new HashMap<>();

        return new Pair<>(neighbours, System.currentTimeMillis() - startExecutionTime);
    }
}
