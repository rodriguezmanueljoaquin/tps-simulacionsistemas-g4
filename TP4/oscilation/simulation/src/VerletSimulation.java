public class VerletSimulation extends Simulation{

    private double currentAcceleration;
    private double prevPos;
    public VerletSimulation(Double simulationDeltaT, Double outputDeltaT) {
        super(simulationDeltaT, outputDeltaT);
        currentAcceleration = getForce(p.getX(), p.getxVelocity())/p.getMass();
        prevPos = p.getX() - p.getxVelocity() *simulationDeltaT;
    }

    @Override
    protected double getNewPosition() {
        return IntegrationAlgorithm.verletGetPosition(p.getX(),prevPos,currentAcceleration,simulationDeltaT);
    }

    @Override
    protected double getNewVelocity() {
        double newPosition = getNewPosition();
        double newVel = IntegrationAlgorithm.verletGetVelocity(newPosition,prevPos,simulationDeltaT);
        prevPos = p.getX();
        currentAcceleration = getForce(newPosition, newVel)/p.getMass();
        return newVel;
    }
}
