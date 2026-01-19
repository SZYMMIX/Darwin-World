package agh.ics.oop.app.components;

import agh.ics.oop.simulation.SimulationStats;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class ChartView extends VBox {
    private final LineChart<Number, Number> chart;
    private final ComboBox<ChartMode> modeSelector;

    private final List<SimulationStats> allStatsHistory = new ArrayList<>();

    private final XYChart.Series<Number, Number> series1 = new XYChart.Series<>();
    private final XYChart.Series<Number, Number> series2 = new XYChart.Series<>();

    private ChartMode currentMode = ChartMode.POPULATION;

    public enum ChartMode {
        POPULATION("Populacja (Zwierzęta/Rośliny)"),
        ENERGY("Średnia Energia"),
        LIFESPAN("Średnia Długość Życia"),
        CHILDREN("Średnia Liczba Dzieci");

        private final String label;
        ChartMode(String label) { this.label = label; }
        @Override public String toString() { return label; }
    }

    public ChartView() {
        setPadding(new Insets(10));
        setSpacing(10);
        setAlignment(Pos.CENTER);

        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Dzień");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Wartość");

        chart = new LineChart<>(xAxis, yAxis);
        chart.setCreateSymbols(false);
        chart.setAnimated(false);

        modeSelector = new ComboBox<>();
        modeSelector.getItems().addAll(ChartMode.values());
        modeSelector.setValue(ChartMode.POPULATION);
        modeSelector.setMaxWidth(Double.MAX_VALUE); // Rozciągnij na pełną szerokość
        modeSelector.setOnAction(e -> refreshChart(modeSelector.getValue()));

        VBox.setVgrow(chart, Priority.ALWAYS);
        getChildren().addAll(modeSelector, chart);

        refreshChart(ChartMode.POPULATION);
    }

    public void addDataPoint(int day, SimulationStats stats) {
        allStatsHistory.add(stats);
        appendToSeries(day, stats);
    }

    private void refreshChart(ChartMode newMode) {
        this.currentMode = newMode;

        chart.getData().clear();
        series1.getData().clear();
        series2.getData().clear();

        switch (newMode) {
            case POPULATION -> {
                series1.setName("Zwierzęta");
                series2.setName("Rośliny");
                chart.getData().addAll(series1, series2);
                chart.setStyle("CHART_COLOR_1: #8B4513; CHART_COLOR_2: #228B22;");
            }
            case ENERGY -> {
                series1.setName("Śr. Energia");
                chart.getData().add(series1);
                chart.setStyle("CHART_COLOR_1: orange;");
            }
            case LIFESPAN -> {
                series1.setName("Śr. Dł. Życia");
                chart.getData().add(series1);
                chart.setStyle("CHART_COLOR_1: blue;");
            }
            case CHILDREN -> {
                series1.setName("Śr. Liczba Dzieci");
                chart.getData().add(series1);
                chart.setStyle("CHART_COLOR_1: purple;");
            }
        }

        for (int i = 0; i < allStatsHistory.size(); i++) {
            appendToSeries(i, allStatsHistory.get(i));
        }
    }

    private void appendToSeries(int day, SimulationStats stats) {
        switch (currentMode) {
            case POPULATION -> {
                series1.getData().add(new XYChart.Data<>(day, stats.currentAnimalsCount()));
                series2.getData().add(new XYChart.Data<>(day, stats.currentPlantsCount()));
            }
            case ENERGY ->
                    series1.getData().add(new XYChart.Data<>(day, stats.averageEnergy()));
            case LIFESPAN ->
                    series1.getData().add(new XYChart.Data<>(day, stats.averageLifespan()));
            case CHILDREN ->
                    series1.getData().add(new XYChart.Data<>(day, stats.averageChildren()));
        }
    }

    public List<SimulationStats> getAllStatsHistory() {
        return new ArrayList<>(allStatsHistory);
    }
}