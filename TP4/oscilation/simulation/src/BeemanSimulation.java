public class BeemanSimulation extends Simulation {
    private double currentAcceleration, previousAcceleration;
    public BeemanSimulation(Double simulationDeltaT, Double outputDeltaT) {
        super(simulationDeltaT, outputDeltaT);
        currentAcceleration = getForce(p.getX(), p.getxVelocity())/p.getMass();
        previousAcceleration = 0; //TODO: ???
    }

    @Override
    protected double getNewPosition() {
        return IntegrationAlgorithm.beemanGetPosition(p.getX(), p.getxVelocity(), currentAcceleration,
                previousAcceleration, simulationDeltaT);
    }

    @Override
    protected double getNewVelocity() {
        double predictedVelocity = IntegrationAlgorithm.beemanGetPredictedVelocity(p.getxVelocity(), currentAcceleration,
                previousAcceleration, simulationDeltaT);

        double newPosition = getNewPosition();
        double nextAccelerationPredicted = getForce(newPosition, predictedVelocity) / p.getMass();
        double newVelocity = IntegrationAlgorithm.beemanGetCorrectedVelocity(p.getxVelocity(), nextAccelerationPredicted,
                currentAcceleration, previousAcceleration, simulationDeltaT);

        previousAcceleration = currentAcceleration;
        currentAcceleration = getForce(newPosition, newVelocity)/p.getMass();

        return newVelocity;
    }
}
