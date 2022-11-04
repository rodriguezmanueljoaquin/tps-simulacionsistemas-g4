import java.util.Objects;

public class Particle implements Comparable {
    private Double x, y, wanderTargetX, wanderTargetY;
    private double yVelocity;
    private double xVelocity;
    private double radius;
    private final double vdMax;
    private double zombieContactTime;
    private double startWanderingTime;
    private ParticleState state;
    private static Integer count = 1;
    private final Integer id;
    private final Double AP;
    private final Double BP;

    public Particle(Double x, Double y, double angle, ParticleState state, Double vdMax, Double AP, Double BP) {
        this.x = x;
        this.y = y;
        this.id = count++;
        this.state = state;
        this.xVelocity = vdMax * Math.cos(angle);
        this.yVelocity = vdMax * Math.sin(angle);
        this.radius = Constants.PARTICLE_MAX_RADIUS;
        this.vdMax = vdMax;
        this.AP = AP;
        this.BP = BP;
    }

    public Particle(Double x, Double y, double angle, double radius, ParticleState state, Double vdMax, Double AP, Double BP) {
        this.x = x;
        this.y = y;
        this.id = count++;
        this.state = state;
        this.xVelocity = vdMax * Math.cos(angle);
        this.yVelocity = vdMax * Math.sin(angle);
        this.radius = radius;
        this.vdMax = vdMax;
        this.AP = AP;
        this.BP = BP;
    }

    public Double calculateDistanceTo(Particle other) {
        return Math.hypot(this.getX() - other.getX(), this.getY() - other.getY()) - this.radius - other.radius;
    }

    public double calculateDistanceToWithoutRadius(double otherX, double otherY) {
        return Math.hypot(this.getX() - otherX, this.getY() - otherY);
    }

    public void updatePosition(double dt) {
        this.x += this.xVelocity * dt;
        this.y += this.yVelocity * dt;
    }

    public void radiusUpdate(boolean contact, Double DELTA_T) {
        if (contact) {
            this.radius = Constants.PARTICLE_MIN_RADIUS;
        } else if (this.radius < Constants.PARTICLE_MAX_RADIUS)
            this.radius += Constants.PARTICLE_MAX_RADIUS / (Constants.EXPANSION_TIME / DELTA_T);
    }

    //Para este metodo, el x y el y serian los de:
    //-Si hubo colision, la particula con la que colisiono
    //-Si no hubo colision el target
    // Recibe velocity, pues en el caso de que un zombie este deambulando sin objetivo debe hacerlo con una velocidad especifica
    public void velocityUpdate(boolean contact, double otherX, double otherY, Double velocity) {
        double rx;
        double ry;
        rx = (otherX - this.x) / this.calculateDistanceToWithoutRadius(otherX, otherY);
        ry = (otherY - this.y) / this.calculateDistanceToWithoutRadius(otherX, otherY);
        if (contact) {
            //Si hubo contacto, la velocidad de escape es en direcciones opuestas al other, en la direccion del eje de contacto
            rx *= -1;
            ry *= -1;
            velocity = this.vdMax;
        } else if (velocity == null) {
            velocity = vdMax * (Math.pow((radius - Constants.PARTICLE_MIN_RADIUS) /
                    (Constants.PARTICLE_MAX_RADIUS - Constants.PARTICLE_MIN_RADIUS), Constants.b));
        }
        this.xVelocity = velocity * rx;
        this.yVelocity = velocity * ry;
    }

    public boolean hasWanderTarget() {
        return this.wanderTargetX != null && this.wanderTargetY != null;
    }

    public boolean changeWanderTarget(Double currentTime) {
        return calculateDistanceToWithoutRadius(wanderTargetX, wanderTargetY) < Constants.WANDER_TARGET_DISTANCE_EPSILON ||
                currentTime > this.startWanderingTime + Constants.WANDER_TARGET_TIME;
    }

    public ParticleState getState() {
        return this.state;
    }

    public void setState(ParticleState state) {
        this.state = state;
    }

    public Integer getId() {
        return this.id;
    }

    public Double getX() {
        return this.x;
    }

    public Double getY() {
        return this.y;
    }

    public double getRadius() {
        return radius;
    }

    public Double getXVelocity() {
        return this.xVelocity;
    }

    public Double getYVelocity() {
        return this.yVelocity;
    }

    public Double getWanderTargetX() {
        return this.wanderTargetX;
    }

    public Double getWanderTargetY() {
        return this.wanderTargetY;
    }

    public Double getAP() {
        return this.AP;
    }

    public Double getBP() {
        return this.BP;
    }

    public void setXVelocity(double xVelocity) {
        this.xVelocity = xVelocity;
    }

    public void setYVelocity(double yVelocity) {
        this.yVelocity = yVelocity;
    }

    public void setWanderTarget(Double wanderTargetX, Double wanderTargetY, Double time) {
        this.wanderTargetX = wanderTargetX;
        this.wanderTargetY = wanderTargetY;
        this.startWanderingTime = time;
    }

    public Double getZombieContactTime() {
        return zombieContactTime;
    }

    public void setZombieContactTime(Double time) {
        this.zombieContactTime = time;
    }

    public double distanceToOrigin() {
        return Math.hypot(this.getX(), this.getY());
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
                "x=" + this.x +
                ", y=" + this.y +
                ", state=" + this.state.toString() +
                ", id=" + this.id +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    @Override
    public int compareTo(Object o) {
        if (getClass() != o.getClass()) {
            return -1;
        }
        Particle other = (Particle) o;
        return this.id.compareTo(other.getId());
    }
}
