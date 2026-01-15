package agh.ics.oop.simulation.handlers;

import java.util.Map;
import java.util.Random;

import agh.ics.oop.model.Plant;
import agh.ics.oop.model.Vector2d;
import agh.ics.oop.simulation.SimulationParameters;

public class GrowthHandler {
    private final int width;
    private final int height;
    private final int dailyPlantGrowth;

    private final Map<Vector2d, Plant> plants;

    private final Random random;

    public GrowthHandler(SimulationParameters parameters, Map<Vector2d, Plant> plants, Random random) {
        this.width = parameters.width();
        this.height = parameters.height();
        this.dailyPlantGrowth = parameters.dailyPlantGrowth();

        this.plants = plants;

        this.random = random;

        spawn(parameters.initialPlantCount());
    }

    private void spawn(int initialPlantCount) {
        // TODO:
    }

    public void handle() {
        // TODO:
    }
}
