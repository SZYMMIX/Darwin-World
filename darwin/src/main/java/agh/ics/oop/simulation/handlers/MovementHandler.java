package agh.ics.oop.simulation.handlers;

import java.util.List;

import agh.ics.oop.model.Animal;
import agh.ics.oop.simulation.SimulationParameters;

public class MovementHandler {
    private final int width;
    private final int height;

    private final List<Animal> animals;

    public MovementHandler(SimulationParameters parameters, List<Animal> animals) {
        this.width = parameters.width();
        this.height = parameters.height();

        this.animals = animals;
    }

    public void handle() {
        // TODO:
    }
}
