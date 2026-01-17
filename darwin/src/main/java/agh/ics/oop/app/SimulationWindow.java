package agh.ics.oop.app;

import agh.ics.oop.simulation.Simulation;
import agh.ics.oop.simulation.SimulationParameters;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class SimulationWindow extends Stage {

    private final SimulationParameters params;
    private final Simulation simulation;

    public SimulationWindow(SimulationParameters params) {
        this.params = params;
        this.simulation = new Simulation(params);

        setTitle("Darwin World - Symulacja #" + hashCode());

        BorderPane root = new BorderPane();

        Pane mapPlaceholder = new StackPane(new Label("Tu będzie wizualizacja mapy\n(" + params.width() + "x" + params.height() + ")"));
        mapPlaceholder.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        mapPlaceholder.setPrefSize(600, 600);
        root.setCenter(mapPlaceholder);

        VBox statsPanel = createStatsPanel();
        root.setRight(statsPanel);

        Scene scene = new Scene(root, 800, 600);
        setScene(scene);

        setOnCloseRequest(e -> System.out.println("Zamykanie symulacji..."));
    }

    private VBox createStatsPanel() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(10));
        box.setPrefWidth(200);
        box.setBackground(new Background(new BackgroundFill(Color.WHITESMOKE, CornerRadii.EMPTY, Insets.EMPTY)));

        Label title = new Label("Statystyki");
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        box.getChildren().addAll(
                title,
                new Label("Dzień: 0"),
                new Label("Zwierzaki: " + params.initialAnimalCount()),
                new Label("Rośliny: " + params.initialPlantCount())
        );
        return box;
    }
}