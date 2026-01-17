package agh.ics.oop.app;

import agh.ics.oop.simulation.SimulationParameters;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class SimulationConfigurator {

    private final Spinner<Integer> widthSpinner = createSpinner(10, 500, 20);
    private final Spinner<Integer> heightSpinner = createSpinner(10, 500, 20);

    private final Spinner<Integer> initialPlantCountSpinner = createSpinner(0, 5000, 20);
    private final Spinner<Integer> plantEnergySpinner = createSpinner(1, 1000, 10);
    private final Spinner<Integer> dailyPlantGrowthSpinner = createSpinner(0, 500, 10);

    private final Spinner<Integer> initialAnimalCountSpinner = createSpinner(0, 1000, 15);
    private final Spinner<Integer> initialAnimalEnergySpinner = createSpinner(1, 1000, 30);

    private final Spinner<Integer> dailyEnergyCostSpinner = createSpinner(0, 100, 1);
    private final Spinner<Integer> reproductionEnergyMinSpinner = createSpinner(1, 1000, 20);
    private final Spinner<Integer> reproductionEnergyCostSpinner = createSpinner(1, 1000, 15);

    private final Spinner<Integer> minMutationsSpinner = createSpinner(0, 20, 0);
    private final Spinner<Integer> maxMutationsSpinner = createSpinner(0, 20, 5);
    private final Spinner<Integer> genotypeLengthSpinner = createSpinner(1, 20, 5);

    private final CheckBox poisonMapCheckBox = new CheckBox("Aktywuj mapę z trucizną");
    private final Slider poisonProbabilitySlider = new Slider(0.0, 1.0, 0.2);
    private final Spinner<Integer> poisonEnergyCostSpinner = createSpinner(0, 1000, 10);

    public void start(Stage primaryStage) {
        primaryStage.setTitle("Darwin World - Konfiguracja");

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);

        VBox mainLayout = new VBox(15);
        mainLayout.setPadding(new Insets(20));
        mainLayout.setAlignment(Pos.TOP_CENTER);

        mainLayout.getChildren().addAll(
                createSectionHeader("1. Mapa"),
                createGrid(
                        "Szerokość:", widthSpinner,
                        "Wysokość:", heightSpinner
                ),

                createSectionHeader("2. Rośliny"),
                createGrid(
                        "Startowa liczba:", initialPlantCountSpinner,
                        "Energia z rośliny:", plantEnergySpinner,
                        "Dzienny wzrost:", dailyPlantGrowthSpinner
                ),

                createSectionHeader("3. Zwierzęta"),
                createGrid(
                        "Startowa liczba:", initialAnimalCountSpinner,
                        "Startowa energia:", initialAnimalEnergySpinner
                ),

                createSectionHeader("4. Fizjologia"),
                createGrid(
                        "Koszt energii dnia:", dailyEnergyCostSpinner,
                        "Min. energia do rozmnażania:", reproductionEnergyMinSpinner,
                        "Koszt energii rozmnażania:", reproductionEnergyCostSpinner
                ),

                createSectionHeader("5. Genetyka"),
                createGrid(
                        "Długość genotypu:", genotypeLengthSpinner,
                        "Min. mutacji:", minMutationsSpinner,
                        "Max. mutacji:", maxMutationsSpinner
                ),

                createSectionHeader("6. Wariant: Trucizna"),
                createPoisonSection(),

                new Separator(),
                createButtons()
        );

        scrollPane.setContent(mainLayout);
        Scene scene = new Scene(scrollPane, 500, 750);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createPoisonSection() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(5, 0, 0, 0));

        poisonProbabilitySlider.setShowTickLabels(true);
        poisonProbabilitySlider.setShowTickMarks(true);
        poisonProbabilitySlider.setMajorTickUnit(0.25f);
        poisonProbabilitySlider.setBlockIncrement(0.05f);

        GridPane grid = createGrid(
                "Prawdopodobieństwo:", poisonProbabilitySlider,
                "Koszt zatrucia:", poisonEnergyCostSpinner
        );

        grid.disableProperty().bind(poisonMapCheckBox.selectedProperty().not());

        box.getChildren().addAll(poisonMapCheckBox, grid);
        return box;
    }

    private HBox createButtons() {
        Button startBtn = new Button("Rozpocznij Symulację");
        startBtn.setStyle("-fx-font-size: 16px; -fx-base: #b6e7c9;");
        startBtn.setPrefWidth(200);
        startBtn.setOnAction(event -> handleStart());

        // Placeholder na Save/Load
        Button saveBtn = new Button("Zapisz");
        Button loadBtn = new Button("Wczytaj");

        HBox buttons = new HBox(10, loadBtn, saveBtn, new Region(), startBtn);
        HBox.setHgrow(buttons.getChildren().get(2), Priority.ALWAYS);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        return buttons;
    }

    private void handleStart() {
        SimulationParameters params = buildParameters();
        System.out.println("Start symulacji z parametrami: " + params);
    }

    private SimulationParameters buildParameters() {
        boolean isPoison = poisonMapCheckBox.isSelected();

        return new SimulationParameters(
                widthSpinner.getValue(),
                heightSpinner.getValue(),
                initialPlantCountSpinner.getValue(),
                plantEnergySpinner.getValue(),
                dailyPlantGrowthSpinner.getValue(),
                initialAnimalCountSpinner.getValue(),
                initialAnimalEnergySpinner.getValue(),
                dailyEnergyCostSpinner.getValue(),
                reproductionEnergyMinSpinner.getValue(),
                reproductionEnergyCostSpinner.getValue(),
                minMutationsSpinner.getValue(),
                maxMutationsSpinner.getValue(),
                genotypeLengthSpinner.getValue(),
                isPoison,
                isPoison ? poisonProbabilitySlider.getValue() : 0.0,
                isPoison ? poisonEnergyCostSpinner.getValue() : 0
        );
    }

    private Spinner<Integer> createSpinner(int min, int max, int initial) {
        Spinner<Integer> spinner = new Spinner<>(min, max, initial);
        spinner.setEditable(true);
        spinner.setMaxWidth(Double.MAX_VALUE);
        return spinner;
    }

    private Label createSectionHeader(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("System", FontWeight.BOLD, 14));
        label.setPadding(new Insets(10, 0, 5, 0));
        return label;
    }

    private GridPane createGrid(Object... content) {
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(10);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setMinWidth(150);

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS);

        grid.getColumnConstraints().addAll(col1, col2);

        int row = 0;
        for (int i = 0; i < content.length; i += 2) {
            String labelText = (String) content[i];
            Control control = (Control) content[i + 1];

            grid.add(new Label(labelText), 0, row);
            grid.add(control, 1, row);
            row++;
        }
        return grid;
    }
}