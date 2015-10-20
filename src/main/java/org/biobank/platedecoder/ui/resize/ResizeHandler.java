package org.biobank.platedecoder.ui.resize;

import javafx.scene.Cursor;

public interface ResizeHandler {

    public void setResizeCursor(Cursor value);

    public void mouseDragged(ResizeHandle resizeRect, double deltaX, double deltaY);

}
