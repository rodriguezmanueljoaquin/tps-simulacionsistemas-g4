import java.util.Objects;

public class Particle implements Comparable {
    private Double x, y, wanderTargetX, wanderTargetY;
    private double yVelocity;
    private double xVelocity;
    private double radius;
    private final double vdMax;
    private double zombieContactTime;
    private ParticleState state;
    private static Integer count = 1;
    private final Integer id;

    public Particle(Double x, Double y, double angle, ParticleState state, Double vdMax) {
        this.x = x;
        this.y = y;
        this.id = count++;
        this.state = state;
        this.xVelocity = vdMax * Math.cos(angle);
        this.yVelocity = vdMax * Math.sin(angle);
        this.radius = Constants.PARTICLE_MAX_RADIUS;
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

    public void setWanderTarget(Double wanderTargetX, Double wanderTargetY) {
        this.wanderTargetX = wanderTargetX;
        this.wanderTargetY = wanderTargetY;
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

    public void updatePosition(double dt) {
        this.x += this.xVelocity * dt;
        this.y += this.yVelocity * dt;
    }

    public void radiusUpdate(boolean contact) {
        if (contact) {
            this.radius = Constants.PARTICLE_MIN_RADIUS;
        } else if (this.radius < Constants.PARTICLE_MAX_RADIUS)
            this.radius += Constants.PARTICLE_MAX_RADIUS / (Constants.EXPANSION_TIME / Constants.DELTA_T);
    }

    //Para este metodo, el x y el y serian los de:
    //-Si hubo colision, la particula con la que colisiono
    //-Si no hubo colision y es un zombie, el humano objetivo
    //-Si no hubo colision y es un humano, el zombie más cercano

    // Recibe velocity, pues en el caso de que un zombie este deambulando sin objetivo debe hacerlo con una velocidad especifica
    public void velocityUpdate(boolean contact, double otherX, double otherY, Double velocity) {
        double rx;
        double ry;
        if (contact) {
            //Si hubo contacto, la velocidad de escape es en direcciones opuestas, en la direccion del eje de contacto
            rx = (this.x - otherX) / this.calculateDistanceToWithoutRadius(otherX, otherY);
            ry = (this.y - otherY) / this.calculateDistanceToWithoutRadius(otherX, otherY);
            velocity = this.vdMax;
        } else {
            if (velocity == null)
                velocity = vdMax * (Math.pow((radius - Constants.PARTICLE_MIN_RADIUS) / (Constants.PARTICLE_MAX_RADIUS - Constants.PARTICLE_MIN_RADIUS), Constants.b));

            rx = (otherX - this.x) / Math.abs(otherX - this.x); //target x
            ry = (otherY - this.y) / Math.abs(otherY - this.y); //target y
            if (!this.state.equals(ParticleState.ZOMBIE) && !this.state.equals(ParticleState.ZOMBIE_INFECTING)) {
                // dirección opuesta a la que lleva al target
                rx *= -1;
                ry *= -1;
            }
        }
        this.xVelocity = velocity * rx;
        this.yVelocity = velocity * ry;
    }

    public boolean hasWanderTarget() {
        return this.wanderTargetX != null && this.wanderTargetY != null;
    }

    public boolean reachedWanderTarget() {
        return (Math.abs(this.x - this.wanderTargetX) < Constants.DISTANCE_EPSILON)
                && (Math.abs(this.y - this.wanderTargetY) < Constants.DISTANCE_EPSILON);
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
