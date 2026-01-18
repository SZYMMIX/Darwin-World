package agh.ics.oop.app;

import agh.ics.oop.app.components.SimulationToolbar;
import agh.ics.oop.simulation.Simulation;
import agh.ics.oop.simulation.SimulationParameters;
import agh.ics.oop.simulation.SimulationSnapshot;
import javafx.application.Platform;

import java.util.LinkedList;

public class SimulationPresenter {

    private final SimulationWindow view;
    private final SimulationEngine engine;
    private final LinkedList<SimulationSnapshot> history = new LinkedList<>();
    private static final int MAX_HISTORY_SIZE = 100;
    private int historyCursor = -1;

    public SimulationPresenter(SimulationWindow view, SimulationParameters params) {
        this.view = view;

        Simulation simulation = new Simulation(params);
        this.engine = new SimulationEngine(simulation);

        this.engine.addObserver(this::onSimulationStep);

        setupToolbarListeners();

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

    private void onSimulationStep(SimulationSnapshot snapshot) {
        synchronized (history) {
            history.add(snapshot);
            if (history.size() > MAX_HISTORY_SIZE) {
                history.removeFirst();
            }
        }

        if (historyCursor == -1) {
            Platform.runLater(() -> updateView(snapshot));
        }
    }

    private void updateView(SimulationSnapshot snapshot) {
        view.getMapVisualizer().draw(snapshot);
        view.getToolbar().getDayLabel().setText("Dzień: " + snapshot.day());
        view.getSidebar().getStatsView().update(snapshot.stats());
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

            if (newIndex >= 0 && newIndex <= maxIndex) {
                historyCursor = newIndex;
                if (newIndex == maxIndex) historyCursor = -1;

                SimulationSnapshot frame = history.get(newIndex);
                updateView(frame);
            }
        }
    }

    public void onWindowClose() {
        engine.stop();
    }
}