package agh.ics.oop.app.components;

import agh.ics.oop.simulation.GenotypeStat;
import agh.ics.oop.simulation.SimulationStats;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
import javafx.scene.layout.VBox;

public class StatsView extends VBox {

    private final Label animalsCount = new Label("-");
    private final Label plantsCount = new Label("-");
    private final Label freeFields = new Label("-");
    private final Label avgEnergy = new Label("-");
    private final Label avgLifespan = new Label("-");
    private final Label avgChildren = new Label("-");

    private final ListView<GenotypeStat> topGenotypes = new ListView<>();

    public StatsView() {
        setSpacing(10);
        setPadding(new Insets(15));

        Label header = new Label("Statystyki Ogólne");
        header.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        topGenotypes.setPrefHeight(150);
        topGenotypes.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(GenotypeStat item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%s (n=%d)", item.genotype().toString(), item.animalIds().size()));
                }
            }
        });

        getChildren().addAll(
                header,
                new Separator(),
                createStatRow("Zwierzaki:", animalsCount),
                createStatRow("Rośliny:", plantsCount),
                createStatRow("Wolne pola:", freeFields),
                createStatRow("Śr. energia:", avgEnergy),
                createStatRow("Śr. długość życia:", avgLifespan),
                createStatRow("Śr. liczba dzieci:", avgChildren),
                new Separator(),
                new Label("Top 5 Genotypów:"),
                topGenotypes
        );
    }

    private VBox createStatRow(String title, Label valueLabel) {
        VBox row = new VBox(2);

        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 10px;");
        valueLabel.setStyle("-fx-font-weight: bold;");

        row.getChildren().addAll(titleLabel, valueLabel);
        return row;
    }

    public void update(SimulationStats stats) {
        animalsCount.setText(String.valueOf(stats.currentAnimalsCount()));
        plantsCount.setText(String.valueOf(stats.currentPlantsCount()));
        freeFields.setText(String.valueOf(stats.freeFieldsCount()));
        avgEnergy.setText(String.format("%.2f", stats.averageEnergy()));
        avgLifespan.setText(String.format("%.2f", stats.averageLifespan()));
        avgChildren.setText(String.format("%.2f", stats.averageChildren()));

        topGenotypes.getItems().setAll(stats.topGenotypes());
    }

    public ListView<GenotypeStat> getTopGenotypesList() {
        return topGenotypes;
    }
}