import java.util.function.Predicate;

public class NeighbourPredicates {
    public static Predicate<Particle> IsANeighbour(Particle particle, Double neighbourRadius, int boxLength, boolean periodicConditions, boolean cellIndexOptimized){
        return other -> !other.equals(particle) && (
                (!periodicConditions && particle.calculateDistanceTo(other) < neighbourRadius) ||
                        (periodicConditions && particle.calculateDistancePeriodicTo(other, boxLength, cellIndexOptimized) < neighbourRadius)
        );
    }
}
