package agh.ics.oop.simulation;

import agh.ics.oop.model.Genotype;

public record SimulationConfig(
        int width,
        int height,
        int plantEnergy,
        int dailyPlantGrowth,
        int dailyEnergyCost,
        int reproductionEnergyMin,
        int reproductionEnergyCost,
        int minMutations,
        int maxMutations,
        int genomeLength,

        boolean isPoisonMap,
        double poisonProbability,
        int poisonEnergyCost,
        Genotype immunityGenotype
) {
}