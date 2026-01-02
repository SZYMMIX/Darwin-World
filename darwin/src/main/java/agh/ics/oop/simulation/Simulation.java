package agh.ics.oop.simulation;

import agh.ics.oop.model.Animal;
import agh.ics.oop.model.Genotype;
import agh.ics.oop.model.Plant;
import agh.ics.oop.model.Vector2d;

import java.util.*;

public class Simulation {
    private final SimulationConfig config;
    private final Random random = new Random();

    private List<Animal> animals = new ArrayList<>();
    private final Map<Vector2d, Plant> plants = new HashMap<>();

    private final List<Animal> deadAnimals = new ArrayList<>();
    private int currentDay = 0;

    public Simulation(SimulationParameters params) {
        Genotype immunityGenotype = null;
        if (params.isPoisonMap()) {
            immunityGenotype = Genotype.random(params.genomeLength(), random);
        }

        this.config = new SimulationConfig(
                params.width(),
                params.height(),
                params.plantEnergy(),
                params.dailyEnergyCost(),
                params.dailyPlantGrowth(),
                params.reproductionEnergyMin(),
                params.reproductionEnergyCost(),
                params.minMutations(),
                params.maxMutations(),
                params.genomeLength(),
                params.isPoisonMap(),
                params.poisonProbability(),
                params.poisonEnergyCost(),
                immunityGenotype
        );

        initializeAnimals(params.initialAnimalCount(), params.initialAnimalEnergy());
        initializePlants(params.initialPlantCount());
    }

    private void initializeAnimals(int count, int initialEnergy) {
        for (int i = 0; i < count; i++) {
            Vector2d position = generateRandomPosition();
            Animal animal = Animal.createRandom(
                    position,
                    initialEnergy,
                    config.genomeLength(),
                    0,
                    random
            );
            animals.add(animal);
        }
    }

    private void initializePlants(int count) {
        // TODO: Jungle logic
        for (int i = 0; i < count; i++) {
            Vector2d position = generateRandomPosition();
            if (!plants.containsKey(position)) {
                boolean isPoison = config.isPoisonMap() && random.nextDouble() < config.poisonProbability();
                plants.put(position, new Plant(isPoison));
            }
        }
    }

    private Vector2d generateRandomPosition() {
        return new Vector2d(
                random.nextInt(config.width()),
                random.nextInt(config.height())
        );
    }

    public void step() {
        removeDeadAnimals();
        moveAnimals();
        eatPlants();
        reproduceAnimals();
        growPlants();

        subtractDailyEnergy();

        currentDay++;
    }

    private void removeDeadAnimals() {
        Iterator<Animal> iterator = animals.iterator();
        while (iterator.hasNext()) {
            Animal animal = iterator.next();
            if (animal.isDead()) {
                animal.markAsDead(currentDay);
                deadAnimals.add(animal);
                iterator.remove();
            }
        }
    }

    private void moveAnimals() {
        for (Animal animal : animals) {
            animal.move(config.width(), config.height(), currentDay);
        }
    }

    private void subtractDailyEnergy() {
        for (Animal animal : animals) {
            animal.subtractEnergy(config.dailyEnergyCost());
        }
    }

    private void eatPlants() {
        // TODO: In next task
    }

    private void reproduceAnimals() {
        // TODO: In next task
    }

    private void growPlants() {
        initializePlants(config.dailyPlantGrowth());
    }

    public synchronized List<Animal> getAnimals() {
        return new ArrayList<>(animals);
    }

    public synchronized Map<Vector2d, Plant> getPlants() {
        return new HashMap<>(plants);
    }

    public SimulationConfig getConfig() {
        return config;
    }

    public int getCurrentDay() {
        return currentDay;
    }
}