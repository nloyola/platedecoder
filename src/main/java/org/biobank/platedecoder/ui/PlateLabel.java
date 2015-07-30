package org.biobank.platedecoder.ui;

import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.Control;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;

public class PlateLabel extends Region {

    private Text text;

    public PlateLabel(String label) {
        setPrefSize(20, 40);
        setMinHeight(Control.USE_PREF_SIZE);
        setMaxHeight(Control.USE_PREF_SIZE);
        text = new Text(label);
        getChildren().add(text);
    }

    public PlateLabel(char label) {
        this(String.valueOf(label));
    }

    @Override
    protected void layoutChildren() {
        layoutInArea(text, 0, 0, getWidth(), getHeight(), getBaselineOffset(),
                     HPos.CENTER, VPos.CENTER);
    }
}
