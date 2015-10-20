package org.biobank.platedecoder.ui.resize;

import javafx.beans.property.DoubleProperty;
import javafx.scene.Cursor;

public class ResizeHandleSE extends ResizeHandle {

    protected static final Cursor MOUSE_ENTERED_CURSOR = Cursor.SE_RESIZE;

    public ResizeHandleSE(ResizeHandler resizeHandler,
                          DoubleProperty xProperty,
                          DoubleProperty yProperty,
                          DoubleProperty widthProperty,
                          DoubleProperty heightProperty,
                          DoubleProperty scaleProperty) {
        super(resizeHandler, RESIZE_RECT_SIZE, MOUSE_ENTERED_CURSOR);

        this.xProperty()
            .bind(xProperty.add(widthProperty).subtract(RESIZE_RECT_SIZE)
                  .multiply(scaleProperty));
        this.yProperty()
            .bind(yProperty.add(heightProperty.subtract(RESIZE_RECT_SIZE))
                  .multiply(scaleProperty));
        this.widthProperty().bind(scaleProperty.multiply(RESIZE_RECT_SIZE));
        this.heightProperty().bind(scaleProperty.multiply(RESIZE_RECT_SIZE));
    }
}
