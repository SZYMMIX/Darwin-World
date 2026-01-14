package agh.ics.oop.simulation.handlers;

import java.util.List;

import agh.ics.oop.model.Animal;
import agh.ics.oop.simulation.SimulationParameters;

public class DeathHandler {
    private final int dailyEnergyCost;

    public DeathHandler(SimulationParameters parameters) {
        this.dailyEnergyCost = parameters.dailyEnergyCost();
    }

    public void handle(List<Animal> alive, List<Animal> dead) {
        // TODO:
    }

    public void energy(List<Animal> animals) {
        // TODO:
    }
}
