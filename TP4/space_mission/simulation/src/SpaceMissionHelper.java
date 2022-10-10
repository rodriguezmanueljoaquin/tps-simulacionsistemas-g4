import java.util.List;

public class SpaceMissionHelper {

    //Metodos para actualizar posiciones y velocidad

    ///Beeman
    public static double getBeemanPosition(double currentPosition, double currentVelocity, double currentAcceleration,
                               double prevAcceleration, double deltaT){
        return currentPosition + currentVelocity * deltaT +
                (2./3) * currentAcceleration * Math.pow(deltaT,2)
                -  (1./6) * prevAcceleration * Math.pow(deltaT,2);
    }

    public static double getBeemanPredictedVelocity(double currentVelocity, double currentAcceleration,
                                       double prevAcceleration, double deltaT) {
        return currentVelocity + (3./2) * currentAcceleration * deltaT - 0.5 * prevAcceleration * deltaT;
    }

    public static double getBeemanCorrectedVelocity(double currentVelocity, double nextAcceleration, double currentAcceleration,
                                       double prevAcceleration, double deltaT) {
        return currentVelocity + (1./3) * nextAcceleration * deltaT
                + (5./6) * currentAcceleration * deltaT
                - (1./6) * prevAcceleration * deltaT;
    }

    ///Euler
    public static double getEulerPosition(double currentPosition, double currentVelocity, double currentAcceleration, double deltaT,boolean modified){
        if(modified){
            return currentPosition + deltaT * getEulerVelocity(currentVelocity,currentAcceleration,deltaT) + Math.pow(deltaT,2)*currentAcceleration/2;
        }
        return currentPosition + deltaT * currentVelocity + Math.pow(deltaT,2)*currentAcceleration/2;
    }

    public static double getEulerVelocity(double currentVelocity, double currentAcceleration, double deltaT){
        return currentVelocity + deltaT * currentAcceleration;
    }

    //Metodos para calculo de fuerza

    private static double forceGravity(Particle otherParticle, Particle currentParticle){
        return Constants.G*otherParticle.getMass()* currentParticle.getMass()/(Math.pow(otherParticle.calculateDistanceToWithoutRadius(currentParticle),2));
    }

    private static double forceX(Particle otherParticle, Particle currentParticle){
        return forceGravity(otherParticle, currentParticle)*(otherParticle.getX() - currentParticle.getX())/ otherParticle.calculateDistanceToWithoutRadius(currentParticle);
    }

    private static double forceY(Particle otherParticle, Particle currentParticle){
        return forceGravity(otherParticle,currentParticle)*(otherParticle.getY() - currentParticle.getY())/otherParticle.calculateDistanceToWithoutRadius(currentParticle);
    }

    public static double totalForceX(Particle currentParticle, List<Particle> particles){
//        return particles.stream().mapToDouble(particle -> forceX(currentParticle,particle)).sum();
        double totalForceX = 0.0;
        for(Particle particle : particles){
            totalForceX += forceX(particle,currentParticle);
        }
        return totalForceX;
    }

    public static double totalForceY(Particle currentParticle, List<Particle> particles){
//        return particles.stream().mapToDouble(particle -> forceY(currentParticle,particle)).sum();
        double totalForceY = 0.0;
        for(Particle particle : particles){
            totalForceY += forceY(particle,currentParticle);
        }
        return totalForceY;
    }

    public static double totalForce(Particle currentParticle, List<Particle> particles){
//        return particles.stream().mapToDouble(particle -> forceGravity(currentParticle,particle)).sum();
        double totalForce = 0.0;
        for(Particle particle : particles){
            totalForce += forceGravity(particle,currentParticle);
        }
        return totalForce;
    }
}
