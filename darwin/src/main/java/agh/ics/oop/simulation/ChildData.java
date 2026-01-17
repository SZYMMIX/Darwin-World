package agh.ics.oop.simulation;

import agh.ics.oop.model.Genotype;
import agh.ics.oop.model.Vector2d;

record ChildData(
        Vector2d position,
        int initialEnergy,
        Genotype genotype,
        int parentAId,
        int parentBId
) {}