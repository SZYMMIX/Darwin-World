package agh.ics.oop.simulation.handlers;

import java.util.List;
import java.util.List;

import agh.ics.oop.model.Animal;
import agh.ics.oop.simulation.SimulationParameters;

public class DeathHandler {
    private final int dailyEnergyCost;

    private final List<Animal> animals;
    private final List<Animal> dead;

    public DeathHandler(SimulationParameters parameters, List<Animal> animals, List<Animal> dead) {
        this.dailyEnergyCost = parameters.dailyEnergyCost();

        this.animals = animals;
        this.dead = dead;
    }

    public void handle() {
        // TODO:
    }

    public void subtractEnergy() {
        // TODO:
    }
}
