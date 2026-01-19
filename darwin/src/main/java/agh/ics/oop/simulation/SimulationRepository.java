package agh.ics.oop.simulation;

import agh.ics.oop.model.AnimalDetails;
import agh.ics.oop.model.Genotype;
import agh.ics.oop.model.TrackedAnimalStats;
import agh.ics.oop.model.Vector2d;

import java.util.*;
import java.util.stream.Collectors;

class SimulationRepository {
    private final List<AnimalDetails> details = new ArrayList<>();
    private final List<Integer> deathDays = new ArrayList<>();
    private final List<List<Integer>> children = new ArrayList<>();
    private final List<List<Integer>> eatenPlants = new ArrayList<>();

    private final Map<Genotype, Set<Integer>> genotypeFollowers = new HashMap<>();

    private long totalDeadLifeSpan = 0;
    private int deadCount = 0;

    RuntimeAnimal registerBirth(ChildData childData, int day, Random random) {
        int newId = details.size();

        AnimalDetails newDetails = new AnimalDetails(
                childData.genotype(),
                day,
                childData.parentAId(),
                childData.parentBId()
        );
        details.add(newDetails);
        deathDays.add(null);
        children.add(new ArrayList<>());
        eatenPlants.add(new ArrayList<>());

        if (childData.parentAId() != null) children.get(childData.parentAId()).add(newId);
        if (childData.parentBId() != null) children.get(childData.parentBId()).add(newId);

        genotypeFollowers.computeIfAbsent(childData.genotype(), k -> new HashSet<>()).add(newId);

        return RuntimeAnimal.fromChildData(newId, childData, day, random);
    }

    void registerDeath(int id, int day) {
        deathDays.set(id, day);

        AnimalDetails attr = details.get(id);
        int lifeSpan = day - attr.birthDay();
        totalDeadLifeSpan += lifeSpan;
        deadCount++;

        Set<Integer> followers = genotypeFollowers.get(attr.genotype());
        if (followers != null) {
            followers.remove(id);
            if (followers.isEmpty()) {
                genotypeFollowers.remove(attr.genotype());
            }
        }
    }

    void registerPlantConsumption(int id, int day) {
        eatenPlants.get(id).add(day);
    }

    Optional<TrackedAnimalStats> getTrackedAnimalStats(int id, int dayOfInterest, RuntimeAnimal currentRuntimeState) {
        if (id < 0 || id >= details.size()) return Optional.empty();

        AnimalDetails staticDetails = details.get(id);
        if (staticDetails.birthDay() > dayOfInterest) return Optional.empty();

        Integer actualDeathDay = deathDays.get(id);
        boolean isDeadAtThatDay = actualDeathDay != null && actualDeathDay <= dayOfInterest;

        int age = isDeadAtThatDay ? actualDeathDay - staticDetails.birthDay() : dayOfInterest - staticDetails.birthDay();

        int plantsEatenCount = (int) eatenPlants.get(id).stream()
                .filter(day -> day <= dayOfInterest)
                .count();

        Set<Integer> childrenIds = children.get(id).stream()
                .filter(childId -> details.get(childId).birthDay() <= dayOfInterest)
                .collect(Collectors.toSet());

        Set<Integer> descendantsIds = findAllDescendants(id, dayOfInterest);

        int energy = 0;
        int activeGene = 0;
        if (!isDeadAtThatDay && currentRuntimeState != null && currentRuntimeState.getId() == id) {
            energy = currentRuntimeState.getEnergy();
        }
        if (!isDeadAtThatDay) {
            activeGene = staticDetails.genotype().getGene(age);
        }

        return Optional.of(new TrackedAnimalStats(
                id,
                staticDetails.genotype(),
                activeGene,
                energy,
                plantsEatenCount,
                childrenIds,
                descendantsIds,
                age,
                isDeadAtThatDay ? Optional.of(actualDeathDay) : Optional.empty()
        ));
    }

    private Set<Integer> findAllDescendants(int rootId, int limitDay) {
        Set<Integer> descendants = new HashSet<>();
        Queue<Integer> toVisit = new LinkedList<>();

        List<Integer> firstLevel = children.get(rootId).stream()
                .filter(cid -> details.get(cid).birthDay() <= limitDay)
                .toList();

        toVisit.addAll(firstLevel);

        while (!toVisit.isEmpty()) {
            Integer currentId = toVisit.poll();

            if (descendants.contains(currentId)) {
                continue;
            }

            descendants.add(currentId);

            List<Integer> nextGeneration = children.get(currentId).stream()
                    .filter(cid -> details.get(cid).birthDay() <= limitDay)
                    .toList();

            toVisit.addAll(nextGeneration);
        }

        return descendants;
    }

    SimulationStats getStats(Set<RuntimeAnimal> liveAnimals, Set<Vector2d> plantsPositions, int width, int height) {
        return StatisticsCalculator.calculate(
                liveAnimals,
                plantsPositions,
                this.genotypeFollowers,
                this.deadCount,
                this.totalDeadLifeSpan,
                width,
                height
        );
    }

    Optional<AnimalDetails> getDetails(int id) {
        if (id < 0 || id >= details.size()) return Optional.empty();
        return Optional.of(details.get(id));
    }
}