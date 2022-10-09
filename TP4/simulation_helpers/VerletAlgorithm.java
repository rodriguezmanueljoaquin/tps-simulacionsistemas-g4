public class VerletAlgorithm extends IntegrationAlgorithmImp{
    private double currentAcceleration;
    private double prevPos;

    public VerletAlgorithm(double simulationDeltaT, double outputDeltaT, Particle p) {
        super(simulationDeltaT,outputDeltaT,p);
        currentAcceleration = getForce(p.getX(), p.getxVelocity())/p.getMass();
        IntegrationAlgorithmImp eulerIntegrationAlgorithm = new EulerAlgorithm(-simulationDeltaT, outputDeltaT, p);
        prevPos = eulerIntegrationAlgorithm.getNewPosition();
    }

    @Override
    protected double getNewPosition() {
        return getPosition(p.getX(),prevPos,currentAcceleration,simulationDeltaT);
    }

    @Override
    protected double getNewVelocity() {
        double newPosition = getNewPosition();
        double newVel = getVelocity(newPosition,prevPos,simulationDeltaT);
        prevPos = p.getX();
        currentAcceleration = getForce(newPosition, newVel)/p.getMass();
        return newVel;
    }

    private double getPosition(double currentPosition, double prevPosition, double currentAcceleration, double deltaT) {
        return 2 * currentPosition - prevPosition + Math.pow(deltaT, 2) * currentAcceleration;
    }

    private double getVelocity(double nextPosition, double prevPosition, double deltaT) {
        return (nextPosition - prevPosition)/(2.*deltaT);
    }
}
