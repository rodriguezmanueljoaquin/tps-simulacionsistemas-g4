import java.util.Objects;

public class Particle {
    private final Double radius;
    private final Double x;
    private final Double y;
    private static Integer count = 1;
    private final Integer id;

    public Particle(Double x, Double y,double radius) {
        this.x = x;
        this.y = y;
        this.id = count++;
        this.radius = radius;
    }

    Double calculateDistanceTo(Particle other) {
        return Math.hypot(this.getX() - other.getX(), this.getY() - other.getY()) - 2 * this.radius;
    }

    public Double getX() {
        return x;
    }

    public Double getY() {
        return y;
    }

    public Integer getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Particle particle = (Particle) o;
        return Objects.equals(id, particle.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {

        return "Particle{ position:(" +
                "x=" + Math.floor(x*100)/100 +
                ", y=" + Math.floor(y*100)/100 +
                "), id=" + id +
                '}';
    }
}
