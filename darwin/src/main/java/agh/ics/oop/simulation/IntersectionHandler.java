package agh.ics.oop.simulation;

import agh.ics.oop.model.Genotype;
import agh.ics.oop.model.Plant;
import agh.ics.oop.model.Vector2d;

import java.util.*;

class InteractionHandler {
    private final SimulationParameters params;
    private final Random random;

    private final Genotype idealImmunityGenotype;

    InteractionHandler(SimulationParameters params, Random random) {
        this.params = params;
        this.random = random;
        this.idealImmunityGenotype = params.isPoisonMap()
                ? Genotype.random(params.genotypeLength(), random)
                : null;
    }

    record InteractionResult(boolean plantEaten, List<ChildData> newChildren) {}

    InteractionResult handleInteractions(List<RuntimeAnimal> animalsOnField, Plant plant) {
        boolean plantEaten = false;
        List<ChildData> children = new ArrayList<>();

        if (animalsOnField.isEmpty()) {
            return new InteractionResult(false, children);
        }

        RuntimeAnimal strongest = animalsOnField.get(0);

        if (plant != null) {
            eatPlant(strongest, plant);
            plantEaten = true;
        }

        if (animalsOnField.size() >= 2) {
            RuntimeAnimal parent1 = animalsOnField.get(0);
            RuntimeAnimal parent2 = animalsOnField.get(1);

            ChildData child = parent1.reproduce(
                    parent2,
                    params.reproductionEnergyMin(),
                    params.reproductionEnergyCost(),
                    params.minMutations(),
                    random
            );

            if (child != null) {
                children.add(child);
            }
        }

        return new InteractionResult(plantEaten, children);
    }

    private void eatPlant(RuntimeAnimal animal, Plant plant) {
        if (plant.isPoisonous()) {
            if (params.isPoisonMap() && idealImmunityGenotype != null) {
                int similarity = animal.getDetails().genotype().similarity(idealImmunityGenotype);

                double protection = (double) similarity / params.genotypeLength();
                int realCost = (int) (params.poisonEnergyCost() * (1.0 - protection));

                animal.subtractEnergy(realCost);
            } else {
                animal.subtractEnergy(params.poisonEnergyCost());
            }
        } else {
            animal.eat(params.plantEnergy());
        }
    }
}