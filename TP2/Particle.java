import java.util.Objects;

public class Particle implements Comparable{
    private Double x;
    private Double y;
    private static Integer count = 1;
    private final Integer id;
    private static final double velocity = Constants.PARTICLE_VELOCITY;
    private double angle;

    public Particle(Double x, Double y, double angle) {
        this.x = x;
        this.y = y;
        this.id = count++;
        this.angle = angle;
    }

    public Double calculateDistanceTo(Particle other) {
        return Math.max(0, Math.hypot(this.getX() - other.getX(), this.getY() - other.getY()));
    }

    public Double calculateDistancePeriodicTo(Particle other, int boxLength) {
        double minDistance = 2 * boxLength; // la distancia si o si sera mas chica que este valor inicial

        for (int i =  -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                minDistance = Math.min(minDistance, Math.max(0,
                        Math.hypot(this.getX() - other.getX() + i * boxLength, this.getY() - other.getY() + j * boxLength)));
            }
        }
        return minDistance;
    }


    public Double getX() {
        return x;
    }

    public Double getY() {
        return y;
    }

    public double getAngle() {
        return angle;
    }

    public Integer getId() {
        return id;
    }

    public Double getXVelocity() { return velocity * Math.cos(angle);}

    public Double getYVelocity() { return velocity * Math.sin(angle);}

    public void setAngle(double angle) {
        this.angle = angle;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public void setY(Double y) {
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Particle particle = (Particle) o;
        return Objects.equals(id, particle.id);
    }

    @Override
    public String toString() {
        return "Particle{" +
                ", x=" + x +
                ", y=" + y +
                ", id=" + id +
                ", angle=" + angle +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(Object o) {
        if(getClass() != o.getClass()){
            return -1;
        }
        Particle other = (Particle) o;
        return this.id.compareTo(other.getId());
    }
}
