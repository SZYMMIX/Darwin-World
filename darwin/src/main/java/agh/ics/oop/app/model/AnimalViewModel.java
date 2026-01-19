package agh.ics.oop.app.model;

import agh.ics.oop.model.Genotype;
import agh.ics.oop.model.Vector2d;
import java.util.Optional;

public record AnimalViewModel(
        int id,
        Vector2d position,
        Genotype genotype,
        int activeGeneIndex,
        int energy,
        int eatenPlants,
        int childrenCount,
        int descendantsCount,
        int age,
        Optional<Integer> deathDay,
        boolean isDead
) {
    public static AnimalViewModel empty() {
        return new AnimalViewModel(-1, null, null, 0, 0, 0, 0, 0, 0, Optional.empty(), false);
    }
}