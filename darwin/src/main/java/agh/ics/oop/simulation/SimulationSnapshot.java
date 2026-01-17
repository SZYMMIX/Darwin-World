package agh.ics.oop.simulation;

import agh.ics.oop.model.AnimalSnapshot;
import agh.ics.oop.model.Plant;
import agh.ics.oop.model.Vector2d;

import java.util.List;
import java.util.Map;

public record SimulationSnapshot(
        int day,
        List<AnimalSnapshot> animals,
        Map<Vector2d, Plant> plants,
        SimulationStats stats
) {}