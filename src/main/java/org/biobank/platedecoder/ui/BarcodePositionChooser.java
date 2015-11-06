package org.biobank.platedecoder.ui;

import org.biobank.platedecoder.model.BarcodePosition;
import org.biobank.platedecoder.model.PlateModel;

import javafx.beans.property.ObjectProperty;

/**
 * This widget allows the user to select the location of the 2D barcode on a tube.
 *
 * <p>The user's  selection is saved to PlateModel.
 */
public class BarcodePositionChooser extends RadioButtonChooser<BarcodePosition> {

    private final PlateModel model = PlateModel.getInstance();

    public BarcodePositionChooser(ObjectProperty<BarcodePosition> property) {
       super("Barcode positions", property);

        final BarcodePosition lastUsed = model.getBarcodePosition();

        for (BarcodePosition position : BarcodePosition.values()) {
            addButton(position.getDisplayLabel(), position, (lastUsed == position));
        }
    }

}
