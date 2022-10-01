import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class IntegrationAlgorithm {
    static double[] alfas = {3./16, 251./360,1, 11./18, 1./6, 1./60};

    public enum Type {
        EULER, BEEMAN, VERLET, GEAR
    }

    //Euler
    public static double eulerGetPosition(double currentPosition, double currentVelocity, double currentAcceleration, double deltaT) {
        return currentPosition + deltaT * eulerGetVelocity(currentVelocity, currentAcceleration, deltaT)
                + Math.pow(deltaT, 2) * currentAcceleration / 2;
    }

    public static double eulerGetVelocity(double currentVelocity, double currentAcceleration, double deltaT) {
        return currentVelocity + deltaT * currentAcceleration;
    }

    //Beeman
    public static double beemanGetPosition(double currentPosition, double currentVelocity, double currentAcceleration,
                                           double prevAcceleration, double deltaT) {
        return currentPosition + currentVelocity * deltaT +
                (2./3) * currentAcceleration * Math.pow(deltaT,2)
                -  (1./6) * prevAcceleration * Math.pow(deltaT,2);
    }

    public static double beemanGetPredictedVelocity(double currentVelocity, double currentAcceleration,
                                                     double prevAcceleration, double deltaT) {
        return currentVelocity + (3./2) * currentAcceleration * deltaT - 0.5 * prevAcceleration;
    }

    public static double beemanGetCorrectedVelocity(double currentVelocity, double nextAcceleration, double currentAcceleration,
                                                    double prevAcceleration, double deltaT) {
        return currentVelocity + (1./3) * nextAcceleration * deltaT
                + (5./6) * currentAcceleration * deltaT
                - (1./6) * prevAcceleration * deltaT;
    }

    //Verlet
    public static double verletGetPosition(double currentPosition, double prevPosition, double currentAcceleration, double deltaT) {
        return 2 * currentPosition - prevPosition + Math.pow(deltaT, 2) * currentAcceleration;
    }

    public static double verletGetVelocity(double nextPosition, double prevPosition, double deltaT) {
        return (nextPosition - prevPosition)/(2.*deltaT);
    }


    //Gear
    public static List<Double> gearGetPredictedDerivatives(Double deltaT,
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

    public static List<Double> gearGetCorrectedDerivatives(Double deltaT,
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
    private static long getFactorialOf(int n) {
        if(n == 0){
            return 1;
        }
        if (n <= 2) {
            return n;
        }
        return n * getFactorialOf(n - 1);
    }
}
