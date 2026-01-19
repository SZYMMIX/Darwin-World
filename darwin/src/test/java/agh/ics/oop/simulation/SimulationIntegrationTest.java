package agh.ics.oop.simulation;

import agh.ics.oop.model.*;
import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

class SimulationIntegrationTest {

    private Random createFixedRandom() {
        return new Random() {
            @Override
            public int nextInt(int bound) { return 0; }
            @Override
            public double nextDouble() { return 0.0; }
            @Override
            public boolean nextBoolean() { return false; }
            @Override
            public IntStream ints(long size, int origin, int bound) {
                return IntStream.generate(() -> 0).limit(size);
            }
        };
    }

    @Test
    void stepShouldMoveAnimalsAndIncrementDay() {
        int width = 10;
        int height = 10;
        SimulationParameters params = new SimulationParameters(
                width, height,
                0, 0, 0,
                1, 100, 0,
                0, 0, 0, 0, 5,
                false, 0.0, 0
        );
        Simulation simulation = new Simulation(params, createFixedRandom());

        SimulationSnapshot snapshot = simulation.step();

        assertEquals(1, snapshot.day());
        assertEquals(1, snapshot.animals().size());

        assertNotNull(snapshot.animals().get(0).position());
    }

    @Test
    void stepShouldReduceEnergyAndRemoveDeadAnimals() {
        int startEnergy = 5;
        int dailyCost = 10;
        SimulationParameters params = new SimulationParameters(
                5, 5, 0, 0, 0,
                1, startEnergy, dailyCost,
                0, 0, 0, 0, 5,
                false, 0.0, 0
        );
        Simulation simulation = new Simulation(params, createFixedRandom());

        SimulationSnapshot snap1 = simulation.step();

        assertEquals(1, snap1.animals().size());
        assertEquals(startEnergy - dailyCost, snap1.animals().get(0).energy());

        SimulationSnapshot snap2 = simulation.step();

        assertTrue(snap2.animals().isEmpty());
        assertEquals(0, snap2.stats().currentAnimalsCount());
    }

    @Test
    void stepShouldAllowAnimalsToEatPlants() {
        int startEnergy = 10;
        int plantEnergy = 20;
        int dailyCost = 0;

        SimulationParameters params = new SimulationParameters(
                1, 1,
                1, plantEnergy, 0,
                1, startEnergy, dailyCost,
                0, 0, 0, 0, 5,
                false, 0.0, 0
        );
        Simulation simulation = new Simulation(params, createFixedRandom());

        SimulationSnapshot snapshot = simulation.step();

        assertFalse(snapshot.animals().isEmpty());
        assertEquals(startEnergy + plantEnergy, snapshot.animals().get(0).energy());

        assertTrue(snapshot.plants().isEmpty());
    }

    @Test
    void stepShouldCreateNewChildWhenReproductionConditionsMet() {
        int startEnergy = 50;
        int reproductionMin = 20;
        int reproductionCost = 10;

        SimulationParameters params = new SimulationParameters(
                1, 1, 0, 0, 0,
                2, startEnergy, 0,
                reproductionMin, reproductionCost,
                0, 0, 5,
                false, 0.0, 0
        );
        Simulation simulation = new Simulation(params, createFixedRandom());

        SimulationSnapshot snapshot = simulation.step();

        assertEquals(3, snapshot.animals().size());
        assertEquals(3, snapshot.stats().currentAnimalsCount());

        long parentsCount = snapshot.animals().stream().filter(a -> a.energy() == 40).count();
        long childCount = snapshot.animals().stream().filter(a -> a.energy() == 20).count();

        assertEquals(2, parentsCount);
        assertEquals(1, childCount);
    }

    @Test
    void stepShouldGrowNewPlants() {
        int initialPlants = 0;
        int dailyGrowth = 1;

        SimulationParameters params = new SimulationParameters(
                5, 5,
                initialPlants, 0, dailyGrowth,
                0, 0, 0,
                0, 0, 0, 0, 5,
                false, 0.0, 0
        );
        Simulation simulation = new Simulation(params, createFixedRandom());

        SimulationSnapshot snapshot = simulation.step();

        assertEquals(1, snapshot.plants().size());
        assertEquals(1, snapshot.stats().currentPlantsCount());
    }

    @Test
    void stepShouldReturnCorrectSnapshotAndStats() {
        int width = 10;
        int height = 10;
        int initialAnimals = 3;
        int initialPlants = 2;

        SimulationParameters params = new SimulationParameters(
                width, height,
                initialPlants, 0, 0,
                initialAnimals, 10, 0,
                100, 100, 0, 0, 5,
                false, 0.0, 0
        );

        Random distinctRandom = new Random() {
            private int counter = 0;

            @Override
            public int nextInt(int bound) {
                return counter++ % bound;
            }

            @Override
            public double nextDouble() {
                return 1.0;
            }

            @Override
            public boolean nextBoolean() {
                return true;
            }

            @Override
            public IntStream ints(long size, int origin, int bound) {
                return IntStream.generate(() -> 0).limit(size);
            }
        };

        Simulation simulation = new Simulation(params, distinctRandom);

        SimulationSnapshot snapshot = simulation.step();

        assertNotNull(snapshot);
        assertEquals(1, snapshot.day());

        assertEquals(initialAnimals, snapshot.animals().size());
        assertEquals(initialPlants, snapshot.plants().size());

        SimulationStats stats = snapshot.stats();
        assertNotNull(stats);
        assertEquals(initialAnimals, stats.currentAnimalsCount());
        assertEquals(initialPlants, stats.currentPlantsCount());
    }

    @Test
    void stepShouldReturnCorrectSnapshotAndStatsWithRealRandom() {
        int width = 5;
        int height = 5;
        int initialAnimals = 2;
        int initialPlants = 2;

        SimulationParameters params = new SimulationParameters(
                width, height,
                initialPlants, 0, 0,
                initialAnimals, 10, 0,
                100, 100, 0, 0, 5,
                false, 0.0, 0
        );
        Simulation simulation = new Simulation(params, new Random());

        SimulationSnapshot snapshot = simulation.step();
        assertEquals(snapshot.animals().size(), snapshot.stats().currentAnimalsCount());

        assertEquals(snapshot.plants().size(), snapshot.stats().currentPlantsCount());

        int occupiedUpperBound = snapshot.animals().size() + snapshot.plants().size();
        int minFreeFields = (width * height) - occupiedUpperBound;

        assertTrue(snapshot.stats().freeFieldsCount() >= minFreeFields);
    }

    @Test
    void stepShouldReduceEnergyWhenEatingPoisonPlant() {
        int startEnergy = 20;
        int poisonCost = 10;

        SimulationParameters params = new SimulationParameters(
                1, 1,
                1, 0, 0,
                1, startEnergy, 0,
                0, 0, 0, 0, 5,
                true, 1.0, poisonCost
        );

        Random distinctGenotypeRandom = new Random() {
            private int counter = 0;

            @Override
            public IntStream ints(long size, int origin, int bound) {
                return IntStream.generate(() -> counter++ % 8).limit(size);
            }

            @Override
            public double nextDouble() { return 0.0; }
            @Override
            public int nextInt(int bound) { return 0; }
            @Override
            public boolean nextBoolean() { return false; }
        };

        Simulation simulation = new Simulation(params, distinctGenotypeRandom);

        SimulationSnapshot snapshot = simulation.step();

        assertFalse(snapshot.animals().isEmpty());

        assertEquals(startEnergy - poisonCost, snapshot.animals().get(0).energy());

        assertTrue(snapshot.plants().isEmpty());
    }
}