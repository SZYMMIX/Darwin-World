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

    private static final Color COLOR_STEPPE = Color.web("#fff9c4");
    private static final Color COLOR_JUNGLE = Color.web("#c8e6c9");
    private static final Color COLOR_PLANT_NORMAL = Color.FORESTGREEN;
    private static final Color COLOR_PLANT_POISON = Color.web("#8e24aa");
    private static final Color COLOR_BACKGROUND = Color.web("#222");

    private SimulationSnapshot lastSnapshot = null;

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
        gc.setFill(COLOR_BACKGROUND);
        gc.fillRect(0, 0, w, h);

        GridMetrics m = calculateMetrics(w, h);

        drawTerrains(m);
        drawPlants(m);
        drawAnimals(m);
    }

    private void drawTerrains(GridMetrics m) {
        gc.setFill(COLOR_STEPPE);
        gc.fillRect(m.offsetX, m.offsetY, mapWidth * m.cellSize, mapHeight * m.cellSize);

        gc.setFill(COLOR_JUNGLE);
        double jungleH = mapHeight * 0.2;
        double jungleY = (mapHeight - jungleH) / 2.0;

        gc.fillRect(m.offsetX, m.offsetY + (jungleY * m.cellSize),
                mapWidth * m.cellSize, jungleH * m.cellSize);
    }

    private void drawPlants(GridMetrics m) {
        for (var entry : lastSnapshot.plants().entrySet()) {
            Vector2d pos = entry.getKey();
            Plant plant = entry.getValue();

            gc.setFill(plant.isPoisonous() ? COLOR_PLANT_POISON : COLOR_PLANT_NORMAL);

            double pSize = m.cellSize * 0.6;
            double pOffset = (m.cellSize - pSize) / 2.0;

            gc.fillOval(m.offsetX + pos.x() * m.cellSize + pOffset,
                    m.offsetY + pos.y() * m.cellSize + pOffset,
                    pSize, pSize);
        }
    }

    private void drawAnimals(GridMetrics m) {
        Map<Vector2d, List<AnimalSnapshot>> grouped = lastSnapshot.animals().stream()
                .collect(Collectors.groupingBy(AnimalSnapshot::position));

        for (var entry : grouped.entrySet()) {
            drawAnimalGroup(entry.getKey(), entry.getValue(), m);
        }
    }

    private void drawAnimalGroup(Vector2d pos, List<AnimalSnapshot> group, GridMetrics m) {
        group.sort((a, b) -> Integer.compare(b.energy(), a.energy()));

        int count = group.size();

        int rows, cols;
        if (count == 1) { rows = 1; cols = 1; }
        else if (count <= 4) { rows = 2; cols = 2; }
        else { rows = 3; cols = 3; }

        double subSize = m.cellSize / Math.max(rows, cols);
        int limit = Math.min(count, rows * cols);

        for (int i = 0; i < limit; i++) {
            AnimalSnapshot animal = group.get(i);

            int r = i / cols;
            int c = i % cols;

            double px = m.offsetX + (pos.x() * m.cellSize) + (c * subSize);
            double py = m.offsetY + (pos.y() * m.cellSize) + (r * subSize);

            drawSingleAnimal(px, py, subSize, animal);
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

        Color bodyColor = EnergyColorProvider.calculateColor(animal.energy(), reproductionMinEnergy);
        gc.setFill(bodyColor);
        gc.fillOval(x + padding, y + padding, realSize, realSize);
    }

    public Integer getAnimalIdAt(double mouseX, double mouseY, SimulationSnapshot snapshot) {
        if (snapshot == null) return null;

        GridMetrics m = calculateMetrics(getWidth(), getHeight());

        double relativeX = mouseX - m.offsetX;
        double relativeY = mouseY - m.offsetY;

        if (relativeX < 0 || relativeX >= mapWidth * m.cellSize ||
                relativeY < 0 || relativeY >= mapHeight * m.cellSize) {
            return null;
        }

        int gridX = (int) (relativeX / m.cellSize);
        int gridY = (int) (relativeY / m.cellSize);
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

        double subSize = m.cellSize / Math.max(rows, cols);

        double localX = relativeX % m.cellSize;
        double localY = relativeY % m.cellSize;

        int subCol = (int) (localX / subSize);
        int subRow = (int) (localY / subSize);

        int index = subRow * cols + subCol;

        if (index >= 0 && index < animalsOnTile.size()) {
            return animalsOnTile.get(index).id();
        }

        return animalsOnTile.get(0).id();
    }

    public Canvas getCanvas() { return canvas; }

    private record GridMetrics(double cellSize, double offsetX, double offsetY) {}

    private GridMetrics calculateMetrics(double width, double height) {
        double cellW = width / mapWidth;
        double cellH = height / mapHeight;
        double cellSize = Math.min(cellW, cellH);

        double drawWidth = mapWidth * cellSize;
        double drawHeight = mapHeight * cellSize;

        double offsetX = (width - drawWidth) / 2.0;
        double offsetY = (height - drawHeight) / 2.0;

        return new GridMetrics(cellSize, offsetX, offsetY);
    }

}