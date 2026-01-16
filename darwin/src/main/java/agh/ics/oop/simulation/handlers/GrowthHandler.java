package agh.ics.oop.simulation.handlers;

import java.util.*;

import agh.ics.oop.model.Plant;
import agh.ics.oop.model.Vector2d;
import agh.ics.oop.simulation.SimulationParameters;

public class GrowthHandler {
    private final int width;
    private final int height;
    private final int dailyPlantGrowth;

    private final boolean isPoisonMap;
    private final double poisonProbability;

    private final Map<Vector2d, Plant> plants;
    private final Set<Vector2d> allJunglePositions;
    private final Set<Vector2d> allSteppePositions;

    private final Random random;

    public GrowthHandler(SimulationParameters parameters, Map<Vector2d, Plant> plants, Random random) {
        this.width = parameters.width();
        this.height = parameters.height();
        this.dailyPlantGrowth = parameters.dailyPlantGrowth();
        this.isPoisonMap = parameters.isPoisonMap();
        this.poisonProbability = parameters.poisonProbability();

        this.plants = plants;

        this.random = random;

        int jungleHeight = (int) (height * 0.2);

        int jungleYMin = (height - jungleHeight) / 2;
        int jungleYMax = jungleYMin + jungleHeight - 1;

        this.allJunglePositions = new HashSet<>();
        this.allSteppePositions = new HashSet<>();

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Vector2d pos = new Vector2d(x, y);
                if (y >= jungleYMin && y <= jungleYMax) {
                    allJunglePositions.add(pos);
                } else {
                    allSteppePositions.add(pos);
                }
            }
        }

        spawn(parameters.initialPlantCount());
    }

    private void spawn(int initialPlantCount) {
        growPlants(initialPlantCount);
    }

    public void handle() {
        growPlants(dailyPlantGrowth);
    }

    private void growPlants(int count) {
        for (int i = 0; i < count; i++) {
            if (random.nextDouble() < 0.8) {
                boolean planted = plantRandomlyInZone(allJunglePositions);
                if (!planted) {
                    plantRandomlyInZone(allSteppePositions);
                }
            } else {
                boolean planted = plantRandomlyInZone(allSteppePositions);
                if (!planted) {
                    plantRandomlyInZone(allJunglePositions);
                }
            }
        }
    }

    private boolean plantRandomlyInZone(Set<Vector2d> allZonePositions) {
        Set<Vector2d> freePositions = new HashSet<>(allZonePositions);

        freePositions.removeAll(plants.keySet());

        if (!freePositions.isEmpty()) {
            List<Vector2d> freeList = new ArrayList<>(freePositions);

            int randomIndex = random.nextInt(freeList.size());
            Vector2d position = freeList.get(randomIndex);

            plantAt(position);
            return true;
        }
        return false;
    }

    private void plantAt(Vector2d position) {
        boolean isPoisonous = false;
        if (isPoisonMap) {
            isPoisonous = random.nextDouble() < poisonProbability;
        }
        plants.put(position, new Plant(isPoisonous));
    }

}
