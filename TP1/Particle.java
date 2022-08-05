

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
}
