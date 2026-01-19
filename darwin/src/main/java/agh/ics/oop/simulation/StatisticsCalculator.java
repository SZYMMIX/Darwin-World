package agh.ics.oop.simulation;

import agh.ics.oop.model.Genotype;
import agh.ics.oop.model.Vector2d;

import java.util.*;

public class StatisticsCalculator {

    public static SimulationStats calculate(
            Collection<RuntimeAnimal> liveAnimals,
            Set<Vector2d> plantsPositions,
            Map<Genotype, Set<Integer>> genotypeFollowers,
            int deadCount,
            long totalDeadLifeSpan,
            int width,
            int height
    ) {
        double avgEnergy = 0.0;
        double avgChildren = 0.0;

        if (!liveAnimals.isEmpty()) {
            double totalEnergy = 0;
            double totalChildren = 0;
            for (RuntimeAnimal a : liveAnimals) {
                totalEnergy += a.getEnergy();
                totalChildren += a.getChildrenCount();
            }
            avgEnergy = totalEnergy / liveAnimals.size();
            avgChildren = totalChildren / liveAnimals.size();
        }

        double avgLifespan = (deadCount == 0) ? 0.0 : (double) totalDeadLifeSpan / deadCount;

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
}