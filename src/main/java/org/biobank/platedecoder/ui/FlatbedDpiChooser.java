package org.biobank.platedecoder.ui;

import org.biobank.platedecoder.model.FlatbedDpi;
import org.biobank.platedecoder.model.PlateModel;

import javafx.beans.property.ObjectProperty;

/**
 * A widget that allows the user to select a DPI setting.
 *
 * The user's  election is saved to PlateModel.
 */
public class FlatbedDpiChooser extends RadioButtonChooser<FlatbedDpi> {

    private final PlateModel model = PlateModel.getInstance();

    public FlatbedDpiChooser(ObjectProperty<FlatbedDpi> property) {
       super("Select DPI", property);

        final FlatbedDpi lastUsedDpi = model.getFlatbedDpi();

        for (FlatbedDpi dpi : FlatbedDpi.values()) {
            addButton(dpi.toString(), dpi, (lastUsedDpi == dpi));
        }
    }

}
