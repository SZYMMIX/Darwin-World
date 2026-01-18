package agh.ics.oop.app.components;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public class SimulationToolbar extends HBox {

    private final Button prevBtn = new Button("⏪");
    private final Button playPauseBtn = new Button("⏸");
    private final Button nextBtn = new Button("⏩");
    private final Slider speedSlider = new Slider(1, 20, 10);
    private final Label dayLabel = new Label("Dzień: 0");

    public SimulationToolbar() {
        setPadding(new Insets(10, 20, 10, 20));
        setAlignment(Pos.CENTER_LEFT);
        setStyle("-fx-background-color: #eee; -fx-border-color: #ccc; -fx-border-width: 0 0 1 0;");
        setSpacing(15);

        String btnStyle = "-fx-font-size: 16px; -fx-min-width: 40px; -fx-background-radius: 5px;";
        prevBtn.setStyle(btnStyle);
        playPauseBtn.setStyle(btnStyle + " -fx-font-weight: bold;");
        nextBtn.setStyle(btnStyle);

        Label speedLabel = new Label("Prędkość:");
        speedSlider.setPrefWidth(150);

        dayLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        getChildren().addAll(
                prevBtn,
                playPauseBtn,
                nextBtn,
                new Separator(),
                speedLabel,
                speedSlider,
                spacer,
                dayLabel
        );
    }

    public Button getPrevBtn() {
        return prevBtn;
    }

    public Button getPlayPauseBtn() {
        return playPauseBtn;
    }

    public Button getNextBtn() {
        return nextBtn;
    }

    public Slider getSpeedSlider() {
        return speedSlider;
    }

    public Label getDayLabel() {
        return dayLabel;
    }

    public void setPlayPauseStatus(boolean isPaused) {
        if (isPaused) {
            playPauseBtn.setText("▶");
        } else {
            playPauseBtn.setText("⏸");
        }
    }
}