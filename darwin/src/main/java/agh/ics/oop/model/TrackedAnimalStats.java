package agh.ics.oop.model;

import java.util.Optional;

public record TrackedAnimalStats(
        int id,
        Genotype genotype,
        int activeGene,
        int energy,
        int eatenPlants,
        int childrenCount,
        int descendantsCount,
        int age,
        Optional<Integer> deathDay
) {}