package agh.ics.oop.simulation;

import agh.ics.oop.model.Plant;
import agh.ics.oop.model.Vector2d;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class WorldMapTest {

    private SimulationParameters createParams(int width, int height, boolean isPoisonMap, double poisonProb) {
        return new SimulationParameters(
                width, height,
                0, 0, 0,
                0, 10, 0,
                0, 0,
                0, 0, 5,
                isPoisonMap, poisonProb, 0
        );
    }

    @Test
    void growPlantsShouldPlacePlantInJungleWhenRandomFavorsIt() {
        int height = 100;

        SimulationParameters params = createParams(10, height, false, 0.0);

        Random jungleRandom = new Random() {
            @Override
            public double nextDouble() {
                return 0.0;
            }

            @Override
            public int nextInt(int bound) {
                return 0;
            }
        };

        WorldMap map = new WorldMap(params, jungleRandom);

        map.growPlants(1);

        assertEquals(1, map.getPlants().size());
        Vector2d plantPos = map.getPlants().keySet().iterator().next();

        assertTrue(plantPos.y() >= 40 && plantPos.y() < 60);
    }

    @Test
    void growPlantsShouldPlacePlantInSteppeWhenRandomFavorsIt() {
        int height = 100;
        SimulationParameters params = createParams(10, height, false, 0.0);

        Random steppeRandom = new Random() {
            @Override
            public double nextDouble() {
                return 0.9;
            }

            @Override
            public boolean nextBoolean() {
                return true;
            }

            @Override
            public int nextInt(int bound) {
                return 0;
            }
        };

        WorldMap map = new WorldMap(params, steppeRandom);

        map.growPlants(1);

        Vector2d plantPos = map.getPlants().keySet().iterator().next();
        assertTrue(plantPos.y() < 40);
    }

    @Test
    void growPlantsShouldCreatePoisonousPlantsOnPoisonMap() {
        SimulationParameters params = createParams(10, 10, true, 1.0);

        Random poisonRandom = new Random() {
            @Override
            public double nextDouble() {
                return 0.0;
            }
            @Override
            public int nextInt(int bound) { return 0; }
        };

        WorldMap map = new WorldMap(params, poisonRandom);

        map.growPlants(1);

        Plant plant = map.getPlants().values().iterator().next();
        assertTrue(plant.isPoisonous());
    }

    @Test
    void growPlantsShouldNotExceedMapCapacity() {
        SimulationParameters params = createParams(3, 3, false, 0.0);
        WorldMap map = new WorldMap(params, new Random());

        map.growPlants(20);

        assertEquals(9, map.getPlants().size());
    }

    @Test
    void initializeAnimalsDataShouldCreateCorrectAmount() {
        SimulationParameters params = createParams(10, 10, false, 0);
        WorldMap map = new WorldMap(params, new Random());

        List<ChildData> data = map.initializeAnimalsData(5);

        assertEquals(5, data.size());
    }

    @Test
    void addAnimalShouldAddAnimals() {
        SimulationParameters params = createParams(10, 10, false, 0);
        WorldMap map = new WorldMap(params, new Random());
        ChildData data = ChildData.random(10, 10, 10, 5, new Random());
        RuntimeAnimal animal = RuntimeAnimal.fromChildData(1, data, 0, new Random());

        map.addAnimal(animal);
        assertEquals(1, map.getAnimals().size());
        assertTrue(map.getAnimals().contains(animal));
    }

    @Test
    void removeAnimalShouldRemoveAnimals() {
        SimulationParameters params = createParams(10, 10, false, 0);
        WorldMap map = new WorldMap(params, new Random());
        ChildData data = ChildData.random(10, 10, 10, 5, new Random());
        RuntimeAnimal animal = RuntimeAnimal.fromChildData(1, data, 0, new Random());

        map.removeAnimal(animal);
        assertEquals(0, map.getAnimals().size());
    }

    @Test
    void removePlantShouldRemovePlantFromGivenPosition() {
        SimulationParameters params = createParams(10, 10, false, 0);
        WorldMap map = new WorldMap(params, new Random());

        map.growPlants(1);
        Vector2d plantPos = map.getPlants().keySet().iterator().next();
        map.removePlant(plantPos);

        assertTrue(map.getPlants().isEmpty());
    }
}