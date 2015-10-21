package org.biobank.platedecoder.ui;

import org.biobank.platedecoder.model.PlateModel;
import org.biobank.platedecoder.model.PlateOrientation;

/**
 * This widget allows the user to select a plate Orientation.

 * The user's  election is saved to PlateModel.
 */
public class PlateOrientationChooser extends RadioButtonChooser {

    private final PlateModel model = PlateModel.getInstance();

    public PlateOrientationChooser() {
        super("Orientation");

        final PlateOrientation lastUsedOrientation = model.getPlateOrientation();

        for (PlateOrientation orientation : PlateOrientation.values()) {
            addButton(orientation.getDisplayLabel(), orientation, (lastUsedOrientation == orientation));
        }
    }

    @Override
    protected void setValue(Object obj) {
        model.setPlateOrientation((PlateOrientation) obj);
    }

}
