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

    private static double forceGravity(Particle currentParticle, Particle otherParticle){
        return Constants.G*currentParticle.getMass()* otherParticle.getMass()/(Math.pow(currentParticle.calculateDistanceToWithoutRadius(otherParticle),2));
    }

    private static double forceX(Particle currentParticle, Particle otherParticle){
        return forceGravity(currentParticle, otherParticle)*(currentParticle.getX() - otherParticle.getX())/ currentParticle.calculateDistanceToWithoutRadius(otherParticle);
    }

    private static double forceY(Particle currentParticle, Particle otherParticle){
        return forceGravity(currentParticle,otherParticle)*(currentParticle.getY() - otherParticle.getY())/currentParticle.calculateDistanceToWithoutRadius(otherParticle);
    }

    public static double totalForceX(Particle currentParticle, List<Particle> particles){
        return particles.stream().mapToDouble(particle -> forceX(currentParticle,particle)).sum();
    }

    public static double totalForceY(Particle currentParticle, List<Particle> particles){
        return particles.stream().mapToDouble(particle -> forceY(currentParticle,particle)).sum();
    }

    public static double totalForce(Particle currentParticle, List<Particle> particles){
        return particles.stream().mapToDouble(particle -> forceGravity(currentParticle,particle)).sum();
    }
}
