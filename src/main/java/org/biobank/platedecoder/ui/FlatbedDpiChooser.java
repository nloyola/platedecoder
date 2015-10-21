package org.biobank.platedecoder.ui;

import org.biobank.platedecoder.model.FlatbedDpi;
import org.biobank.platedecoder.model.PlateModel;

/**
 * A widget that allows the user to select a DPI setting.
 *
 * The user's  election is saved to PlateModel.
 */
public class FlatbedDpiChooser extends RadioButtonChooser {

    private final PlateModel model = PlateModel.getInstance();

    public FlatbedDpiChooser() {
        super("DPI");

        final FlatbedDpi lastUsedDpi = model.getFlatbedDpi();

        for (FlatbedDpi dpi : FlatbedDpi.values()) {
            addButton(dpi.toString(), dpi, (lastUsedDpi == dpi));
        }
    }

    @Override
    protected void setValue(Object obj) {
        model.setFlatbedDpi((FlatbedDpi) obj);
    }

}
