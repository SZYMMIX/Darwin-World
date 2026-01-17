package agh.ics.oop.model;

public record AnimalDetails(
        Genotype genotype,
        int birthDay,
        Integer parentAId,
        Integer parentBId
) {}