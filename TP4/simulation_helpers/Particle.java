import java.util.List;
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


    public double forceGravity(Particle p){
        return Constants.G*this.getMass()* p.getMass()/(Math.pow(calculateDistanceTo(p),2));
    }

    public double forceX(Particle p){
            return forceGravity(p)*(this.getX() - p.getX())/calculateDistanceTo(p);
    }

    public double forceY(Particle p){
        return forceGravity(p)*(this.getY() - p.getY())/calculateDistanceTo(p);
    }

    public double totalForceX(List<Particle> particles){
        return particles.stream().mapToDouble(particle -> forceX(particle)).sum();
    }

    public double totalForceY(List<Particle> particles){
        return particles.stream().mapToDouble(particle -> forceY(particle)).sum();
    }

    public double totalForce(List<Particle> particles){
        return particles.stream().mapToDouble(particle -> forceGravity(particle)).sum();
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
