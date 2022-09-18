import java.util.Objects;

public class Particle {
    private Double x;
    private Double y;
    private static Integer count = 1;
    private final Integer id;
    private double yVelocity;
    private double xVelocity;
    private double velocity;
    private double radius;
    private double mass;
    private String lastCollisionType = Constants.NO_COLLISION_KEY;

    public Particle(Double x, Double y, double angle, double velocity, double radius, double mass) {
        this.x = x;
        this.y = y;
        this.xVelocity = velocity * Math.cos(angle);
        this.yVelocity = velocity * Math.sin(angle);
        this.velocity = velocity;
        this.radius = radius;
        this.mass = mass;

        this.id = count++;
    }

    public Particle(Double x, Double y, double angle) {
        this.x = x;
        this.y = y;
        this.id = count++;
        this.xVelocity = velocity * Math.cos(angle);
        this.yVelocity = velocity * Math.sin(angle);
    }

    public Double calculateDistanceTo(Particle other) {
        return Math.hypot(this.getX() - other.getX(), this.getY() - other.getY()) - other.getRadius() - radius;
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

    public double getRadius() {
        return radius;
    }

    public double getMass() {
        return mass;
    }

    public double getyVelocity() {
        return yVelocity;
    }


    public double getxVelocity() {
        return xVelocity;
    }


    public void setX(Double x) {
        this.x = x;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public void setxVelocity(double xVelocity) {
        this.xVelocity = xVelocity;
    }

    public void setyVelocity(double yVelocity) {
        this.yVelocity = yVelocity;
    }

    public String getLastCollisionType() {
        return lastCollisionType;
    }

    public void setLastCollisionType(String lastCollisionType) {
        this.lastCollisionType = lastCollisionType;
    }

    @Override
    public String toString() {
        return "Particle{" +
                "x=" + x +
                ", y=" + y +
                ", id=" + id +
                ", yVelocity=" + yVelocity +
                ", xVelocity=" + xVelocity +
                '}';
    }
}
