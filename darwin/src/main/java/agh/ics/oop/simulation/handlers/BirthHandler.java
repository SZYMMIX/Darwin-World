package agh.ics.oop.simulation.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import agh.ics.oop.simulation.SimulationParameters;
import agh.ics.oop.model.Animal;
import agh.ics.oop.model.Plant;
import agh.ics.oop.model.Vector2d;

public class BirthHandler {
    private final int reproductionEnergyMin;
    private final int reproductionEnergyCost;
    private final int minMutations;
    private final int maxMutations;

    private final ArrayList<Animal> animals;
    private final HashMap<Vector2d, Plant> plants;

    private final Random random;

    public BirthHandler(
            SimulationParameters parameters,
            ArrayList<Animal> animals,
            HashMap<Vector2d, Plant> plants,
            Random random) {
        this.reproductionEnergyMin = parameters.reproductionEnergyMin();
        this.reproductionEnergyCost = parameters.reproductionEnergyCost();
        this.minMutations = parameters.minMutations();
        this.maxMutations = parameters.maxMutations();

        this.animals = animals;
        this.plants = plants;

        this.random = random;

        spawn(parameters.initialAnimalCount(), parameters.initialAnimalEnergy(), parameters.genomeLength());
    }

    private void spawn(int initialAnimalCount, int initialAnimalEnergy, int genomeLength) {
        // TODO:
    }

    public void handle() {
        // TODO:
    }
}
