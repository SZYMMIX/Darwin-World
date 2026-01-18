package agh.ics.oop.app.components;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class SimulationSidebar extends TabPane {

    private final StatsView statsView;
    private final InspectorView inspectorView;
    private final ChartView chartView;

    public SimulationSidebar() {
        setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
        setMinWidth(250);
        setPrefWidth(300);

        statsView = new StatsView();
        inspectorView = new InspectorView();
        chartView = new ChartView();

        Tab statsTab = new Tab("Statystyki", statsView);
        Tab inspectorTab = new Tab("Inspektor", inspectorView);
        Tab chartTab = new Tab("Wykres", chartView);

        getTabs().addAll(statsTab, inspectorTab, chartTab);
    }

    public StatsView getStatsView() {
        return statsView;
    }

    public InspectorView getInspectorView() {
        return inspectorView;
    }

    public ChartView getChartView() {
        return chartView;
    }

    public void selectInspectorTab() {
        getSelectionModel().select(1);
    }
}