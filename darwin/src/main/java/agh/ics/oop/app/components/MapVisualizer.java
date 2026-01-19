package agh.ics.oop.app.components;

import agh.ics.oop.model.AnimalSnapshot;
import agh.ics.oop.model.Plant;
import agh.ics.oop.model.Vector2d;
import agh.ics.oop.simulation.SimulationSnapshot;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.util.*;
import java.util.stream.Collectors;

public class MapVisualizer extends Pane {
    private final Canvas canvas;
    private final GraphicsContext gc;
    private final int mapWidth;
    private final int mapHeight;
    private final int reproductionMinEnergy;

    private Integer trackedAnimalId = null;
    private Set<Integer> animalsWithDominantGenotype = new HashSet<>();
    private Set<Integer> highlightedChildren = new HashSet<>();
    private Set<Integer> highlightedDescendants = new HashSet<>();

    private SimulationSnapshot lastSnapshot = null;

    private static final Color COLOR_LOW_ENERGY = Color.RED;
    private static final Color COLOR_HIGH_ENERGY = Color.web("#4e342e");
    private static final Color COLOR_DEAD = Color.web("#dddddd");

    public MapVisualizer(int mapWidth, int mapHeight, int reproductionMinEnergy) {
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.reproductionMinEnergy = reproductionMinEnergy;

        this.canvas = new Canvas();
        this.gc = canvas.getGraphicsContext2D();

        canvas.widthProperty().bind(widthProperty());
        canvas.heightProperty().bind(heightProperty());

        widthProperty().addListener(evt -> drawLastSnapshot());
        heightProperty().addListener(evt -> drawLastSnapshot());

        getChildren().add(canvas);
    }

    public void setTrackedAnimalId(Integer id) {
        this.trackedAnimalId = id;
        drawLastSnapshot();
    }

    public void setAnimalsWithDominantGenotype(List<Integer> animalIds) {
        this.animalsWithDominantGenotype = (animalIds == null) ? new HashSet<>() : new HashSet<>(animalIds);
        drawLastSnapshot();
    }

    public void setHighlightedFamily(Set<Integer> children, Set<Integer> descendants) {
        this.highlightedChildren = (children == null) ? new HashSet<>() : new HashSet<>(children);
        this.highlightedDescendants = (descendants == null) ? new HashSet<>() : new HashSet<>(descendants);
        drawLastSnapshot();
    }

    public void draw(SimulationSnapshot snapshot) {
        this.lastSnapshot = snapshot;
        drawLastSnapshot();
    }

    private void drawLastSnapshot() {
        if (lastSnapshot == null) return;

        double w = getWidth();
        double h = getHeight();
        if (w <= 0 || h <= 0) return;

        gc.clearRect(0, 0, w, h);
        gc.setFill(Color.web("#222"));
        gc.fillRect(0, 0, w, h);

        double cellW = w / mapWidth;
        double cellH = h / mapHeight;
        double cellSize = Math.min(cellW, cellH);

        double drawWidth = mapWidth * cellSize;
        double drawHeight = mapHeight * cellSize;

        double offsetX = (w - drawWidth) / 2.0;
        double offsetY = (h - drawHeight) / 2.0;

        drawTerrains(offsetX, offsetY, cellSize);
        drawPlants(offsetX, offsetY, cellSize);
        drawAnimals(offsetX, offsetY, cellSize);
    }

    private void drawTerrains(double ox, double oy, double sz) {
        gc.setFill(Color.web("#fff9c4"));
        gc.fillRect(ox, oy, mapWidth * sz, mapHeight * sz);

        gc.setFill(Color.web("#c8e6c9"));
        double jungleH = mapHeight * 0.2;
        double jungleY = (mapHeight - jungleH) / 2.0;
        gc.fillRect(ox, oy + (jungleY * sz), mapWidth * sz, jungleH * sz);
    }

    private void drawPlants(double ox, double oy, double sz) {
        for (var entry : lastSnapshot.plants().entrySet()) {
            Vector2d pos = entry.getKey();
            Plant plant = entry.getValue();

            gc.setFill(plant.isPoisonous() ? Color.web("#8e24aa") : Color.FORESTGREEN);

            double pSize = sz * 0.6;
            double pOffset = (sz - pSize) / 2.0;
            gc.fillOval(ox + pos.x() * sz + pOffset, oy + pos.y() * sz + pOffset, pSize, pSize);
        }
    }

