package org.biobank.platedecoder.ui.wellgrid;

import javafx.scene.Cursor;
import javafx.scene.Node;

public class ResizeRectNW extends ResizeRect {

    protected static final Cursor MOUSE_ENTERED_CURSOR = Cursor.NW_RESIZE;

    public ResizeRectNW(Node          parent,
                        double        size,
                        ResizeHandler resizeHandler) {
        super(parent, size, MOUSE_ENTERED_CURSOR, resizeHandler);
    }

}
