package agh.ics.oop.app;

import agh.ics.oop.app.components.InspectorView;
import agh.ics.oop.app.components.SimulationToolbar;
import agh.ics.oop.app.components.StatisticsSaver;
import agh.ics.oop.model.Genotype;
import agh.ics.oop.model.TrackedAnimalStats;
import agh.ics.oop.simulation.GenotypeStat;
import agh.ics.oop.simulation.Simulation;
import agh.ics.oop.simulation.SimulationParameters;
import agh.ics.oop.simulation.SimulationSnapshot;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.*;

public class SimulationPresenter {
    private final SimulationWindow view;
    private final SimulationEngine engine;
    private final Simulation simulation;

    private final LinkedList<SimulationSnapshot> history = new LinkedList<>();
    private static final int MAX_HISTORY_SIZE = 100;
    private int historyCursor = -1;

    private Genotype selectedGenotypeToHighlight = null;
    private Integer trackedAnimalId = null;

    public SimulationPresenter(SimulationWindow view, SimulationParameters params) {
        this.view = view;
        this.simulation = new Simulation(params);
        this.engine = new SimulationEngine(simulation);

        this.engine.addObserver(this::onSimulationStep);

        setupToolbarListeners();
        setupMapListeners();
        setupSidePanelListeners();

        SimulationSnapshot initialSnapshot = simulation.getSnapshot();
        synchronized (history) {
            history.add(initialSnapshot);
        }
        updateView(initialSnapshot);
        view.getToolbar().setPlayPauseStatus(true);

        Thread thread = new Thread(engine);
        thread.setDaemon(true);
        thread.start();
    }

    private void setupToolbarListeners() {
        SimulationToolbar toolbar = view.getToolbar();
        toolbar.getPlayPauseBtn().setOnAction(e -> togglePlayPause());
        toolbar.getSpeedSlider().valueProperty().addListener((obs, oldVal, newVal) -> {
            int delay = 600 - (newVal.intValue() * 28);
            engine.setMoveDelay(delay);
        });
        toolbar.getPrevBtn().setOnAction(e -> handleManualStep(-1));
        toolbar.getNextBtn().setOnAction(e -> handleManualStep(1));
    }

    private void setupMapListeners() {
        view.getMapVisualizer().getCanvas().setOnMouseClicked(event -> {
            SimulationSnapshot current = getCurrentSnapshot();
            if (current != null) {
                Integer clickedId = view.getMapVisualizer().getAnimalIdAt(event.getX(), event.getY(), current);
                handleAnimalSelection(clickedId);
            }
        });
    }

