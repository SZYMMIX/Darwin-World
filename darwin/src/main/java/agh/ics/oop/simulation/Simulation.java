package agh.ics.oop.simulation;

import agh.ics.oop.model.*;
import agh.ics.oop.util.VisibleForTests;

import java.util.*;

public class Simulation {
    private final SimulationParameters params;
    private final SimulationRepository repository;
    private final WorldMap map;
    private final InteractionHandler interactionHandler;
    private final Random random;

    private int currentDay = 0;

    @VisibleForTests
    Simulation(SimulationParameters params, Random random) {
        this.params = params;
        this.repository = new SimulationRepository();
        this.random = random;

        this.map = new WorldMap(params, random);
        this.interactionHandler = new InteractionHandler(params, random);

        spawnInitialAnimals();
        spawnInitialPlants();
    }

    public Simulation(SimulationParameters params) {
        this(params, new Random());
    }

    public SimulationSnapshot step() {
        currentDay++;
        removeDead();
        moveAnimals();
        eatAndReproduce();
        map.growPlants(params.dailyPlantGrowth());
        applyDailyEnergyCost();
        return createSnapshot();
    }

    public SimulationSnapshot getSnapshot() {
        return createSnapshot();
    }

    public Optional<TrackedAnimalStats> getAnimalDetails(int animalId, int dayOfInterest) {
        RuntimeAnimal runtimeAnimal = map.getAnimals().stream()
                .filter(a -> a.getId() == animalId)
                .findFirst()
                .orElse(null);

        return repository.getTrackedAnimalStats(animalId, dayOfInterest, runtimeAnimal);
    }

    private void removeDead() {
        List<RuntimeAnimal> deadAnimals = map.getAnimals().stream()
                .filter(RuntimeAnimal::isDead)
                .toList();

        for (RuntimeAnimal dead : deadAnimals) {
            repository.registerDeath(dead.getId(), currentDay);
            map.removeAnimal(dead);
        }
    }

    private void moveAnimals() {
        int width = map.getWidth();
        int height = map.getHeight();

        for (RuntimeAnimal animal : map.getAnimals()) {
            animal.move(width, height, currentDay);
        }
    }

    private void eatAndReproduce() {
        List<ChildData> newBirths = new ArrayList<>();

        Map<Vector2d, List<RuntimeAnimal>> conflicts = RuntimeAnimal.getSortedGroups(map.getAnimals());
        Map<Vector2d, Plant> plants = map.getPlants();

        for (var entry : conflicts.entrySet()) {
            Vector2d position = entry.getKey();
            List<RuntimeAnimal> animalsOnField = entry.getValue();
            Plant plant = plants.get(position);

            var result = interactionHandler.handleInteractions(animalsOnField, plant);

            if (result.plantEaten()) {
                map.removePlant(position);
                repository.registerPlantConsumption(animalsOnField.get(0).getId(), currentDay);
            }

            newBirths.addAll(result.newChildren());
        }

        registerNewChildren(newBirths);
    }

    private void registerNewChildren(List<ChildData> births) {
        for (ChildData data : births) {
            RuntimeAnimal child = repository.registerBirth(data, currentDay, random);
            map.addAnimal(child);
        }
    }

    private void applyDailyEnergyCost() {
        int cost = params.dailyEnergyCost();
        for (RuntimeAnimal animal : map.getAnimals()) {
            animal.subtractEnergy(cost);
        }
    }

    private SimulationSnapshot createSnapshot() {
        SimulationStats stats = repository.getStats(
                map.getAnimals(),
                map.getPlants().keySet(),
                map.getWidth(),
                map.getHeight()
        );

        List<AnimalSnapshot> animalSnapshots = map.getAnimals().stream()
                .map(RuntimeAnimal::getSnapshot)
                .toList();

        return new SimulationSnapshot(
                currentDay,
                animalSnapshots,
                new HashMap<>(map.getPlants()),
                stats
        );
    }

    private void spawnInitialAnimals() {
        List<ChildData> initialData = map.initializeAnimalsData(params.initialAnimalCount());

        for (ChildData data : initialData) {
            RuntimeAnimal animal = repository.registerBirth(data, currentDay, random);
            map.addAnimal(animal);
        }
    }

    private void spawnInitialPlants() {
        map.growPlants(params.initialPlantCount());
    }
}