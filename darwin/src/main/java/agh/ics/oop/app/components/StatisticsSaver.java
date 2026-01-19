package agh.ics.oop.app.components;

import agh.ics.oop.simulation.SimulationStats;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class StatisticsSaver {

    public static void saveToFile(File file, List<SimulationStats> statsHistory) {
        if (file == null || statsHistory == null) return;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("Dzien;Zwierzeta;Rosliny;WolnePola;SredniaEnergia;SredniaDlugoscZycia;SredniaLiczbaDzieci");
            writer.newLine();

            int day = 0;
            for (SimulationStats stats : statsHistory) {
                String line = String.format("%d;%d;%d;%d;%.2f;%.2f;%.2f",
                        day++,
                        stats.currentAnimalsCount(),
                        stats.currentPlantsCount(),
                        stats.freeFieldsCount(),
                        stats.averageEnergy(),
                        stats.averageLifespan(),
                        stats.averageChildren()
                ).replace(',', '.');

                writer.write(line);
                writer.newLine();
            }

            System.out.println("Zapisano statystyki do: " + file.getAbsolutePath());

        } catch (IOException e) {
            System.err.println("Błąd zapisu statystyk: " + e.getMessage());
        }
    }
}