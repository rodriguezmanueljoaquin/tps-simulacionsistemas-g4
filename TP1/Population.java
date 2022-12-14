
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Population {
    private final Double neighbourRadius;
    private final List<Particle> particles;
    private final int boxLength;

    public Population(List<Particle> particles, Double neighbourRadius, int boxLength) {
        this.particles = particles;
        this.neighbourRadius = neighbourRadius;
        this.boxLength = boxLength;
    }

    public Pair<Map<Integer, Set<Particle>>, Long> getResultsBruteForceMethod(boolean periodicConditions) {
        Long startTime = System.nanoTime();
        return new Pair<>(
                getNeighboursBruteForceMethod(periodicConditions),
                System.nanoTime() - startTime);
    }

    private Map<Integer, Set<Particle>> getNeighboursBruteForceMethod(boolean periodicConditions) {
        Map<Integer, Set<Particle>> neighbours = new HashMap<>();



        particles.forEach(particle -> {
            Supplier<TreeSet<Particle>> treeSetSupplier = () -> new TreeSet<Particle>();
            TreeSet<Particle> particleNeighbours = particles.stream()
                    .filter(NeighbourPredicates.IsANeighbour(particle, neighbourRadius, boxLength, periodicConditions, false))
                    .collect(Collectors.toCollection(treeSetSupplier));

            neighbours.put(particle.getId(), particleNeighbours);
        });

        return neighbours;
    }

    public Pair<Map<Integer, Set<Particle>>, Long> getResultsCellIndexMethod(int cellsQuantity, boolean periodicConditions) {
        Long startTime = System.nanoTime();
        return new Pair<>(
                getNeighboursCellIndexMethod(cellsQuantity, periodicConditions),
                System.nanoTime() - startTime);
    }

    private Map<Integer, Set<Particle>> getNeighboursCellIndexMethod(int cellsQuantity, boolean periodicConditions) {
        List<List<List<Particle>>> matrix = setMatrix(particles, cellsQuantity);
        Map<Integer, Set<Particle>> neighbours = new HashMap<>();

        Set<Particle> newNeighbours;

        for (int i = 0; i < cellsQuantity; i++) {
            for (int j = 0; j < cellsQuantity; j++) {
                for (Particle particle : matrix.get(i).get(j)) {
                    newNeighbours = getParticleNeighboursCellIndexMethod(particle, matrix, cellsQuantity, periodicConditions);
                    newNeighbours.forEach(p -> {
                        neighbours.putIfAbsent(p.getId(), new TreeSet<>());
                        neighbours.get(p.getId()).add(particle);
                    });
                    neighbours.putIfAbsent(particle.getId(), new TreeSet<>());
                    neighbours.get(particle.getId()).addAll(newNeighbours);
                }
            }
        }

        return neighbours;
    }

    private Set<Particle> getParticleNeighboursCellIndexMethod(Particle particle, List<List<List<Particle>>> matrix, int cellsQuantity, boolean periodicConditions) {
        Set<Particle> neighbours = new TreeSet<>();
        List<Pair<Integer, Integer>> neighbourCells = new ArrayList<>();
        Pair<Integer, Integer> position = getParticleCell(particle, cellsQuantity);

        //Getting neighbours cells
        neighbourCells.add(position); //(0,0)
        neighbourCells.add(new Pair<>(position.getLeft() + 1, position.getRight())); //(1,0)
        neighbourCells.add(new Pair<>(position.getLeft() + 1, position.getRight() + 1)); //(1,1)
        neighbourCells.add(new Pair<>(position.getLeft(), position.getRight() + 1)); //(0,1)
        neighbourCells.add(new Pair<>(position.getLeft() - 1, position.getRight() + 1)); //(-1,1)

        if (periodicConditions) {
            neighbourCells = neighbourCells.stream()
                    .map(cell -> cell.setNewValues(Math.floorMod(cell.getLeft(), cellsQuantity), Math.floorMod(cell.getRight(), cellsQuantity)))
                    .collect(Collectors.toList());
        } else {
            neighbourCells = neighbourCells.stream()
                    .filter(cell -> cell.getLeft() >= 0 && cell.getRight() >= 0 && cell.getLeft() < cellsQuantity && cell.getRight() < cellsQuantity)
                    .collect(Collectors.toList());
        }

        //Get neighbours
        for (Pair<Integer, Integer> cell : neighbourCells) {
            neighbours.addAll(matrix.get(cell.getLeft()).get(cell.getRight()).stream()
                    .filter(NeighbourPredicates.IsANeighbour(particle, neighbourRadius, boxLength, periodicConditions, true))
                    .collect(Collectors.toSet()));
        }

        return neighbours;
    }

    private List<List<List<Particle>>> setMatrix(List<Particle> particlesToInsert, int cellsQuantity) {
        List<List<List<Particle>>> matrix = new ArrayList<>();
        List<List<Particle>> colList;
        List<Particle> particleList;
        for (int i = 0; i < cellsQuantity; i++) {
            colList = new ArrayList<>();
            for (int j = 0; j < cellsQuantity; j++) {
                particleList = new ArrayList<>();
                colList.add(particleList);
            }
            matrix.add(colList);
        }

        Pair<Integer, Integer> cellPosition;
        for (Particle particle : particlesToInsert) {
            cellPosition = getParticleCell(particle, cellsQuantity);
            matrix.get(cellPosition.getLeft())
                    .get(cellPosition.getRight())
                    .add(particle);
        }

        return matrix;
    }

    private Pair<Integer, Integer> getParticleCell(Particle particle, int cellsQuantity) {
        double cellLength = (double) boxLength / cellsQuantity;

        int row = 0;
        int col = 0;

        if (particle.getX() != 0) {
            col = ((Double) (particle.getX() / cellLength)).intValue();
        }
        if (particle.getY() != 0) {
            row = ((Double) (particle.getY() / cellLength)).intValue();
        }

        return new Pair<>(row, col);
    }

    @Override
    public String toString() {
        return "Population{" +
                "neighbourRadius=" + neighbourRadius +
                ", particles=" + particles +
                ", boxLength=" + boxLength +
                '}';
    }
}
