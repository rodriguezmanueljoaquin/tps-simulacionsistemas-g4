public class BeemanAlgorithm extends IntegrationAlgorithmImp {
    private double currentAcceleration, previousAcceleration;

    public BeemanAlgorithm(Double simulationDeltaT, Double outputDeltaT , Particle p) {
        super(simulationDeltaT, outputDeltaT,p);
        currentAcceleration = getForce(p.getX(), p.getxVelocity())/p.getMass();
        IntegrationAlgorithmImp eulerIntegrationAlgorithm = new EulerAlgorithm(-simulationDeltaT, outputDeltaT, p);
        previousAcceleration = getForce(eulerIntegrationAlgorithm.getNewPosition(), eulerIntegrationAlgorithm.getNewVelocity())/p.getMass();
    }

    @Override
    protected double getNewPosition() {
        return getPosition(p.getX(), p.getxVelocity(), currentAcceleration,
                previousAcceleration, simulationDeltaT);
    }

    @Override
    protected double getNewVelocity() {
        double predictedVelocity = getPredictedVelocity(p.getxVelocity(), currentAcceleration,
                previousAcceleration, simulationDeltaT);

        double newPosition = getNewPosition();
        double nextAccelerationPredicted = getForce(newPosition, predictedVelocity) / p.getMass();
        double newVelocity = getCorrectedVelocity(p.getxVelocity(), nextAccelerationPredicted,
                currentAcceleration, previousAcceleration, simulationDeltaT);

        previousAcceleration = currentAcceleration;
        currentAcceleration = getForce(newPosition, newVelocity)/p.getMass();

        return newVelocity;
    }

    private double getPosition(double currentPosition, double currentVelocity, double currentAcceleration,
                               double prevAcceleration, double deltaT){
        return currentPosition + currentVelocity * deltaT +
                (2./3) * currentAcceleration * Math.pow(deltaT,2)
                -  (1./6) * prevAcceleration * Math.pow(deltaT,2);
    }

    public double getPredictedVelocity(double currentVelocity, double currentAcceleration,
                                                    double prevAcceleration, double deltaT) {
        return currentVelocity + (3./2) * currentAcceleration * deltaT - 0.5 * prevAcceleration * deltaT;
    }

    public double getCorrectedVelocity(double currentVelocity, double nextAcceleration, double currentAcceleration,
                                                    double prevAcceleration, double deltaT) {
        return currentVelocity + (1./3) * nextAcceleration * deltaT
                + (5./6) * currentAcceleration * deltaT
                - (1./6) * prevAcceleration * deltaT;
    }
}
