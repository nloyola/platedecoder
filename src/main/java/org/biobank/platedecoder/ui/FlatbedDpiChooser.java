package org.biobank.platedecoder.ui;

import org.biobank.platedecoder.model.FlatbedDpi;
import org.biobank.platedecoder.model.PlateModel;

/**
 * A widget that allows the user to select a DPI setting.
 *
 * The user's  election is saved to PlateModel.
 */
public class FlatbedDpiChooser extends RadioButtonChooser<FlatbedDpi> {

    private final PlateModel model = PlateModel.getInstance();

    public FlatbedDpiChooser() {
        super("Select DPI");

        final FlatbedDpi lastUsedDpi = model.getFlatbedDpi();

        for (FlatbedDpi dpi : FlatbedDpi.values()) {
            addButton(dpi.toString(), dpi, (lastUsedDpi == dpi));
        }
    }

    @Override
    protected void setValue(FlatbedDpi dpi) {
        model.setFlatbedDpi(dpi);
    }

}
