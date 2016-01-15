package org.biobank.platedecoder.ui.resize;

import javafx.beans.property.DoubleProperty;
import javafx.scene.Cursor;

public class ResizeHandleSE extends ResizeHandle {

   protected static final Cursor MOUSE_ENTERED_CURSOR = Cursor.SE_RESIZE;

   /**
    * Manages a rectangle that can be used to resize a parent rectangle.
    *
    * <p>The resize control is placed in the South-East corner of the parent rectangle.
    *
    * @param resizeHandler the handler that will be informed when the user interacts with the
    * resize handle.
    */
   public ResizeHandleSE(ResizeHandler resizeHandler) {
      super(resizeHandler, MOUSE_ENTERED_CURSOR);

      DoubleProperty scaleProperty = resizeHandler.scaleProperty();

      this.xProperty().bind(resizeHandler.xPositionProperty()
                            .add(resizeHandler.widthProperty())
                            .multiply(scaleProperty)
                            .subtract(this.widthProperty()));
      this.yProperty().bind(resizeHandler.yPositionProperty()
                            .add(resizeHandler.heightProperty())
                            .multiply(scaleProperty)
                            .subtract(this.heightProperty()));
   }
}
