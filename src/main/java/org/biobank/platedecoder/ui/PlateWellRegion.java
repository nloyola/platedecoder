package org.biobank.platedecoder.ui;

import javafx.animation.FadeTransition;
import javafx.beans.binding.Bindings;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.Control;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.util.Duration;

import org.biobank.platedecoder.model.PlateWell;

public class PlateWellRegion extends Region {

    private final Region highlight;
    private final FadeTransition highlightTransition;

    @SuppressWarnings("unused")
    private final int row;

    @SuppressWarnings("unused")
    private final int col;

    private final PlateWell well;

    public PlateWellRegion(PlateWell well, final int row, final int col) {
        this.well = well;
        this.row = row;
        this.col = col;

        highlight = new Region();
        highlight.setOpacity(0);
        highlight.setStyle("-fx-border-width: 3; -fx-border-color: #424242");

        highlightTransition = new FadeTransition(Duration.millis(200), highlight);
        highlightTransition.setFromValue(0);
        highlightTransition.setToValue(1);

        styleProperty().bind(
            Bindings.when(well.getSelectedProperty())
            .then("-fx-background-color: #2EFE64")
            .otherwise(Bindings.when(well.getFilledProperty())
                       .then("-fx-background-color: #FBF800")
                       .otherwise("-fx-background-color: #FBF8EF")));

        Light.Distant light = new Light.Distant();
        light.setAzimuth(-135);
        light.setElevation(30);
        setEffect(new Lighting(light));
        setPrefSize(60, 60);
        setMinHeight(Control.USE_PREF_SIZE);
        setMaxHeight(Control.USE_PREF_SIZE);
        getChildren().add(highlight);
        addEventHandler(MouseEvent.MOUSE_ENTERED_TARGET, t -> {
            highlightTransition.setRate(1);
            highlightTransition.play();
        });
        addEventHandler(MouseEvent.MOUSE_EXITED_TARGET, t -> {
            highlightTransition.setRate(-1);
            highlightTransition.play();
        });
        setOnMouseClicked(t -> {
                boolean selectedRegionEnd = t.isShiftDown();
                boolean addToSelection = t.isControlDown();
                well.userSelected(selectedRegionEnd, addToSelection);
        });
    }

    public PlateWell getWell() {
        return well;
    }

    @Override
    protected void layoutChildren() {
        layoutInArea(highlight, 0, 0, getWidth(), getHeight(), getBaselineOffset(),
            HPos.CENTER, VPos.CENTER);
    }

}
