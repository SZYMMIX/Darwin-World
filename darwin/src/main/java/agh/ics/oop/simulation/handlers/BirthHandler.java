package agh.ics.oop.simulation.handlers;

import java.util.*;

import agh.ics.oop.model.*;
import agh.ics.oop.simulation.SimulationParameters;

public class BirthHandler {
    private final int reproductionEnergyMin;
    private final int reproductionEnergyCost;
    private final int minMutations;
    private final int maxMutations;

    private final int height;
    private final int width;

    private final List<Animal> animals;
    private final Map<Vector2d, Plant> plants;

    private final Random random;
    private final Comparator<Animal> conflictResolver;

    public BirthHandler(
            SimulationParameters parameters,
            List<Animal> animals,
            Map<Vector2d, Plant> plants,
            Random random) {
        this.reproductionEnergyMin = parameters.reproductionEnergyMin();
        this.reproductionEnergyCost = parameters.reproductionEnergyCost();
        this.minMutations = parameters.minMutations();
        this.maxMutations = parameters.maxMutations();
        this.height = parameters.height();
        this.width = parameters.width();

        this.animals = animals;
        this.plants = plants;

        this.random = random;

        this.conflictResolver = Comparator.comparingInt(Animal::getEnergy).reversed()
                .thenComparingInt(Animal::getBirthDay)
                .thenComparingInt(Animal::getChildrenCount).reversed()
                .thenComparing(x -> random.nextDouble());

        spawn(parameters.initialAnimalCount(), parameters.initialAnimalEnergy(), parameters.genomeLength());
    }

    private void spawn(int initialAnimalCount, int initialAnimalEnergy, int genomeLength) {
        for (int i = 0; i < initialAnimalCount; i++){
            Vector2d position = new Vector2d(random.nextInt(width), random.nextInt(height));
            Animal animal = Animal.createRandom(position, initialAnimalEnergy, genomeLength, 0, random);
            animals.add(animal);
        }
    }

    public void handle(int currentDay) {
        Map<Vector2d, List<Animal>> animalsByPosition = new HashMap<>();
        for (Animal animal : animals) {
            animalsByPosition.computeIfAbsent(animal.getPosition(), k -> new ArrayList<>()).add(animal);
        }

        List<Animal> newChildren = new ArrayList<>();

        for (List<Animal> candidates : animalsByPosition.values()) {
            if (candidates.size() >= 2) {
                candidates.sort(conflictResolver);

                Animal parent1 = candidates.get(0);
                Animal parent2 = candidates.get(1);

                if (parent2.getEnergy() >= reproductionEnergyMin) {
                    parent1.subtractEnergy(reproductionEnergyCost);
                    parent2.subtractEnergy(reproductionEnergyCost);

                    int childEnergy = reproductionEnergyCost * 2;

                    Animal child = Animal.fromParents(
                            parent1, parent2,
                            currentDay,
                            childEnergy,
                            random
                    );

                    newChildren.add(child);
                }
            }
        }
        animals.addAll(newChildren);
    }
}
