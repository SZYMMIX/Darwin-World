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

    private final List<Animal> animals;
    private final HashMap<Vector2d, Plant> plants;

    public ConsumptionHandler(SimulationParameters parameters, List<Animal> animals, HashMap<Vector2d, Plant> plants) {
        this.plantEnergy = parameters.plantEnergy();
        this.poisonEnergyCost = parameters.poisonEnergyCost();

        this.animals = animals;
        this.plants = plants;
    }

    public void handle() {
        // TODO:
    }
}
