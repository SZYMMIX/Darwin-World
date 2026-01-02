package agh.ics.oop.simulation;

public record SimulationParameters(
        int width,
        int height,
        int initialPlantCount,
        int plantEnergy,
        int dailyPlantGrowth,
        int initialAnimalCount,
        int initialAnimalEnergy,
        int dailyEnergyCost,
        int reproductionEnergyMin,
        int reproductionEnergyCost,
        int minMutations,
        int maxMutations,
        int genomeLength,

        boolean isPoisonMap,
        double poisonProbability,
        int poisonEnergyCost
) {
}