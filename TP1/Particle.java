import java.util.Objects;

public class Particle {
    private final Double radius;
    private final Double x;
    private final Double y;
    private static Integer count = 1;
    private final Integer id;

    public Particle(Double x, Double y, double radius) {
        this.x = x;
        this.y = y;
        this.id = count++;
        this.radius = radius;
    }

    public Double calculateDistanceTo(Particle other) {
        return Math.max(0, Math.hypot(this.getX() - other.getX(), this.getY() - other.getY()) - this.radius - other.radius);
    }

    public Double calculateDistancePeriodicTo(Particle other, int boxLength) {
        double minDistance = Math.hypot(this.getX() - other.getX(), this.getY() - other.getY());

        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                minDistance = Math.min(minDistance, Math.max(0,
                        Math.hypot(this.getX() - other.getX() + i * boxLength, this.getY() - other.getY() + j * boxLength) - this.radius - other.radius));
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

    public Integer getId() {
        return id;
    }

    public Double getRadius() {
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
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {

        return "Particle{ position:(" +
                "x=" + Math.floor(x * 100) / 100 +
                ", y=" + Math.floor(y * 100) / 100 +
                "), id=" + id +
                '}';
    }
}
