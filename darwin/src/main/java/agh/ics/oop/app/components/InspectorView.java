package agh.ics.oop.app.components;

import agh.ics.oop.app.model.AnimalViewModel;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;

public class InspectorView extends VBox {

    private final Label statusLabel = new Label("Kliknij zwierzę na mapie...");
    private final Label idLabel = new Label("-");
    private final Label genotypeLabel = new Label("-");
    private final Label activeGeneLabel = new Label("-");
    private final Label energyLabel = new Label("-");
    private final Label eatenPlantsLabel = new Label("-");
    private final Label childrenLabel = new Label("-");
    private final Label descendantsLabel = new Label("-");
    private final Label ageLabel = new Label("-");

    private final CheckBox showDescendantsCheckbox = new CheckBox("Pokaż potomków");
    private final Button stopTrackingButton = new Button("Przestań śledzić");

    public InspectorView() {
        setSpacing(10);
        setPadding(new Insets(15));

        Label header = new Label("Inspektor Zwierzaka");
        header.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        statusLabel.setWrapText(true);
        statusLabel.setStyle("-fx-font-style: italic; -fx-text-fill: gray;");

        stopTrackingButton.setVisible(false);
        showDescendantsCheckbox.setDisable(true);

        getChildren().addAll(
                header,
                statusLabel,
                new Separator(),
                createDetailRow("ID:", idLabel),
                createDetailRow("Genotyp:", genotypeLabel),
                createDetailRow("Aktywny gen:", activeGeneLabel),
                createDetailRow("Energia:", energyLabel),
                createDetailRow("Zjedzone rośliny:", eatenPlantsLabel),
                createDetailRow("Dzieci:", childrenLabel),
                createDetailRow("Potomkowie:", descendantsLabel),
                createDetailRow("Wiek / Dzień śmierci:", ageLabel),
                new Separator(),
                showDescendantsCheckbox,
                stopTrackingButton
        );
    }

    private VBox createDetailRow(String label, Label value) {
        VBox box = new VBox(new Label(label), value);
        value.setStyle("-fx-font-weight: bold;");
        return box;
    }

    public void update(AnimalViewModel animal) {
        if (animal.id() == -1) {
            clear();
            return;
        }

        statusLabel.setText(animal.isDead() ? "Zwierzak nie żyje." : "Zwierzak żyje.");
        statusLabel.setStyle(animal.isDead() ? "-fx-text-fill: red;" : "-fx-text-fill: green;");

        idLabel.setText(String.valueOf(animal.id()));
        genotypeLabel.setText(animal.genotype() != null ? "Ukryty (kliknij)" : "-");
        activeGeneLabel.setText(String.valueOf(animal.activeGeneIndex()));
        energyLabel.setText(String.valueOf(animal.energy()));
        eatenPlantsLabel.setText(String.valueOf(animal.eatenPlants()));
        childrenLabel.setText(String.valueOf(animal.childrenCount()));
        descendantsLabel.setText(String.valueOf(animal.descendantsCount()));

        if (animal.isDead() && animal.deathDay().isPresent()) {
            ageLabel.setText(animal.age() + " (Zmarł w dniu " + animal.deathDay().get() + ")");
        } else {
            ageLabel.setText(String.valueOf(animal.age()));
        }

        stopTrackingButton.setVisible(true);
        showDescendantsCheckbox.setDisable(false);
    }

    public void clear() {
        statusLabel.setText("Kliknij zwierzę na mapie...");
        statusLabel.setStyle("-fx-font-style: italic; -fx-text-fill: gray;");
        idLabel.setText("-");
        genotypeLabel.setText("-");
        activeGeneLabel.setText("-");
        energyLabel.setText("-");
        eatenPlantsLabel.setText("-");
        childrenLabel.setText("-");
        descendantsLabel.setText("-");
        ageLabel.setText("-");

        stopTrackingButton.setVisible(false);
        showDescendantsCheckbox.setDisable(true);
        showDescendantsCheckbox.setSelected(false);
    }

    public Button getStopTrackingButton() {
        return stopTrackingButton;
    }

    public CheckBox getShowDescendantsCheckbox() {
        return showDescendantsCheckbox;
    }
}