package agh.ics.oop.simulation;

import agh.ics.oop.model.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class InteractionHandlerTest {

    private SimulationParameters createParams(boolean isPoisonMap,
                                              int plantEnergy,
                                              int poisonCost,
                                              int genotypeLength,
                                              int reproMin,
                                              int reproCost) {
        return new SimulationParameters(
                10, 10, 0,
                plantEnergy, 1, 0, 0,
                0,
                reproMin, reproCost,
                0, 0,
                genotypeLength,
                isPoisonMap, 0.5, poisonCost
        );
    }

    private RuntimeAnimal createAnimal(int id, int energy, Genotype genotype) {
        ChildData data = new ChildData(
                new Vector2d(2, 2),
                energy,
                genotype,
                -1, -1
        );
        return RuntimeAnimal.fromChildData(id, data, 0, new Random());
    }

    private Genotype createRandomGenotype(int length) {
        return Genotype.random(length, new Random());
    }

    @Test
    void handleInteractionsShouldIncreaseEnergyWhenEatingHealthyPlant() {
        int startEnergy = 50;
        int plantEnergy = 20;
        SimulationParameters params = createParams(false,
                plantEnergy, 0, 5, 10, 5);
        InteractionHandler handler = new InteractionHandler(params, new Random());

        RuntimeAnimal animal = createAnimal(1, startEnergy, createRandomGenotype(5));
        Plant plant = new Plant(false);

        InteractionHandler.InteractionResult result = handler.handleInteractions(List.of(animal), plant);

        assertTrue(result.plantEaten());
        assertEquals(startEnergy + plantEnergy, animal.getEnergy());
        assertTrue(result.newChildren().isEmpty());
    }

    @Test
    void handleInteractionsShouldNotReduceEnergyWhenGenotypeIsIdenticalToImmunityPattern() {
        int startEnergy = 100;
        int poisonCost = 50;
        int genotypeLength = 4;
        SimulationParameters params = createParams(true, 0, poisonCost, genotypeLength, 10, 5);

        Random predictorRandom = new Random(42);
        Genotype predictedIdealGenotype = Genotype.random(genotypeLength, predictorRandom);

        InteractionHandler handler = new InteractionHandler(params, new Random(42));

        RuntimeAnimal animal = createAnimal(1, startEnergy, predictedIdealGenotype);
        Plant poison = new Plant(true);

        handler.handleInteractions(List.of(animal), poison);

        assertEquals(startEnergy, animal.getEnergy());
    }

    @Test
    void handleInteractionsShouldReproduceWhenEnoughEnergy() {
        int startEnergy = 50;
        int minReproEnergy = 20;
        int reproCost = 10;

        SimulationParameters params = createParams(false, 0, 0, 5, minReproEnergy, reproCost);
        InteractionHandler handler = new InteractionHandler(params, new Random());

        RuntimeAnimal parent1 = createAnimal(1, startEnergy, createRandomGenotype(5));
        RuntimeAnimal parent2 = createAnimal(2, startEnergy, createRandomGenotype(5));
        List<RuntimeAnimal> animals = List.of(parent1, parent2);

        InteractionHandler.InteractionResult result = handler.handleInteractions(animals, null);

        assertEquals(1, result.newChildren().size());

        assertEquals(startEnergy - reproCost, parent1.getEnergy());
        assertEquals(startEnergy - reproCost, parent2.getEnergy());

        assertEquals(reproCost * 2, result.newChildren().get(0).initialEnergy());
    }

    @Test
    void handleInteractionsShouldNotReproduceWhenNotEnoughEnergy() {
        int startEnergy = 10;
        int minReproEnergy = 20;
        int reproCost = 10;

        SimulationParameters params = createParams(false, 0, 0, 5, minReproEnergy, reproCost);
        InteractionHandler handler = new InteractionHandler(params, new Random());

        RuntimeAnimal parent1 = createAnimal(1, startEnergy, createRandomGenotype(5));
        RuntimeAnimal parent2 = createAnimal(2, startEnergy, createRandomGenotype(5));
        List<RuntimeAnimal> animals = List.of(parent1, parent2);

        InteractionHandler.InteractionResult result = handler.handleInteractions(animals, null);

        assertTrue(result.newChildren().isEmpty());
        assertEquals(startEnergy, parent1.getEnergy());
        assertEquals(startEnergy, parent2.getEnergy());
    }

    @Test
    void handleInteractionsShouldEnableOnlyTheStrongestToEat() {
        int startEnergy = 50;
        int plantEnergy = 10;
        SimulationParameters params = createParams(false, plantEnergy, 0, 5, 100, 10); // Wysokie minRepro, żeby się nie rozmnożyły
        InteractionHandler handler = new InteractionHandler(params, new Random());

        RuntimeAnimal strong = createAnimal(1, startEnergy, createRandomGenotype(5));
        RuntimeAnimal weak = createAnimal(2, startEnergy, createRandomGenotype(5));

        List<RuntimeAnimal> animals = List.of(strong, weak);
        Plant plant = new Plant(false);

        handler.handleInteractions(animals, plant);

        assertEquals(startEnergy + plantEnergy, strong.getEnergy());
        assertEquals(startEnergy, weak.getEnergy());
    }
}