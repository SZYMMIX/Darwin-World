package agh.ics.oop.model;

import agh.ics.oop.model.Direction;
import agh.ics.oop.model.Vector2d;

public record AnimalSnapshot(
        int id,
        Vector2d position,
        Direction direction,
        int energy
) {}