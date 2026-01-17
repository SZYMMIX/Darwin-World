package agh.ics.oop.simulation;

import agh.ics.oop.model.Plant;
import agh.ics.oop.model.Vector2d;

import java.util.*;

class WorldMap {
    private final int width;
    private final int height;
    private final SimulationParameters params;
    private final Random random;

    private final Map<Vector2d, Plant> plants;
    private final Set<RuntimeAnimal> animals;

    private final int jungleYMin;
    private final int jungleYMax;

    WorldMap(SimulationParameters params, Random random) {
        this.params = params;
        this.width = params.width();
        this.height = params.height();
        this.random = random;

        this.plants = new HashMap<>();
        this.animals = new HashSet<>();

        int jungleHeight = Math.max(1, (int) (height * 0.2));
        this.jungleYMin = (height - jungleHeight) / 2;
        this.jungleYMax = jungleYMin + jungleHeight;
    }

    void growPlants(int plantsToGrow) {
        int attempts = 0;
        int maxAttempts = plantsToGrow * 10;
        int grown = 0;

        while (grown < plantsToGrow && attempts < maxAttempts) {
            Vector2d pos = getRandomPositionWithJungleBias();

            if (!plants.containsKey(pos)) {
                boolean isPoisonous = shouldPlantBePoisonous(pos);
                plants.put(pos, new Plant(isPoisonous));
                grown++;
            }
            attempts++;
        }
    }

    List<ChildData> initializeAnimalsData(int count) {
        List<ChildData> initialData = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            initialData.add(ChildData.random(width, height, params.initialAnimalEnergy(), params.genotypeLength(), random));
        }
        return initialData;
    }

    private Vector2d getRandomPositionWithJungleBias() {
        boolean chooseJungle = random.nextDouble() < 0.8;
        int y;

        if (chooseJungle) {
            y = jungleYMin + random.nextInt(jungleYMax - jungleYMin);
        } else {
            if (random.nextBoolean()) {
                y = random.nextInt(jungleYMin);
            } else {
                int bottomHeight = height - jungleYMax;
                y = (bottomHeight > 0) ? jungleYMax + random.nextInt(bottomHeight) : random.nextInt(height);
            }
        }

        int x = random.nextInt(width);
        return new Vector2d(x, y);
    }

    private boolean shouldPlantBePoisonous(Vector2d pos) {
        if (!params.isPoisonMap()) return false;
        return random.nextDouble() < params.poisonProbability();
    }

    void addAnimal(RuntimeAnimal animal) {
        animals.add(animal);
    }

    void removeAnimal(RuntimeAnimal animal) {
        animals.remove(animal);
    }

    void removePlant(Vector2d position) {
        plants.remove(position);
    }

    Set<RuntimeAnimal> getAnimals() {
        return Collections.unmodifiableSet(animals);
    }

    Map<Vector2d, Plant> getPlants() {
        return Collections.unmodifiableMap(plants);
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
}