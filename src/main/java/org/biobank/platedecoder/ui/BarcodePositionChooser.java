package org.biobank.platedecoder.ui;

import org.biobank.platedecoder.model.BarcodePosition;
import org.biobank.platedecoder.model.PlateModel;

/**
 * This widget allows the user to select a the location of the barcode on a tube.

 * The user's  election is saved to PlateModel.
 */
public class BarcodePositionChooser extends RadioButtonChooser {

    private final PlateModel model = PlateModel.getInstance();

    public BarcodePositionChooser() {
        super("Barcode positions");

        final BarcodePosition lastUsed = model.getBarcodePosition();

        for (BarcodePosition position : BarcodePosition.values()) {
            addButton(position.getDisplayLabel(), position, (lastUsed == position));
        }
    }

    @Override
    protected void setValue(Object obj) {
        model.setBarcodePosition((BarcodePosition) obj);
    }

}
