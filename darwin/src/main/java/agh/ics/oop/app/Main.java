package agh.ics.oop.app;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        SimulationConfigurator configurator = new SimulationConfigurator();
        configurator.start(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}