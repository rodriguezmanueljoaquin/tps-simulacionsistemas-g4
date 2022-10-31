public enum ParticleState {
    HUMAN(0),
    HUMAN_INFECTED(1),
    ZOMBIE_INFECTING(2),
    ZOMBIE(3),
    WALL(4);

    private int value;

    ParticleState(int value) {
        this.value = value;
    }


}
