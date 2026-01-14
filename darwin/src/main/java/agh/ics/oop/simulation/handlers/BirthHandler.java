package agh.ics.oop.simulation.handlers;

import java.util.HashMap;
import java.util.List;

import agh.ics.oop.simulation.SimulationParameters;
import agh.ics.oop.model.Animal;
import agh.ics.oop.model.Plant;
import agh.ics.oop.model.Vector2d;

public class BirthHandler {
    private final int reproductionEnergyMin;
    private final int reproductionEnergyCost;
    private final int minMutations;
    private final int maxMutations;

    public BirthHandler(SimulationParameters parameters) {
        this.reproductionEnergyMin = parameters.reproductionEnergyMin();
        this.reproductionEnergyCost = parameters.reproductionEnergyCost();
        this.minMutations = parameters.minMutations();
        this.maxMutations = parameters.maxMutations();
    }

    public List<Animal> spawn(int initialAnimalCount, int initialAnimalEnergy, int genomeLength) {
        // TODO:
        return null;
    }

    public List<Animal> handle(List<Animal> animals) {
        // TODO:
        return null;
    }
}
