package org.biobank.platedecoder.ui.wellgrid;

import javafx.scene.Cursor;

public class ResizeRectSE extends ResizeRect {

    protected static final Cursor MOUSE_ENTERED_CURSOR = Cursor.SE_RESIZE;


    public ResizeRectSE(WellGridHandler wellGridHandler,
                        double          size,
                        ResizeHandler   resizeHandler) {
        super(wellGridHandler, size, MOUSE_ENTERED_CURSOR, resizeHandler);
    }
}
