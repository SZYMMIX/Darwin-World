package agh.ics.oop.app;

import agh.ics.oop.app.components.SimulationToolbar;
import agh.ics.oop.app.model.AnimalViewModel;
import agh.ics.oop.simulation.GenotypeStat;
import agh.ics.oop.simulation.Simulation;
import agh.ics.oop.simulation.SimulationParameters;
import agh.ics.oop.simulation.SimulationSnapshot;
import javafx.application.Platform;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class SimulationPresenter {
    private final SimulationWindow view;
    private final SimulationEngine engine;
    private final Simulation simulation;

    private final LinkedList<SimulationSnapshot> history = new LinkedList<>();
    private static final int MAX_HISTORY_SIZE = 100;
    private int historyCursor = -1;

    private boolean isHighlightingDominant = false;
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

    private void setupSidePanelListeners() {}

    private void handleAnimalSelection(Integer animalId) {
        this.trackedAnimalId = animalId;
        view.getMapVisualizer().setTrackedAnimalId(animalId);
        updateInspector(getCurrentSnapshot());
    }

    private void toggleDominantHighlight() {
        this.isHighlightingDominant = !this.isHighlightingDominant;
        updateHighlightFilters(getCurrentSnapshot());
    }

    private void updateHighlightFilters(SimulationSnapshot snapshot) {
        if (snapshot == null) return;

        if (isHighlightingDominant) {
            List<GenotypeStat> tops = snapshot.stats().topGenotypes();
            if (!tops.isEmpty()) {
                List<Integer> ids = tops.get(0).animalIds();
                view.getMapVisualizer().setAnimalsWithDominantGenotype(ids);
            }
        } else {
            view.getMapVisualizer().setAnimalsWithDominantGenotype(Collections.emptyList());
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
            view.getSidebar().getChartView().addDataPoint(
                    snapshot.day(),
                    snapshot.stats().currentAnimalsCount(),
                    snapshot.stats().currentPlantsCount()
            );
        }

        updateInspector(snapshot);
    }

    private void updateInspector(SimulationSnapshot snapshot) {
        if (trackedAnimalId == null) {
            view.getSidebar().getInspectorView().clear();
            return;
        }

        var trackedSnap = snapshot.animals().stream()
                .filter(a -> a.id() == trackedAnimalId)
                .findFirst();

        if (trackedSnap.isPresent()) {
            AnimalViewModel vm = new AnimalViewModel(
                    trackedAnimalId,
                    trackedSnap.get().position(),
                    null,
                    0,
                    trackedSnap.get().energy(),
                    0, 0, 0, 0,
                    Optional.empty(),
                    false
            );
            view.getSidebar().getInspectorView().update(vm);
        } else {
            AnimalViewModel deadVm = new AnimalViewModel(
                    trackedAnimalId, null, null, 0, 0, 0, 0, 0, 0, Optional.empty(), true
            );
            view.getSidebar().getInspectorView().update(deadVm);
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

    public void onWindowClose() { engine.stop(); }

    private SimulationSnapshot getCurrentSnapshot() {
        synchronized (history) {
            if (history.isEmpty()) return null;
            if (historyCursor != -1) return history.get(historyCursor);
            return history.getLast();
        }
    }
}