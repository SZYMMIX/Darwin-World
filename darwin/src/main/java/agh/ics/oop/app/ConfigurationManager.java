package agh.ics.oop.app;

import agh.ics.oop.simulation.SimulationParameters;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConfigurationManager {
    private static final String CONFIG_DIR = "darwin_config";

    public ConfigurationManager() {
        try {
            Files.createDirectories(Paths.get(CONFIG_DIR));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveConfiguration(String name, SimulationParameters params) throws IOException {
        Path path = Paths.get(CONFIG_DIR, name + ".cfg");

        String content = String.format("""
                width=%d
                height=%d
                initialPlantCount=%d
                plantEnergy=%d
                dailyPlantGrowth=%d
                initialAnimalCount=%d
                initialAnimalEnergy=%d
                dailyEnergyCost=%d
                reproductionEnergyMin=%d
                reproductionEnergyCost=%d
                minMutations=%d
                maxMutations=%d
                genotypeLength=%d
                isPoisonMap=%b
                poisonProbability=%s
                poisonEnergyCost=%d
                """,
                params.width(), params.height(),
                params.initialPlantCount(), params.plantEnergy(), params.dailyPlantGrowth(),
                params.initialAnimalCount(), params.initialAnimalEnergy(),
                params.dailyEnergyCost(), params.reproductionEnergyMin(), params.reproductionEnergyCost(),
                params.minMutations(), params.maxMutations(), params.genotypeLength(),
                params.isPoisonMap(), params.poisonProbability(), params.poisonEnergyCost()
        );

        Files.writeString(path, content, StandardCharsets.UTF_8);
    }

    public SimulationParameters loadConfiguration(String name) throws IOException {
        Path path = Paths.get(CONFIG_DIR, name + ".cfg");
        List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);

        var map = lines.stream()
                .map(line -> line.split("="))
                .filter(arr -> arr.length == 2)
                .collect(Collectors.toMap(arr -> arr[0].trim(), arr -> arr[1].trim()));

        return new SimulationParameters(
                Integer.parseInt(map.get("width")),
                Integer.parseInt(map.get("height")),
                Integer.parseInt(map.get("initialPlantCount")),
                Integer.parseInt(map.get("plantEnergy")),
                Integer.parseInt(map.get("dailyPlantGrowth")),
                Integer.parseInt(map.get("initialAnimalCount")),
                Integer.parseInt(map.get("initialAnimalEnergy")),
                Integer.parseInt(map.get("dailyEnergyCost")),
                Integer.parseInt(map.get("reproductionEnergyMin")),
                Integer.parseInt(map.get("reproductionEnergyCost")),
                Integer.parseInt(map.get("minMutations")),
                Integer.parseInt(map.get("maxMutations")),
                Integer.parseInt(map.get("genotypeLength")),
                Boolean.parseBoolean(map.get("isPoisonMap")),
                Double.parseDouble(map.get("poisonProbability")),
                Integer.parseInt(map.get("poisonEnergyCost"))
        );
    }

    public List<String> getAvailablePresets() {
        try (Stream<Path> stream = Files.list(Paths.get(CONFIG_DIR))) {
            return stream
                    .filter(file -> !Files.isDirectory(file))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .filter(name -> name.endsWith(".cfg"))
                    .map(name -> name.replace(".cfg", ""))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            return List.of();
        }
    }
}