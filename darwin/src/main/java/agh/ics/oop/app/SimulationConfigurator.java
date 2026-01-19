package agh.ics.oop.app;

import agh.ics.oop.simulation.SimulationParameters;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.Optional;

public class SimulationConfigurator {

    private final ConfigurationManager configManager = new ConfigurationManager();
    private final ComboBox<String> presetComboBox = new ComboBox<>();

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
                buildMapSection(),

                createSectionHeader("2. Rośliny"),
                buildPlantsSection(),

                createSectionHeader("3. Zwierzęta"),
                buildAnimalsSection(),

                createSectionHeader("4. Fizjologia"),
                buildPhysiologySection(),

                createSectionHeader("5. Genetyka"),
                buildGeneticsSection(),

                createSectionHeader("6. Wariant: Trucizna"),
                buildPoisonSection(),

                new Separator(),
                createButtons()
        );

        scrollPane.setContent(mainLayout);
        Scene scene = new Scene(scrollPane, 550, 800);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private HBox createButtons() {
        refreshPresets();
        presetComboBox.setPromptText("Wybierz preset...");
        presetComboBox.setPrefWidth(140);

        Button loadBtn = new Button("Wczytaj");
        loadBtn.setOnAction(e -> handleLoad());

        Button saveBtn = new Button("Zapisz");
        saveBtn.setOnAction(e -> handleSave());

        Button startBtn = new Button("Rozpocznij Symulację");
        startBtn.setStyle("-fx-font-size: 14px; -fx-base: #b6e7c9; -fx-font-weight: bold;");
        startBtn.setPrefWidth(180);
        startBtn.setOnAction(event -> handleStart());

        HBox leftSide = new HBox(10, presetComboBox, loadBtn, saveBtn);
        leftSide.setAlignment(Pos.CENTER_LEFT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox buttons = new HBox(10, leftSide, spacer, startBtn);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        buttons.setPadding(new Insets(10, 0, 0, 0));

        return buttons;
    }

    private void refreshPresets() {
        presetComboBox.getItems().clear();
        presetComboBox.getItems().addAll(configManager.getAvailablePresets());
    }

    private void handleSave() {
        TextInputDialog dialog = new TextInputDialog("moj_konfig");
        dialog.setTitle("Zapisz konfigurację");
        dialog.setHeaderText("Zapisywanie nowego presetu");
        dialog.setContentText("Podaj nazwę pliku:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            try {
                SimulationParameters params = buildParameters();
                configManager.saveConfiguration(name, params);
                refreshPresets();
                presetComboBox.getSelectionModel().select(name);
            } catch (Exception e) {
                showAlert("Błąd zapisu", "Nie udało się zapisać pliku: " + e.getMessage());
            }
        });
    }

    private void handleLoad() {
        String selected = presetComboBox.getValue();
        if (selected == null || selected.isBlank()) {
            showAlert("Błąd", "Wybierz preset z listy!");
            return;
        }

        try {
            SimulationParameters params = configManager.loadConfiguration(selected);
            updateControls(params);
        } catch (Exception e) {
            showAlert("Błąd odczytu", "Nie udało się wczytać pliku: " + e.getMessage());
        }
    }

    private void updateControls(SimulationParameters params) {
        widthSpinner.getValueFactory().setValue(params.width());
        heightSpinner.getValueFactory().setValue(params.height());

        initialPlantCountSpinner.getValueFactory().setValue(params.initialPlantCount());
        plantEnergySpinner.getValueFactory().setValue(params.plantEnergy());
        dailyPlantGrowthSpinner.getValueFactory().setValue(params.dailyPlantGrowth());

        initialAnimalCountSpinner.getValueFactory().setValue(params.initialAnimalCount());
        initialAnimalEnergySpinner.getValueFactory().setValue(params.initialAnimalEnergy());

        dailyEnergyCostSpinner.getValueFactory().setValue(params.dailyEnergyCost());
        reproductionEnergyMinSpinner.getValueFactory().setValue(params.reproductionEnergyMin());
        reproductionEnergyCostSpinner.getValueFactory().setValue(params.reproductionEnergyCost());

        minMutationsSpinner.getValueFactory().setValue(params.minMutations());
        maxMutationsSpinner.getValueFactory().setValue(params.maxMutations());
        genotypeLengthSpinner.getValueFactory().setValue(params.genotypeLength());

        poisonMapCheckBox.setSelected(params.isPoisonMap());
        poisonProbabilitySlider.setValue(params.poisonProbability());
        poisonEnergyCostSpinner.getValueFactory().setValue(params.poisonEnergyCost());
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void handleStart() {
        SimulationParameters params = buildParameters();
        new SimulationWindow(params).show();
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

    private void addParameter(GridPane grid, int row, String labelText, Control control) {
        grid.add(new Label(labelText), 0, row);
        grid.add(control, 1, row);
    }

    private GridPane createSectionGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(10);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setMinWidth(150);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(col1, col2);

        return grid;
    }

    private GridPane buildMapSection() {
        GridPane grid = createSectionGrid();
        addParameter(grid, 0, "Szerokość:", widthSpinner);
        addParameter(grid, 1, "Wysokość:", heightSpinner);
        return grid;
    }

    private GridPane buildPlantsSection() {
        GridPane grid = createSectionGrid();
        addParameter(grid, 0, "Startowa liczba:", initialPlantCountSpinner);
        addParameter(grid, 1, "Energia z rośliny:", plantEnergySpinner);
        addParameter(grid, 2, "Dzienny wzrost:", dailyPlantGrowthSpinner);
        return grid;
    }

    private GridPane buildAnimalsSection() {
        GridPane grid = createSectionGrid();
        addParameter(grid, 0, "Startowa liczba:", initialAnimalCountSpinner);
        addParameter(grid, 1, "Startowa energia:", initialAnimalEnergySpinner);
        return grid;
    }

    private GridPane buildPhysiologySection() {
        GridPane grid = createSectionGrid();
        addParameter(grid, 0, "Koszt energii dnia:", dailyEnergyCostSpinner);
        addParameter(grid, 1, "Min. energia do rozmnażania:", reproductionEnergyMinSpinner);
        addParameter(grid, 2, "Koszt energii rozmnażania:", reproductionEnergyCostSpinner);
        return grid;
    }

    private GridPane buildGeneticsSection() {
        GridPane grid = createSectionGrid();
        addParameter(grid, 0, "Długość genotypu:", genotypeLengthSpinner);
        addParameter(grid, 1, "Min. mutacji:", minMutationsSpinner);
        addParameter(grid, 2, "Max. mutacji:", maxMutationsSpinner);
        return grid;
    }

    private VBox buildPoisonSection() {
        VBox box = new VBox(10);
        box.setPadding(new Insets(5, 0, 0, 0));

        poisonProbabilitySlider.setShowTickLabels(true);
        poisonProbabilitySlider.setShowTickMarks(true);
        poisonProbabilitySlider.setMajorTickUnit(0.25f);
        poisonProbabilitySlider.setBlockIncrement(0.05f);

        GridPane grid = createSectionGrid();
        addParameter(grid, 0, "Prawdopodobieństwo:", poisonProbabilitySlider);
        addParameter(grid, 1, "Koszt zatrucia:", poisonEnergyCostSpinner);

        grid.disableProperty().bind(poisonMapCheckBox.selectedProperty().not());

        box.getChildren().addAll(poisonMapCheckBox, grid);
        return box;
    }
}