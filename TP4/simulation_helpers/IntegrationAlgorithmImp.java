public abstract class IntegrationAlgorithmImp {
    protected Particle p;
    protected double simulationDeltaT;
    private double outputDeltaT;
    protected double currentSimulationTime;

    public IntegrationAlgorithmImp(Double simulationDeltaT, Double outputDeltaT, Particle p) {
        this.p = p;
        this.simulationDeltaT = simulationDeltaT;
        this.outputDeltaT = outputDeltaT;
    }

    protected abstract double getNewPosition();

    protected abstract double getNewVelocity();

    protected double getForce(double position, double velocity){
        return (-Constants.K * position - Constants.GAMMA * velocity);
    }
}
