package agh.ics.oop.model;

public class Plant {
    private final boolean isPoisonous;

    public Plant(boolean isPoisonous) {
        this.isPoisonous = isPoisonous;
    }

    public boolean isPoisonous() {
        return isPoisonous;
    }
}