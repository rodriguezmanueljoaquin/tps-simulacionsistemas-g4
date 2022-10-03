public class VerletAlgorithm extends IntegrationAlgorithmImp{

    private double currentAcceleration;
    private double prevPos;


    public VerletAlgorithm(double simulationDeltaT, double outputDeltaT, Particle p) {
        super(simulationDeltaT,outputDeltaT,p);
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
