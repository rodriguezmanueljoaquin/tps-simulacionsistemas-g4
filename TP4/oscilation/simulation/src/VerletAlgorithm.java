public class VerletAlgorithm implements IntegrationAlgorithm{
    public VerletAlgorithm() {
    }

    @Override
    public String getName(){
        return "Verlet";
    }

    @Override
    public Pair<Double, Double> getPosition(Double time, Double deltaT, Double currentPos, Double prevPos) {

        return null;
    }

    @Override
    public Pair<Double, Double> getVelocity(Double time, Double deltaT) {
        return null;
    }
}
