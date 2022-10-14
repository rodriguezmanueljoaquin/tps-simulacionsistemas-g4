import java.time.LocalDateTime;

public class SpaceConstants {
    public static final double EPSILON = 0.00000001;
    public static final double OUTPUT_DELTA_T = 0.001;
    public static final double MAX_TRIP_TIME = 60*60*24*90.;
    public static final double SUN_MASS = 1988500*Math.pow(10,24);
    public static final double EARTH_MASS = 5.97219*Math.pow(10,24);
    public static final double VENUS_MASS = 48.685 * Math.pow(10,23);
    public static final double SPACESHIP_MASS = 2 * Math.pow(10, 5);
    public static final double SUN_RADIUS = 695700;
    public static final double EARTH_RADIUS = 6371.01;
    public static final double VENUS_RADIUS = 6051.84;
    public static final double SPACESHIP_RADIUS = 0.01;
    public static final double VELOCITY_LAUNCH = 8; // absolute value
    public static final double DISTANCE_SPACE_STATION_TO_ORIGIN = 1500;
    public static final double VELOCITY_SPACIAL_STATION = -7.12;
    public static final double ARRIVAL_UMBRAL =  1000;
    public static LocalDateTime START_SIMULATION_DATE = LocalDateTime.parse("2022-09-23T00:00:00");
}
