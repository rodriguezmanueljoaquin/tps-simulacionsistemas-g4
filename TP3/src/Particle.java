import java.util.Objects;

public class Particle implements Comparable{
    private Double x;
    private Double y;
    private static Integer count = 1;
    private final Integer id;
    private static final double velocity = Constants.PARTICLE_VELOCITY;
    private double angle;

    private static final double radius = Constants.PARTICLE_RADIUS;

    private static final double mass = Constants.PARTICLE_MASS;

    public Particle(Double x, Double y, double angle) {
        this.x = x;
        this.y = y;
        this.id = count++;
        this.angle = angle;
    }

    public Double calculateDistanceTo(Particle other) {
        return Math.hypot(this.getX() - other.getX(), this.getY() - other.getY()) - Particle.getRadius()*2;
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

    public static double getRadius() {
        return radius;
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
