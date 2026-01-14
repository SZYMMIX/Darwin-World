package agh.ics.oop.simulation.handlers;

import java.util.HashMap;
import java.util.List;

import agh.ics.oop.simulation.SimulationParameters;
import agh.ics.oop.model.Animal;
import agh.ics.oop.model.Plant;
import agh.ics.oop.model.Vector2d;

public class ConsumptionHandler {
    private final int plantEnergy;
    private final int poisonEnergyCost;
    // TODO: Immune Genome

    public ConsumptionHandler(SimulationParameters parameters) {
        this.plantEnergy = parameters.plantEnergy();
        this.poisonEnergyCost = parameters.poisonEnergyCost();
    }

    public void handle(List<Animal> animals, HashMap<Vector2d, Plant> plants) {
        // TODO:
    }
}