    private void drawAnimals(double ox, double oy, double sz) {
        Map<Vector2d, List<AnimalSnapshot>> grouped = lastSnapshot.animals().stream()
                .collect(Collectors.groupingBy(AnimalSnapshot::position));

        for (var entry : grouped.entrySet()) {
            Vector2d pos = entry.getKey();
            List<AnimalSnapshot> group = entry.getValue();
            group.sort((a, b) -> Integer.compare(b.energy(), a.energy()));

            int count = group.size();
            int rows, cols;
            if (count == 1) { rows = 1; cols = 1; }
            else if (count <= 4) { rows = 2; cols = 2; }
            else { rows = 3; cols = 3; }

            double subSize = sz / Math.max(rows, cols);
            int limit = Math.min(count, rows * cols);

            for (int i = 0; i < limit; i++) {
                AnimalSnapshot animal = group.get(i);
                int r = i / cols;
                int c = i % cols;
                double px = ox + (pos.x() * sz) + (c * subSize);
                double py = oy + (pos.y() * sz) + (r * subSize);

                drawSingleAnimal(px, py, subSize, animal);
            }
        }
    }

    private void drawSingleAnimal(double x, double y, double size, AnimalSnapshot animal) {
        double padding = size * 0.05;
        double realSize = size - (2 * padding);
        int aid = animal.id();

        if (trackedAnimalId != null && trackedAnimalId.equals(aid)) {
            gc.setStroke(Color.MAGENTA);
            gc.setLineWidth(Math.max(3.0, size * 0.2));
            gc.strokeOval(x + padding, y + padding, realSize, realSize);
        }

        if (animalsWithDominantGenotype.contains(aid)) {
            gc.setStroke(Color.GOLD);
            gc.setLineWidth(Math.max(2.0, size * 0.175));
            gc.strokeOval(x + padding, y + padding, realSize, realSize);
        }

        if (highlightedChildren.contains(aid)) {
            gc.setStroke(Color.CYAN);
            gc.setLineWidth(Math.max(2.0, size * 0.15));
            gc.strokeOval(x + padding, y + padding, realSize, realSize);
        }
        else if (highlightedDescendants.contains(aid)) {
            gc.setStroke(Color.MEDIUMPURPLE);
            gc.setLineWidth(Math.max(2.0, size * 0.1));
            gc.strokeOval(x + padding, y + padding, realSize, realSize);
        }

        Color bodyColor = calculateColor(animal.energy());
        gc.setFill(bodyColor);
        gc.fillOval(x + padding, y + padding, realSize, realSize);
    }

    private Color calculateColor(int energy) {
        if (energy <= 0) return COLOR_DEAD;

        double maxVisualEnergy = reproductionMinEnergy * 2.0;
        double ratio = Math.min(1.0, (double) energy / maxVisualEnergy);

        return COLOR_LOW_ENERGY.interpolate(COLOR_HIGH_ENERGY, ratio);
    }

    public Integer getAnimalIdAt(double mouseX, double mouseY, SimulationSnapshot snapshot) {
        if (snapshot == null) return null;

        double w = getWidth();
        double h = getHeight();
        double cellW = w / mapWidth;
        double cellH = h / mapHeight;
        double cellSize = Math.min(cellW, cellH);

        double offsetX = (w - (mapWidth * cellSize)) / 2.0;
        double offsetY = (h - (mapHeight * cellSize)) / 2.0;
        double relativeX = mouseX - offsetX; double relativeY = mouseY - offsetY;

        if (relativeX < 0 || relativeX >= mapWidth * cellSize || relativeY < 0 || relativeY >= mapHeight * cellSize) return null;

        int gridX = (int) (relativeX / cellSize);
        int gridY = (int) (relativeY / cellSize);
        Vector2d clickPos = new Vector2d(gridX, gridY);

        List<AnimalSnapshot> animalsOnTile = snapshot.animals().stream()
                .filter(a -> a.position().equals(clickPos))
                .sorted((a, b) -> Integer.compare(b.energy(), a.energy()))
                .toList();

        if (animalsOnTile.isEmpty()) return null;

        int count = animalsOnTile.size();
        int rows, cols;
        if (count == 1) { rows = 1; cols = 1; }
        else if (count <= 4) { rows = 2; cols = 2; }
        else { rows = 3; cols = 3; }

        double subSize = cellSize / Math.max(rows, cols);

        double localX = relativeX % cellSize;
        double localY = relativeY % cellSize;

        int subCol = (int) (localX / subSize);
        int subRow = (int) (localY / subSize);

        int index = subRow * cols + subCol;

        if (index >= 0 && index < animalsOnTile.size()) {
            return animalsOnTile.get(index).id();
        }

        return animalsOnTile.get(0).id();
    }

    public Canvas getCanvas() { return canvas; }
}