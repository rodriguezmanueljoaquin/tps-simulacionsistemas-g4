import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CellIndexMethod {
    public static Map<Particle, Set<Particle>> getNeighboursCellIndexMethod(Set<Particle> particles) {
        int cellsQuantity = getMaxCellsQuantity();
        List<List<List<Particle>>> matrix = setMatrix(particles, cellsQuantity);
        Map<Particle, Set<Particle>> neighbours = new HashMap<>();
        Set<Particle> newNeighbours;

        for (int i = 0; i < cellsQuantity; i++) {
            for (int j = 0; j < cellsQuantity; j++) {
                for (Particle particle : matrix.get(i).get(j)) {
                    newNeighbours = getParticleNeighboursCellIndexMethod(particle, matrix, cellsQuantity, true);
                    newNeighbours.forEach(p -> {
                        neighbours.putIfAbsent(p, new TreeSet<>());
                        neighbours.get(p).add(particle);
                    });
                    neighbours.putIfAbsent(particle, new TreeSet<>());
                    neighbours.get(particle).addAll(newNeighbours);
                }
            }
        }

        return neighbours;
    }
    private static Set<Particle> getParticleNeighboursCellIndexMethod(Particle particle, List<List<List<Particle>>> matrix, int cellsQuantity, boolean periodicConditions) {
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
                    .filter(IsANeighbour(particle, periodicConditions))
                    .collect(Collectors.toSet()));
        }

        return neighbours;
    }

    private static List<List<List<Particle>>> setMatrix(Set<Particle> particlesToInsert, int cellsQuantity) {
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

    private static Pair<Integer, Integer> getParticleCell(Particle particle, int cellsQuantity) {
        double cellLength = (double) Constants.BOX_LENGTH / cellsQuantity;

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

    private static Predicate<Particle> IsANeighbour(Particle particle, boolean periodicConditions){
        return other -> !other.equals(particle) && (
                (!periodicConditions && particle.calculateDistanceTo(other) < (Double) Constants.NEIGHBOUR_RADIUS) ||
                        (periodicConditions && particle.calculateDistancePeriodicTo(other, Constants.BOX_LENGTH) < (Double) Constants.NEIGHBOUR_RADIUS)
        );
    }


    private static int getMaxCellsQuantity(){
        double cellsQuantityLimit = Constants.BOX_LENGTH/(Constants.NEIGHBOUR_RADIUS); //c
        int currentCellsQuantity = (int) Math.floor(cellsQuantityLimit);
        //If c is an int, M = c - 1
        if(cellsQuantityLimit == Math.floor(cellsQuantityLimit) && !Double.isInfinite(cellsQuantityLimit)){
            currentCellsQuantity = (int) cellsQuantityLimit - 1;
        }
        return currentCellsQuantity;
    }
}
