public enum ParticleState {
    HUMAN(0),
    HUMAN_INFECTED(1),
    ZOMBIE(2),
    ZOMBIE_INFECTING(3);

    private  int value;

    ParticleState(int value) {
        this.value = value;
    }



}
