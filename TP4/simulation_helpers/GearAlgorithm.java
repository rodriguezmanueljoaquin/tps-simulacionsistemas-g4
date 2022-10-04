import java.util.ArrayList;
import java.util.List;

public class GearAlgorithm extends IntegrationAlgorithmImp{
    private double currentAcceleration;
    private List<Double> posCorrectedDerivatives;

    public GearAlgorithm(Double simulationDeltaT, Double outputDeltaT, Particle p) {
        super(simulationDeltaT,outputDeltaT,p);
        currentAcceleration = getForce(p.getX(), p.getxVelocity())/p.getMass();
        posCorrectedDerivatives = new ArrayList<>();
    }

    @Override
    protected double getNewPosition() {
        calculateCorrectedDerivatives();
        return posCorrectedDerivatives.get(0);
    }

    @Override
    protected double getNewVelocity() {
        double newVelocity = posCorrectedDerivatives.get(1);
        currentAcceleration = getForce(posCorrectedDerivatives.get(0), newVelocity)/p.getMass();
        return newVelocity;
    }

    //Calcula las derivadas actuales para realizar el algoritmo
    private List<Double> calculateCurrentPosDerivatives(){
        //Creamos la lista con las derivadas
        List<Double> posDerivativeEvaluations = new ArrayList<>();
        //R
        posDerivativeEvaluations.add(p.getX());
        //R1
        posDerivativeEvaluations.add(p.getxVelocity());
        //R2
        posDerivativeEvaluations.add(currentAcceleration);
        //R3
        posDerivativeEvaluations.add(getDerivative(posDerivativeEvaluations.get(1),posDerivativeEvaluations.get(2)));
        //R4
        posDerivativeEvaluations.add(getDerivative(posDerivativeEvaluations.get(2),posDerivativeEvaluations.get(3)));
        //R5
        posDerivativeEvaluations.add(getDerivative(posDerivativeEvaluations.get(3),posDerivativeEvaluations.get(4)));
        //Retornamos la lista
        return posDerivativeEvaluations;
    }

    private void calculateCorrectedDerivatives(){
        //Primero, calculamos las derivadas actuales de la posicion
        List<Double> posDerivativeEvaluations = calculateCurrentPosDerivatives();
        //Luego, obtenemos las derivadas predichas a partir del algoritmo
        List<Double> predictedDerivatives = IntegrationAlgorithm.gearGetPredictedDerivatives(simulationDeltaT,posDerivativeEvaluations);
        //Luego, calculamos la aceleracion en el proximo paso, a partir de la posicion y la velocidad predichas
        double nextAcceleration = getForce(predictedDerivatives.get(0),predictedDerivatives.get(1))/p.getMass();
        //Luego, a partir de la misma, calculamos las derivadas corregidas
        posCorrectedDerivatives = IntegrationAlgorithm.gearGetCorrectedDerivatives(simulationDeltaT,predictedDerivatives,nextAcceleration);
        return;
    }

    private double getDerivative(double previousPreviousDerivative, double previousDerivative){
        return (-Constants.K*previousPreviousDerivative-Constants.GAMMA*previousDerivative)/p.getMass();
    }

}
