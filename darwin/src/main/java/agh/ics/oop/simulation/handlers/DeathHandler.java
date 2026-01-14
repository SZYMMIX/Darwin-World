package agh.ics.oop.simulation.handlers;

import java.util.ArrayList;
import java.util.List;

import agh.ics.oop.model.Animal;
import agh.ics.oop.simulation.SimulationParameters;

public class DeathHandler {
    private final int dailyEnergyCost;

    private final ArrayList<Animal> animals;
    private final ArrayList<Animal> dead;

    public DeathHandler(SimulationParameters parameters, ArrayList<Animal> animals, ArrayList<Animal> dead) {
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
