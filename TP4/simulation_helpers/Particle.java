import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class Particle {
    private double x;
    private double y;
    private static Integer count = 1;
    private final Integer id;
    private double yVelocity;
    private double xVelocity;
    private double velocity;
    private double radius;
    private double mass;
    private double xAcceleration;
    private double yAcceleration;
    private double xPrevAcceleration;
    private double yPrevAcceleration;

    public Particle(double x, double y, double xVelocity, double yVelocity, double radius, double mass) {
        this.x = x;
        this.y = y;
        this.xVelocity = xVelocity;
        this.yVelocity = yVelocity;
        this.radius = radius;
        this.mass = mass;

        this.id = count++;
    }


    public Particle(double x, double y, double angle) {
        this.x = x;
        this.y = y;
        this.id = count++;
        this.xVelocity = velocity * Math.cos(angle);
        this.yVelocity = velocity * Math.sin(angle);
    }

    public double calculateDistanceTo(Particle other) {
        return Math.hypot(this.getX() - other.getX(), this.getY() - other.getY()) - other.getRadius() - radius;
    }

    public double calculateDistanceToWithoutRadius(Particle other) {
        return Math.hypot(this.getX() - other.getX(), this.getY() - other.getY());
    }

    public double getX() {
        return x;
    }

    public double getY() {
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


    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setxVelocity(double xVelocity) {
        this.xVelocity = xVelocity;
    }

    public void setyVelocity(double yVelocity) {
        this.yVelocity = yVelocity;
    }

    //Prueba
    public double getXAcceleration() {
        return xAcceleration;
    }

    public void setXAcceleration(double xAcceleration) {
        this.xAcceleration = xAcceleration;
    }

    public double getYAcceleration() {
        return yAcceleration;
    }

    public void setYAcceleration(double yAcceleration) {
        this.yAcceleration = yAcceleration;
    }

    public double getXPrevAcceleration() {
        return xPrevAcceleration;
    }

    public void setXPrevAcceleration(double xPrevAcceleration) {
        this.xPrevAcceleration = xPrevAcceleration;
    }

    public double getYPrevAcceleration() {
        return yPrevAcceleration;
    }

    public void setYPrevAcceleration(double yPrevAcceleration) {
        this.yPrevAcceleration = yPrevAcceleration;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Particle))
            return false;
        Particle particle = (Particle) o;
        return Objects.equals(id, particle.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
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
