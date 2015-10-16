package org.biobank.platedecoder.ui.resize;

import javafx.scene.Cursor;

public class ResizeRectSE extends ResizeRect {

    protected static final Cursor MOUSE_ENTERED_CURSOR = Cursor.SE_RESIZE;

    public ResizeRectSE(ResizeHandler resizeHandler, double size) {
        super(resizeHandler, size, MOUSE_ENTERED_CURSOR);
    }
}
