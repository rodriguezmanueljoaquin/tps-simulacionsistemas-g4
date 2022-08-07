
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public Pair<Map<Particle, List<Particle>>, Long> getNeighbours(){

        Map<Particle, List<Particle>> neighbours = new HashMap<>();

        return new Pair<>(neighbours, System.currentTimeMillis() - startExecutionTime);
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
