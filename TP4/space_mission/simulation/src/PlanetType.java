public enum PlanetType {
    SUN("sun"),
    EARTH("earth"),
    VENUS("venus"),
    SPACESHIP("spaceship");

    private String planetName;

    PlanetType(String planetName) {
        this.planetName = planetName;
    }

    public String getPlanetName() {
        return planetName;
    }
}