    private void setupSidePanelListeners() {
        InspectorView inspector = view.getSidebar().getInspectorView();

        inspector.getHighlightChildrenCheck().selectedProperty().addListener((obs, oldV, newV) -> updateFamilyHighlight());
        inspector.getHighlightDescendantsCheck().selectedProperty().addListener((obs, oldV, newV) -> updateFamilyHighlight());

        inspector.getStopTrackingButton().setOnAction(e -> handleAnimalSelection(null));

        view.getSidebar().getStatsView().getTopGenotypesList()
                .getSelectionModel().selectedItemProperty()
                .addListener((obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        this.selectedGenotypeToHighlight = newVal.genotype();
                    }
                    updateHighlightFilters(getCurrentSnapshot());
                });
    }

    private void handleAnimalSelection(Integer animalId) {
        this.trackedAnimalId = animalId;
        view.getMapVisualizer().setTrackedAnimalId(animalId);
        updateInspector(getCurrentSnapshot());
        if (animalId != null) {
            view.getSidebar().selectInspectorTab();
        }
    }

    private void updateHighlightFilters(SimulationSnapshot snapshot) {
        if (snapshot == null) return;

        if (selectedGenotypeToHighlight != null) {
            Optional<GenotypeStat> stat = snapshot.stats().topGenotypes().stream()
                    .filter(s -> s.genotype().equals(selectedGenotypeToHighlight))
                    .findFirst();

            if (stat.isPresent()) {
                view.getMapVisualizer().setAnimalsWithDominantGenotype(stat.get().animalIds());
            } else {
                view.getMapVisualizer().setAnimalsWithDominantGenotype(Collections.emptyList());
            }
        } else {
            view.getMapVisualizer().setAnimalsWithDominantGenotype(Collections.emptyList());
        }
    }

    private void updateFamilyHighlight() {
        if (trackedAnimalId == null) {
            view.getMapVisualizer().setHighlightedFamily(null, null);
            return;
        }

        SimulationSnapshot snap = getCurrentSnapshot();
        if (snap == null) return;

        Optional<TrackedAnimalStats> statsOpt = simulation.getAnimalDetails(trackedAnimalId, snap.day());

        if (statsOpt.isPresent()) {
            TrackedAnimalStats stats = statsOpt.get();
            InspectorView inspector = view.getSidebar().getInspectorView();

            Set<Integer> children = inspector.getHighlightChildrenCheck().isSelected()
                    ? stats.childrenIds() : Collections.emptySet();

            Set<Integer> descendants = inspector.getHighlightDescendantsCheck().isSelected()
                    ? stats.descendantsIds() : Collections.emptySet();

            view.getMapVisualizer().setHighlightedFamily(children, descendants);
        }
    }

    private void onSimulationStep(SimulationSnapshot snapshot) {
        synchronized (history) {
            history.add(snapshot);
            if (history.size() > MAX_HISTORY_SIZE) history.removeFirst();
        }
        if (historyCursor == -1) Platform.runLater(() -> updateView(snapshot));
    }

    private void updateView(SimulationSnapshot snapshot) {
        updateHighlightFilters(snapshot);
        view.getMapVisualizer().draw(snapshot);
        view.getToolbar().getDayLabel().setText("Dzień: " + snapshot.day());
        view.getSidebar().getStatsView().update(snapshot.stats());

        if (historyCursor == -1) {
            view.getSidebar().getChartView().addDataPoint(snapshot.day(), snapshot.stats());
        }

        updateInspector(snapshot);
    }

    private void updateInspector(SimulationSnapshot snapshot) {
        if (trackedAnimalId == null) {
            view.getSidebar().getInspectorView().clear();
            view.getMapVisualizer().setHighlightedFamily(null, null);
            return;
        }

        Optional<TrackedAnimalStats> statsOpt = simulation.getAnimalDetails(trackedAnimalId, snapshot.day());

        if (statsOpt.isPresent()) {
            view.getSidebar().getInspectorView().update(statsOpt.get());
            updateFamilyHighlight();
        } else {
            view.getSidebar().getInspectorView().clear();
            view.getMapVisualizer().setHighlightedFamily(null, null);
        }
    }

    private void togglePlayPause() {
        boolean wasPaused = engine.isPaused();
        if (wasPaused) {
            historyCursor = -1;
            engine.setPaused(false);
        } else {
            engine.setPaused(true);
        }
        view.getToolbar().setPlayPauseStatus(!wasPaused);
    }

    private void handleManualStep(int direction) {
        engine.setPaused(true);
        view.getToolbar().setPlayPauseStatus(true);

        synchronized (history) {
            if (history.isEmpty()) return;

            int maxIndex = history.size() - 1;
            int currentIndex = (historyCursor == -1) ? maxIndex : historyCursor;
            int newIndex = currentIndex + direction;

            if (newIndex > maxIndex) {
                engine.requestSynchronousStep();
                historyCursor = -1;
                return;
            }

            if (newIndex >= 0) {
                historyCursor = newIndex;
                if (newIndex == maxIndex) historyCursor = -1;
                updateView(history.get(newIndex));
            }
        }
    }

    public void onWindowClose() {
        engine.stop();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Koniec symulacji");
        alert.setHeaderText("Czy chcesz zapisać statystyki do pliku CSV?");
        alert.setContentText("Wybierz opcję:");

        ButtonType buttonTypeYes = new ButtonType("Tak, zapisz");
        ButtonType buttonTypeNo = new ButtonType("Nie");
        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == buttonTypeYes) {
            showSaveFileDialog();
        }
    }

    private void showSaveFileDialog() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Zapisz statystyki");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Pliki CSV", "*.csv"));

        fileChooser.setInitialFileName("statystyki_" + System.currentTimeMillis() + ".csv");

        File file = fileChooser.showSaveDialog(view);
        if (file != null) {
            List<agh.ics.oop.simulation.SimulationStats> allStats = view.getSidebar().getChartView().getAllStatsHistory();
            StatisticsSaver.saveToFile(file, allStats);
        }
    }

    private SimulationSnapshot getCurrentSnapshot() {
        synchronized (history) {
            if (history.isEmpty()) return null;
            if (historyCursor != -1) return history.get(historyCursor);
            return history.getLast();
        }
    }
}