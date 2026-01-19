package agh.ics.oop.app;

import agh.ics.oop.app.components.InspectorView;
import agh.ics.oop.app.components.SimulationToolbar;
import agh.ics.oop.model.Genotype;
import agh.ics.oop.model.TrackedAnimalStats;
import agh.ics.oop.simulation.GenotypeStat;
import agh.ics.oop.simulation.Simulation;
import agh.ics.oop.simulation.SimulationParameters;
import agh.ics.oop.simulation.SimulationSnapshot;
import javafx.application.Platform;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

public class SimulationPresenter {
    private final SimulationWindow view;
    private final SimulationEngine engine;
    private final Simulation simulation;

    private final SimulationTimeline timeline = new SimulationTimeline();
    private final SimulationExporter exporter = new SimulationExporter();

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
        timeline.add(initialSnapshot);
        updateView(initialSnapshot);

        view.getToolbar().setPlayPauseStatus(true);
        startEngineThread();
    }

    private void startEngineThread() {
        Thread thread = new Thread(engine);
        thread.setDaemon(true);
        thread.start();
    }

    private void setupToolbarListeners() {
        SimulationToolbar toolbar = view.getToolbar();
        toolbar.getPlayPauseBtn().setOnAction(e -> togglePlayPause());

        toolbar.getSpeedSlider().valueProperty().addListener((obs,
                                                              oldVal,
                                                              newVal) -> {
            int delay = 600 - (newVal.intValue() * 28);
            engine.setMoveDelay(delay);
        });

        toolbar.getPrevBtn().setOnAction(e -> handleManualStep(-1));
        toolbar.getNextBtn().setOnAction(e -> handleManualStep(1));
    }

    private void setupMapListeners() {
        view.getMapVisualizer().getCanvas().setOnMouseClicked(event -> {
            SimulationSnapshot current = timeline.getCurrent();
            if (current != null) {
                Integer clickedId = view.getMapVisualizer().getAnimalIdAt(event.getX(), event.getY(), current);
                handleAnimalSelection(clickedId);
            }
        });
    }

    private void setupSidePanelListeners() {
        InspectorView inspector = view.getSidebar().getInspectorView();

        inspector.getHighlightChildrenCheck().selectedProperty().addListener((obs,
                                                                              oldV,
                                                                              newV) -> updateFamilyHighlight());
        inspector.getHighlightDescendantsCheck().selectedProperty().addListener((obs,
                                                                                 oldV,
                                                                                 newV) -> updateFamilyHighlight());
        inspector.getStopTrackingButton().setOnAction(e -> handleAnimalSelection(null));

        view.getSidebar().getStatsView().getTopGenotypesList()
                .getSelectionModel().selectedItemProperty()
                .addListener((obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        this.selectedGenotypeToHighlight = newVal.genotype();
                    }
                    updateHighlightFilters(timeline.getCurrent());
                });
    }

    private void onSimulationStep(SimulationSnapshot snapshot) {
        timeline.add(snapshot);
        if (timeline.isLive()) {
            Platform.runLater(() -> updateView(snapshot));
        }
    }

    private void togglePlayPause() {
        boolean wasPaused = engine.isPaused();
        if (wasPaused) {
            timeline.resetCursorToLive();
            engine.setPaused(false);
        } else {
            engine.setPaused(true);
        }
        view.getToolbar().setPlayPauseStatus(!wasPaused);
    }

    private void handleManualStep(int direction) {
        engine.setPaused(true);
        view.getToolbar().setPlayPauseStatus(true);

        if (timeline.shiftCursor(direction)) {
            updateView(timeline.getCurrent());
        } else if (direction > 0 && timeline.isLive()) {
            engine.requestSynchronousStep();
        }
    }

    public void onWindowClose() {
        engine.stop();
        exporter.askOnClose(view, view.getSidebar().getChartView());
    }

    private void updateView(SimulationSnapshot snapshot) {
        if (snapshot == null) return;

        updateHighlightFilters(snapshot);
        view.getMapVisualizer().draw(snapshot);
        view.getToolbar().getDayLabel().setText("Dzień: " + snapshot.day());
        view.getSidebar().getStatsView().update(snapshot.stats());

        if (timeline.isLive()) {
            view.getSidebar().getChartView().addDataPoint(snapshot.day(), snapshot.stats());
        }

        updateInspector(snapshot);
    }

    private void handleAnimalSelection(Integer animalId) {
        this.trackedAnimalId = animalId;
        view.getMapVisualizer().setTrackedAnimalId(animalId);
        updateInspector(timeline.getCurrent());
        if (animalId != null) {
            view.getSidebar().selectInspectorTab();
        }
    }

    private void updateInspector(SimulationSnapshot snapshot) {
        if (trackedAnimalId == null || snapshot == null) {
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

    private void updateFamilyHighlight() {
        if (trackedAnimalId == null) {
            view.getMapVisualizer().setHighlightedFamily(null, null);
            return;
        }
        SimulationSnapshot snap = timeline.getCurrent();
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
}