
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Population {
    private int cellsQuantity, boxLength;
    private Double neighbourRadius;
    private long startExecutionTime;
    private List<List<List<Particle>>> matrix;
    private boolean periodicConditions = false;

    public Population(List<Particle> particles, int cellsQuantity, int boxLength, Double neighbourRadius) {
        this.startExecutionTime = System.currentTimeMillis();
        this.cellsQuantity = cellsQuantity; //M
        this.boxLength = boxLength; //L
        this.neighbourRadius = neighbourRadius;

        this.matrix = setMatrix(particles);
//        particles.forEach(particle -> {
//            Pair<Integer,Integer> cellPosition = getParticleCellInMatrix(particle);
//            List<>
//        });

    }

    public Population(List<Particle> particles, int cellsQuantity, int boxLength, Double neighbourRadius, boolean periodicConditions) {
        this.periodicConditions = periodicConditions;
        this.startExecutionTime = System.currentTimeMillis();
        this.cellsQuantity = cellsQuantity; //M
        this.boxLength = boxLength; //L
        this.neighbourRadius = neighbourRadius;

        this.matrix = setMatrix(particles);
    }

    public Pair<Map<Integer, List<Particle>>, Long> getResults(){
        return new Pair<>(getNeighbours(), System.currentTimeMillis() - startExecutionTime);
    }

    private Map<Integer, List<Particle>> getNeighbours(){

        Map<Integer, List<Particle>> neighbours = new HashMap<>();

        List<Particle> newNeighbours;

        for(int i = 0; i<cellsQuantity;i++){
            for(int j = 0; j<cellsQuantity;j++){
                 for (Particle particle : matrix.get(i).get(j)){
                     newNeighbours = getParticleNeighbours(particle);
                     newNeighbours.forEach( p -> {
                         neighbours.putIfAbsent(p.getId(),new ArrayList<>());
                         neighbours.get(p.getId()).add(particle);
                     });
                     neighbours.putIfAbsent(particle.getId(),new ArrayList<>());
                     neighbours.get(particle.getId()).addAll(newNeighbours);
                 }
            }
        }

        return neighbours;
    }

    private List<Particle> getParticleNeighbours(Particle particle){
        List<Particle> neighbours = new ArrayList<>();
        List<Pair<Integer,Integer>> neighbourCells = new ArrayList<>();
        Pair<Integer,Integer> position = getParticleCellInMatrix(particle);

        //Getting neighbours cells
        neighbourCells.add(position); //(0,0)
        neighbourCells.add(new Pair<>(position.getLeft()+1, position.getRight())); //(1,0)
        neighbourCells.add(new Pair<>(position.getLeft()+1, position.getRight()+1)); //(1,1)
        neighbourCells.add(new Pair<>(position.getLeft(), position.getRight()+1)); //(0,1)
        neighbourCells.add(new Pair<>(position.getLeft()-1, position.getRight()+1)); //(-1,1)

        if(!periodicConditions){
            neighbourCells = neighbourCells.stream().filter(p->p.getLeft()>=0 && p.getRight()>=0 && p.getLeft()<cellsQuantity && p.getRight()<cellsQuantity).collect(Collectors.toList());
        }else{
            neighbourCells = neighbourCells.stream().map(p-> p.setNewValues(Math.floorMod(p.getLeft(),cellsQuantity), Math.floorMod(p.getRight(),cellsQuantity))).collect(Collectors.toList());
        }

        //Get neighbours
        for(Pair <Integer,Integer> cell : neighbourCells){
            neighbours.addAll(matrix.get(cell.getLeft()).get(cell.getRight()).stream().filter(p -> !p.equals(particle) && particle.calculateDistanceTo(p)< neighbourRadius)
                    .collect(Collectors.toList()));
        }

        return neighbours;

    }

    private List<List<List<Particle>>> setMatrix(List<Particle> particlesToInsert){
        List<List<List<Particle>>> matrix = new ArrayList<>();
        List<List<Particle>> colList;
        List<Particle> particleList;
        for(int i =0 ; i< cellsQuantity; i++){
            colList = new ArrayList<>();
            for(int j = 0; j< cellsQuantity; j++){
                particleList = new ArrayList<>();
                colList.add(particleList);
            }
            matrix.add(colList);
        }

        Pair<Integer,Integer> cellPosition;
        for(Particle particle: particlesToInsert ){
            cellPosition = getParticleCellInMatrix(particle);
            matrix.get(cellPosition.getLeft()).get(cellPosition.getRight()).add(particle);
        }

        return matrix;
    }

    private Pair<Integer,Integer> getParticleCellInMatrix(Particle particle){
        double cellLength = (double) boxLength/cellsQuantity;

        int row = 0;
        int col = 0;

        if(particle.getX()!=0){
            col = ((Double)(particle.getX() / cellLength)).intValue();
        }
        if(particle.getY()!=0){
            row = ((Double)(particle.getY() / cellLength)).intValue();
        }

        return new Pair<>(row,col);

    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();

        for(int i=0; i<cellsQuantity;i++){
            sb.append("-----Row "+i+"-----\n");
            for(int j = 0; j<cellsQuantity; j++){
                sb.append("----Col "+j+"----\n");
                sb.append(matrix.get(i).get(j).toString()+"\n");
            }
        }

        return sb.toString();

//        return "Population{" +
//                "cellsQuantity=" + cellsQuantity +
//                ", boxLength=" + boxLength +
//                ", neighbourRadius=" + neighbourRadius +
//                ", startExecutionTime=" + startExecutionTime +
//                ", matrix=" + matrix +
//                '}';
    }
}
