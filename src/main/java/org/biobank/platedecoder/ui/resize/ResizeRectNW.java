package org.biobank.platedecoder.ui.resize;

import javafx.scene.Cursor;

public class ResizeRectNW extends ResizeRect {

    protected static final Cursor MOUSE_ENTERED_CURSOR = Cursor.NW_RESIZE;

    public ResizeRectNW(ResizeHandler resizeHandler, double size) {
        super(resizeHandler, size, MOUSE_ENTERED_CURSOR);
    }

}
