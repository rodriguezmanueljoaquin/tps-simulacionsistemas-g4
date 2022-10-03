public class VerletSimulation extends Simulation{

    private double currentAcceleration;
    public VerletSimulation(Double simulationDeltaT, Double outputDeltaT) {
        super(simulationDeltaT, outputDeltaT);
        currentAcceleration = getForce(p.getX(), p.getxVelocity())/p.getMass();
    }

    @Override
    protected double getNewPosition() {
        return IntegrationAlgorithm.verletGetPosition(p.getX(),p.getxVelocity(),currentAcceleration,simulationDeltaT);
    }

    @Override
    protected double getNewVelocity() {
        double newPosition = getNewPosition();
        double prevPos = p.getX() - p.getxVelocity() *simulationDeltaT;
        return IntegrationAlgorithm.verletGetVelocity(newPosition,prevPos,simulationDeltaT);
    }
}
