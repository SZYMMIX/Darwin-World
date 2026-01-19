package agh.ics.oop.app;

import agh.ics.oop.app.components.ChartView;
import agh.ics.oop.app.components.StatisticsSaver;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Optional;

public class SimulationExporter {

    public void askOnClose(Stage owner, ChartView chartView) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Koniec symulacji");
        alert.setHeaderText("Czy chcesz zapisać statystyki do pliku CSV?");
        alert.setContentText("Wybierz opcję:");

        ButtonType buttonTypeYes = new ButtonType("Tak, zapisz");
        ButtonType buttonTypeNo = new ButtonType("Nie");
        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == buttonTypeYes) {
            saveStats(owner, chartView);
        }
    }

    private void saveStats(Stage owner, ChartView chartView) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Zapisz statystyki");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Pliki CSV", "*.csv"));
        fileChooser.setInitialFileName("statystyki_" + System.currentTimeMillis() + ".csv");

        File file = fileChooser.showSaveDialog(owner);
        if (file != null) {
            StatisticsSaver.saveToFile(file, chartView.getAllStatsHistory());
        }
    }
}