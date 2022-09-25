
public interface IntegrationAlgorithm {

    String getName();

    Pair<Double, Double> getPosition(Double time, Double deltaT, Double currentPos, Double prevPos);

    Pair<Double, Double> getVelocity(Double time, Double deltaT);
}
