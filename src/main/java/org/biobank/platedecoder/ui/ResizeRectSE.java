package org.biobank.platedecoder.ui;

import javafx.scene.Cursor;
import javafx.scene.Node;

public class ResizeRectSE extends ResizeRect {

    protected static final Cursor MOUSE_ENTERED_CURSOR = Cursor.SE_RESIZE;


    public ResizeRectSE(Node          parentNode,
                        double        size,
                        ResizeHandler resizeHandler) {
        super(parentNode, size, MOUSE_ENTERED_CURSOR, resizeHandler);
    }
}
