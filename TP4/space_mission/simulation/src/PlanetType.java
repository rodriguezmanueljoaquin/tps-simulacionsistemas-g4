public enum PlanetType {
    SUN("sun"),
    EARTH("earth"),
    VENUS("venus"),
    MARS("mars"),
    SPACESHIP("spaceship");

    private String planetName;

    PlanetType(String planetName) {
        this.planetName = planetName;
    }

    public String getPlanetName() {
        return planetName;
    }
}
