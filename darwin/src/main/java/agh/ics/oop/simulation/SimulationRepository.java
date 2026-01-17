package agh.ics.oop.simulation;

import agh.ics.oop.model.AnimalDetails;
import agh.ics.oop.model.Genotype;
import agh.ics.oop.model.Vector2d;

import java.util.*;

class SimulationRepository {
    private final List<AnimalDetails> details = new ArrayList<>();
    private final List<Integer> deathDays = new ArrayList<>();
    private final List<List<Integer>> children = new ArrayList<>();

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

    SimulationStats getStats(Set<RuntimeAnimal> liveAnimals, Set<Vector2d> plantsPositions, int width, int height) {
        double avgEnergy = 0.0;
        double avgChildren = 0.0;

        if (!liveAnimals.isEmpty()) {
            long energySum = 0;
            long childrenSum = 0;
            for (RuntimeAnimal a : liveAnimals) {
                energySum += a.getEnergy();
                childrenSum += a.getChildrenCount();
            }
            avgEnergy = (double) energySum / liveAnimals.size();
            avgChildren = (double) childrenSum / liveAnimals.size();
        }

        double avgLifespan = deadCount == 0 ? 0.0 : (double) totalDeadLifeSpan / deadCount;

        Set<Vector2d> occupiedPositions = new HashSet<>(plantsPositions);
        for (RuntimeAnimal a : liveAnimals) {
            occupiedPositions.add(a.getPosition());
        }
        int totalFields = width * height;
        int freeFields = Math.max(0, totalFields - occupiedPositions.size());

        List<GenotypeStat> topGenotypes = genotypeFollowers.entrySet().stream()
                .filter(entry -> !entry.getValue().isEmpty())
                .sorted((e1, e2) -> Integer.compare(e2.getValue().size(), e1.getValue().size()))
                .limit(5)
                .map(entry -> new GenotypeStat(
                        entry.getKey(),
                        new ArrayList<>(entry.getValue())
                ))
                .toList();

        return new SimulationStats(
                liveAnimals.size(),
                plantsPositions.size(),
                freeFields,
                topGenotypes,
                avgEnergy,
                avgLifespan,
                avgChildren
        );
    }

    Optional<AnimalDetails> getDetails(int id) {
        if (id < 0 || id >= details.size()) return Optional.empty();
        return Optional.of(details.get(id));
    }
}