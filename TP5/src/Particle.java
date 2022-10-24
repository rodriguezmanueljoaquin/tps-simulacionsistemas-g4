import java.util.Map;
import java.util.Objects;

public class Particle implements Comparable{
    private Double x;
    private Double y;
    private double yVelocity;
    private double xVelocity;
    private double radius;
    private ParticleState state;
    private static Integer count = 1;
    private final Integer id;
    private Double zombieContactTime;
    private Double vdMax;
    private Double velocity;

    public Particle(Double x, Double y, double angle, ParticleState state, Double vdMax) {
        this.x = x;
        this.y = y;
        this.id = count++;
        this.state = state;
        this.xVelocity = vdMax * Math.cos(angle);
        this.yVelocity = vdMax * Math.sin(angle);
        this.radius = Constants.MAX_RADIUS;
        this.vdMax = vdMax;
    }

    public Double calculateDistanceTo(Particle other) {
        return Math.max(0, Math.hypot(this.getX() - other.getX(), this.getY() - other.getY()) - this.radius - other.radius);
    }

    public double calculateDistanceToWithoutRadius(double otherX, double otherY) {
        return Math.hypot(this.getX() - otherX, this.getY() - otherY);
    }

    public ParticleState getState() {
        return this.state;
    }

    public void setState(ParticleState state){
        this.state = state;
    }

    public Double getX() {
        return this.x;
    }

    public Double getY() {
        return this.y;
    }

    public Integer getId() {
        return this.id;
    }

    public Double getXVelocity() { return this.xVelocity;}

    public Double getYVelocity() { return this.yVelocity;}

    public void setXVelocity(double xVelocity) {
        this.xVelocity = xVelocity;
    }

    public void setYVelocity(double yVelocity) {
        this.yVelocity = yVelocity;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public Double getZombieContactTime() {
        return zombieContactTime;
    }

    public void setZombieContactTime(Double time){
        this.zombieContactTime = time;
    }

    public double distanceToOrigin(){
        return Math.hypot(this.getX(), this.getY());
    }

    public void updatePosition( double dt){
        this.x += this.xVelocity*dt;
        this.y += this.yVelocity*dt;
    }

    public void radiusUpdate(double t, double dt, boolean contact) {
       if(contact){
           this.radius = Constants.MIN_RADIUS;
       }else this.radius += Constants.MAX_RADIUS/(t/dt);
    }

    //Para este metodo, el x y el y serian los de:
    //-Si hubo colision, la particula con la que colisiono
    //-Si no hubo colision y es un zombie, el humano objetivo
    //-Si no hubo colision y es un humano, el zombie más cercano
    public void velocityUpdate(boolean contact, double otherX, double otherY){
        double rx;
        double ry;
        if(contact){
            //Si hubo contacto, la velocidad de escape es en direcciones opuestas, en la direccion del eje de contacto
            rx = (this.x-otherX) / this.calculateDistanceToWithoutRadius(otherX,otherY);
            ry = (this.y-otherY) / this.calculateDistanceToWithoutRadius(otherX,otherY);
            this.velocity = this.vdMax;
        }else{
            this.velocity = vdMax*(Math.pow((radius - Constants.MIN_RADIUS ) /(Constants.MAX_RADIUS - Constants.MIN_RADIUS),Constants.b));
            rx = (this.x - otherX) / Math.abs(otherX - this.x); //target x
            ry = (this.y - otherY) / Math.abs(otherY - this.y); //target y
            if(!this.state.equals(ParticleState.ZOMBIE)){
                // dirección opuesta a la que lleva al target
                rx *= -1;
                ry *= -1;
            }
        }
        this.xVelocity = this.velocity*rx;
        this.yVelocity = this.velocity*ry;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Particle particle = (Particle) o;
        return Objects.equals(this.id, particle.id);
    }

    @Override
    public String toString() {
        return "Particle{" +
                ", x=" + this.x +
                ", y=" + this.y +
                ", id=" + this.id +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
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
