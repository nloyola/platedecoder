package org.biobank.platedecoder.ui.wellgrid;

import javafx.scene.Cursor;

public class ResizeRectNW extends ResizeRect {

    protected static final Cursor MOUSE_ENTERED_CURSOR = Cursor.NW_RESIZE;

    public ResizeRectNW(WellGridHandler wellGridHandler,
                        double          size,
                        ResizeHandler   resizeHandler) {
        super(wellGridHandler, size, MOUSE_ENTERED_CURSOR, resizeHandler);
    }

}
