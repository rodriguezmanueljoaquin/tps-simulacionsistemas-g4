import java.util.Objects;

public class Particle {
    private static final Double radius = 0.25; // TODO: SACAR A OTRA FILE
    private Double x,y;
    private static Integer count = 1;
    private Integer id;

    public Particle(Double x, Double y) {
        this.x = x;
        this.y = y;
        this.id = count++;
    }

    Double calculateDistanceTo(Particle other) {
        return  Math.hypot(this.getX() - other.getX(), this.getY() - other.getY()) - 2 * Particle.radius;
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
                "x=" + x +
                ", y=" + y +
                "), id=" + id +
                '}';
    }
}
