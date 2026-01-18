package agh.ics.oop.app;

import agh.ics.oop.app.components.*;
import agh.ics.oop.simulation.SimulationParameters;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class SimulationWindow extends Stage {

    private final MapVisualizer mapVisualizer;
    private final SimulationSidebar sidebar;
    private final SimulationToolbar toolbar;

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

        sidebar.setMinWidth(250);

        splitPane.getItems().addAll(mapContainer, sidebar);
        splitPane.setDividerPositions(0.7);

        root.setCenter(splitPane);

        Scene scene = new Scene(root, 1100, 750);
        setScene(scene);
    }

    public MapVisualizer getMapVisualizer() {
        return mapVisualizer;
    }

    public SimulationSidebar getSidebar() {
        return sidebar;
    }

    public SimulationToolbar getToolbar() {
        return toolbar;
    }
}