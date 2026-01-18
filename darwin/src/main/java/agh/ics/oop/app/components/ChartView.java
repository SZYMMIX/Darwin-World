package agh.ics.oop.app.components;

import javafx.geometry.Insets;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class ChartView extends VBox {

    private final XYChart.Series<Number, Number> animalsSeries = new XYChart.Series<>();
    private final XYChart.Series<Number, Number> plantsSeries = new XYChart.Series<>();

    public ChartView() {
        setPadding(new Insets(10));

        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Dzień");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Liczebność");

        LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setCreateSymbols(false);
        chart.setAnimated(false);

        animalsSeries.setName("Zwierzęta");
        plantsSeries.setName("Rośliny");

        chart.getData().addAll(animalsSeries, plantsSeries);

        getChildren().add(chart);
        VBox.setVgrow(chart, Priority.ALWAYS);
    }

    public void addDataPoint(int day, int animals, int plants) {
        animalsSeries.getData().add(new XYChart.Data<>(day, animals));
        plantsSeries.getData().add(new XYChart.Data<>(day, plants));
    }

    public void clear() {
        animalsSeries.getData().clear();
        plantsSeries.getData().clear();
    }
}