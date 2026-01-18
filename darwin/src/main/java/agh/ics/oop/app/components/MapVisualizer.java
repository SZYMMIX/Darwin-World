package agh.ics.oop.app.components;

import agh.ics.oop.model.AnimalSnapshot;
import agh.ics.oop.model.Genotype;
import agh.ics.oop.model.Plant;
import agh.ics.oop.model.Vector2d;
import agh.ics.oop.simulation.SimulationSnapshot;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class MapVisualizer extends Pane {

    private final Canvas canvas;
    private final GraphicsContext gc;
    private final int mapWidth;
    private final int mapHeight;

    private AnimalSnapshot trackedAnimal = null;
    private Genotype dominantGenotype = null;
    private boolean showDominantGenotype = false;

    private SimulationSnapshot lastSnapshot = null;

    public MapVisualizer(int mapWidth, int mapHeight) {
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;

        this.canvas = new Canvas(100, 100);
        this.gc = canvas.getGraphicsContext2D();

        getChildren().add(canvas);

        widthProperty().addListener(obs -> drawLastSnapshot());
        heightProperty().addListener(obs -> drawLastSnapshot());
    }

    public void draw(SimulationSnapshot snapshot) {
        this.lastSnapshot = snapshot;
        drawLastSnapshot();
    }

    private void drawLastSnapshot() {
        if (lastSnapshot == null) return;

        double w = getWidth();
        double h = getHeight();
        if (w == 0 || h == 0) return;

        canvas.setWidth(w);
        canvas.setHeight(h);

        double cellW = w / mapWidth;
        double cellH = h / mapHeight;
        double cellSize = Math.min(cellW, cellH);

        double offsetX = (w - (mapWidth * cellSize)) / 2.0;
        double offsetY = (h - (mapHeight * cellSize)) / 2.0;

        drawBackground(w, h);

        gc.setFill(Color.web("#c8e6c9"));
        double jungleH = mapHeight * 0.2;
        double jungleY = (mapHeight - jungleH) / 2.0;
        gc.fillRect(
                offsetX,
                offsetY + (jungleY * cellSize),
                mapWidth * cellSize,
                jungleH * cellSize
        );

        for (var entry : lastSnapshot.plants().entrySet()) {
            Vector2d pos = entry.getKey();
            Plant plant = entry.getValue();

            gc.setFill(plant.isPoisonous() ? Color.web("#8d6e63") : Color.FORESTGREEN);
            gc.fillOval(
                    offsetX + pos.x() * cellSize,
                    offsetY + pos.y() * cellSize,
                    cellSize, cellSize
            );
        }

        for (AnimalSnapshot animal : lastSnapshot.animals()) {
            Vector2d pos = animal.position();
            double px = offsetX + pos.x() * cellSize;
            double py = offsetY + pos.y() * cellSize;

            if (showDominantGenotype && dominantGenotype != null && false) {
                gc.setStroke(Color.ROYALBLUE);
                gc.setLineWidth(Math.max(1.5, cellSize * 0.1));
                gc.strokeOval(px, py, cellSize, cellSize);
            }

            if (trackedAnimal != null && animal.id() == trackedAnimal.id()) {
                gc.setStroke(Color.MAGENTA);
                gc.setLineWidth(Math.max(2.0, cellSize * 0.15));
                gc.strokeOval(px, py, cellSize, cellSize);
            }

            double energyRatio = Math.min(1.0, (double) animal.energy() / 200.0);
            Color animalColor = Color.hsb(0 + (energyRatio * 120), 0.8, 0.9);

            gc.setFill(animalColor);
            gc.fillOval(px + (cellSize * 0.1), py + (cellSize * 0.1), cellSize * 0.8, cellSize * 0.8);
        }
    }

    private void drawBackground(double w, double h) {
        gc.setFill(Color.web("#333"));
        gc.fillRect(0, 0, w, h);

        double cellW = w / mapWidth;
        double cellH = h / mapHeight;
        double cellSize = Math.min(cellW, cellH);
        double offsetX = (w - (mapWidth * cellSize)) / 2.0;
        double offsetY = (h - (mapHeight * cellSize)) / 2.0;

        gc.setFill(Color.web("#fff9c4"));
        gc.fillRect(offsetX, offsetY, mapWidth * cellSize, mapHeight * cellSize);
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

        double relativeX = mouseX - offsetX;
        double relativeY = mouseY - offsetY;

        if (relativeX < 0 || relativeX >= mapWidth * cellSize ||
                relativeY < 0 || relativeY >= mapHeight * cellSize) {
            return null;
        }

        int x = (int) (relativeX / cellSize);
        int y = (int) (relativeY / cellSize);
        Vector2d clickPos = new Vector2d(x, y);

        return snapshot.animals().stream()
                .filter(a -> a.position().equals(clickPos))
                .findFirst()
                .map(AnimalSnapshot::id)
                .orElse(null);
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public void setTrackedAnimal(AnimalSnapshot animal) {
        this.trackedAnimal = animal;
        drawLastSnapshot();
    }

    public void setDominantGenotype(Genotype genotype) {
        this.dominantGenotype = genotype;
        drawLastSnapshot();
    }

    public void toggleDominantGenotype() {
        this.showDominantGenotype = !this.showDominantGenotype;
        drawLastSnapshot();
    }
}