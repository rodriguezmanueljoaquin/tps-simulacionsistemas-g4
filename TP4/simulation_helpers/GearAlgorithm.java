import java.util.ArrayList;
import java.util.List;

public class GearAlgorithm extends IntegrationAlgorithmImp{
    private double currentAcceleration;
    private List<Double> posCorrectedDerivatives;
    private final double[] alfas = {3./16, 251./360,1, 11./18, 1./6, 1./60};

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
        List<Double> predictedDerivatives = getPredictedDerivatives(simulationDeltaT,posDerivativeEvaluations);
        //Luego, calculamos la aceleracion en el proximo paso, a partir de la posicion y la velocidad predichas
        double nextAcceleration = getForce(predictedDerivatives.get(0),predictedDerivatives.get(1))/p.getMass();
        //Luego, a partir de la misma, calculamos las derivadas corregidas
        posCorrectedDerivatives = getCorrectedDerivatives(simulationDeltaT,predictedDerivatives,nextAcceleration);
    }

    private double getDerivative(double previousPreviousDerivative, double previousDerivative){
        return (-Constants.K*previousPreviousDerivative-Constants.GAMMA*previousDerivative)/p.getMass();
    }

    private List<Double> getPredictedDerivatives(Double deltaT,
                                                           List<Double> posDerivativeEvaluations){
        List<Double> posPredictionDerivatives = new ArrayList<>();
        for(int i = 0; i <= Constants.POS_DERIVATIVE_EVALUATIONS_QTY; i++){
            double newPrediction = 0.;

            for(int j = i; j <= Constants.POS_DERIVATIVE_EVALUATIONS_QTY; j++){
                newPrediction += Math.pow(deltaT, j-i) * posDerivativeEvaluations.get(j)/getFactorialOf(j-i);
            }

            posPredictionDerivatives.add(newPrediction);
        }

        return posPredictionDerivatives;
    }

    private List<Double> getCorrectedDerivatives(Double deltaT,
                                                           List<Double> posPredictionsDerivative, Double nextAcceleration) {
        double deltaR2 = (nextAcceleration - posPredictionsDerivative.get(2)) * Math.pow(deltaT, 2) / getFactorialOf(2);

        List<Double> posCorrectedDerivatives = new ArrayList<>();
        for(int i = 0; i <= Constants.POS_DERIVATIVE_EVALUATIONS_QTY; i++){
            double newCorrected = posPredictionsDerivative.get(i) + alfas[i] * deltaR2 * (getFactorialOf(i)/Math.pow(deltaT, i));

            posCorrectedDerivatives.add(newCorrected);
        }

        return posCorrectedDerivatives;
    }

    // UTILS
    private long getFactorialOf(int n) {
        if(n == 0){
            return 1;
        }
        if (n <= 2) {
            return n;
        }
        return n * getFactorialOf(n - 1);
    }

}
