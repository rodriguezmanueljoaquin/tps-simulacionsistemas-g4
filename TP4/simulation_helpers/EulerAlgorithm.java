public class EulerAlgorithm extends IntegrationAlgorithmImp{
    private double currentAcceleration;
    public EulerAlgorithm(Double simulationDeltaT, Double outputDeltaT, Particle p) {
        super(simulationDeltaT, outputDeltaT, p);
        currentAcceleration = getForce(p.getX(), p.getxVelocity())/p.getMass();
    }

    @Override
    protected double getNewPosition() {
        return getPosition(p.getX(),p.getxVelocity(),currentAcceleration,simulationDeltaT);
    }

    @Override
    protected double getNewVelocity() {
        double newPosition = getNewPosition();
        double newVelocity = getVelocity(p.getxVelocity(),currentAcceleration,simulationDeltaT);
        currentAcceleration = getForce(newPosition, newVelocity)/p.getMass();
        return newVelocity;
    }

    private double getPosition(double currentPosition, double currentVelocity, double currentAcceleration, double deltaT){
        return currentPosition + deltaT * getVelocity(currentVelocity,currentAcceleration,deltaT) + Math.pow(deltaT,2)*currentAcceleration/2;
    }

    private double getVelocity(double currentVelocity, double currentAcceleration, double deltaT){
        return currentVelocity + deltaT * currentAcceleration;
    }

}
