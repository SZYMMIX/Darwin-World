package agh.ics.oop.app.components;

import javafx.scene.paint.Color;

public class EnergyColorProvider {
    private static final Color COLOR_LOW_ENERGY = Color.RED;
    private static final Color COLOR_HIGH_ENERGY = Color.web("#4e342e");
    private static final Color COLOR_DEAD = Color.web("#dddddd");

    public static Color calculateColor(int energy, int maxEnergyReference) {
        if (energy <= 0) return COLOR_DEAD;

        double maxVisualEnergy = maxEnergyReference * 2.0;
        double ratio = Math.min(1.0, (double) energy / maxVisualEnergy);

        return COLOR_LOW_ENERGY.interpolate(COLOR_HIGH_ENERGY, ratio);
    }
}