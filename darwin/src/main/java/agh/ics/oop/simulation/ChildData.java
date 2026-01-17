package agh.ics.oop.simulation;

import agh.ics.oop.model.Genotype;
import agh.ics.oop.model.Vector2d;

import java.util.Random;

record ChildData(
        Vector2d position,
        int initialEnergy,
        Genotype genotype,
        Integer parentAId,
        Integer parentBId
) {
    static ChildData random(int width, int height, int initialAnimalEnergy, int genotypeLength, Random random) {
        Vector2d randomPosition = new Vector2d(random.nextInt(width), random.nextInt(height));
        Genotype randomGenotype = Genotype.random(genotypeLength, random);

        return new ChildData(
                randomPosition,
                initialAnimalEnergy,
                randomGenotype,
                null,
                null
        );
    }
}