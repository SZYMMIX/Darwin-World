package agh.ics.oop.simulation.handlers;

import java.util.HashMap;

import agh.ics.oop.model.Plant;
import agh.ics.oop.model.Vector2d;
import agh.ics.oop.simulation.SimulationParameters;

public class GrowthHandler {
    private final int width;
    private final int height;
    private final int dailyPlantGrowth;

    private final HashMap<Vector2d, Plant> plants;

    public GrowthHandler(SimulationParameters parameters, HashMap<Vector2d, Plant> plants) {
        this.width = parameters.width();
        this.height = parameters.height();
        this.dailyPlantGrowth = parameters.dailyPlantGrowth();

        this.plants = plants;

        spawn(parameters.initialPlantCount());
    }

    private void spawn(int initialPlantCount) {
        // TODO:
    }

    public void handle() {
        // TODO:
    }
}
