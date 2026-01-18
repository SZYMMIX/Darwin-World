package agh.ics.oop.app;

import agh.ics.oop.app.components.*;
import agh.ics.oop.simulation.SimulationParameters;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SimulationWindow extends Stage {

    private final MapVisualizer mapVisualizer;
    private final SimulationSidebar sidebar;
    private final SimulationToolbar toolbar;
    private final SimulationPresenter presenter;

    public SimulationWindow(SimulationParameters params) {
        setTitle("Darwin World - Symulacja");

        this.mapVisualizer = new MapVisualizer(params.width(), params.height());
        this.sidebar = new SimulationSidebar();
        this.toolbar = new SimulationToolbar();

        BorderPane root = new BorderPane();
        root.setTop(toolbar);

        SplitPane splitPane = new SplitPane();

        VBox mapContainer = new VBox(mapVisualizer);
        mapContainer.setAlignment(Pos.CENTER);
        mapContainer.setStyle("-fx-background-color: #333;");
        mapContainer.setMinWidth(400);

        splitPane.getItems().addAll(mapContainer, sidebar);
        splitPane.setDividerPositions(0.7);

        root.setCenter(splitPane);

        Scene scene = new Scene(root, 1100, 750);
        setScene(scene);

        this.presenter = new SimulationPresenter(this, params);

        setOnCloseRequest(e -> presenter.onWindowClose());
    }

    public MapVisualizer getMapVisualizer() { return mapVisualizer; }
    public SimulationSidebar getSidebar() { return sidebar; }
    public SimulationToolbar getToolbar() { return toolbar; }
}