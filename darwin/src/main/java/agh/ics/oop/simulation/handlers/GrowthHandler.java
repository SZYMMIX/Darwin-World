package agh.ics.oop.simulation.handlers;

import java.util.HashMap;

import agh.ics.oop.model.Plant;
import agh.ics.oop.model.Vector2d;
import agh.ics.oop.simulation.SimulationParameters;

public class GrowthHandler {
    private final int dailyPlantGrowth;

    public GrowthHandler(SimulationParameters parameters) {
        this.dailyPlantGrowth = parameters.dailyPlantGrowth();
    }

    public HashMap<Vector2d, Plant> spawn(int initialPlantCount) {
        // TODO:
        return null;
    }

    public void handle(HashMap<Vector2d, Plant> plans) {
        // TODO:
    }
}
