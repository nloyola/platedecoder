package org.biobank.platedecoder.ui.resize;

import javafx.beans.property.DoubleProperty;
import javafx.scene.Cursor;

public class ResizeHandleNW extends ResizeHandle {

    protected static final Cursor MOUSE_ENTERED_CURSOR = Cursor.NW_RESIZE;

    /**
     * Manages a rectangle that can be used to resize a parent rectangle.
     *
     * The resize control is placed in the north west corner of the parent rectangle.
     *
     * @param resizeHandler the handler that will be informed when the user interacts with the
     * resize handle.
     *
     * @param xProperty the property that holds the X coordinate of the upper-left corner of the
     * parent rectangle.
     *
     * @param yProperty the property that holds the Y coordinate of the upper-left corner of the
     * parent rectangle.
     *
     * @param scaleProperty the amount of scaling used to display the parent rectangle.
     */
    public ResizeHandleNW(ResizeHandler resizeHandler,
                          DoubleProperty xProperty,
                          DoubleProperty yProperty,
                          DoubleProperty scaleProperty) {
        super(resizeHandler, RESIZE_RECT_SIZE, MOUSE_ENTERED_CURSOR);

        this.xProperty().bind(xProperty.multiply(scaleProperty));
        this.yProperty().bind(yProperty.multiply(scaleProperty));
        this.widthProperty().bind(scaleProperty.multiply(RESIZE_RECT_SIZE));
        this.heightProperty().bind(scaleProperty.multiply(RESIZE_RECT_SIZE));
    }

}
