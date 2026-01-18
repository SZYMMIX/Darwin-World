package agh.ics.oop.model;

import java.util.Optional;
import java.util.Set;

public record TrackedAnimalStats(
        int id,
        Genotype genotype,
        int activeGene,
        int energy,
        int eatenPlants,
        Set<Integer> childrenIds,
        Set<Integer> descendantsIds,
        int age,
        Optional<Integer> deathDay
) {}