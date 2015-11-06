package org.biobank.platedecoder.ui;

import org.biobank.platedecoder.model.PlateModel;
import org.biobank.platedecoder.model.PlateOrientation;

import javafx.beans.property.ObjectProperty;

/**
 * This widget allows the user to select a plate Orientation.

 * The user's  election is saved to PlateModel.
 */
public class PlateOrientationChooser extends RadioButtonChooser<PlateOrientation> {

    private final PlateModel model = PlateModel.getInstance();

    public PlateOrientationChooser(ObjectProperty<PlateOrientation> property) {
       super("Orientation", property);

        final PlateOrientation lastUsedOrientation = model.getPlateOrientation();

        for (PlateOrientation orientation : PlateOrientation.values()) {
            addButton(orientation.getDisplayLabel(), orientation, (lastUsedOrientation == orientation));
        }
    }

}
