package agh.ics.oop.simulation;

import agh.ics.oop.model.Genotype;
import java.util.List;

public record SimulationStats(
        int currentAnimalsCount,
        int currentPlantsCount,
        int freeFieldsCount,
        List<GenotypeStat> topGenotypes,
        double averageEnergy,
        double averageLifespan,
        double averageChildren
) {}