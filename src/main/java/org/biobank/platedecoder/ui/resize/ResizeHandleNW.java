package org.biobank.platedecoder.ui.resize;

import javafx.beans.property.DoubleProperty;
import javafx.scene.Cursor;

public class ResizeHandleNW extends ResizeHandle {

   protected static final Cursor MOUSE_ENTERED_CURSOR = Cursor.NW_RESIZE;

   /**
    * Manages a rectangle that can be used to resize a parent rectangle.
    *
    * <p>The resize control is placed in the North-West corner of the parent rectangle.
    *
    * @param resizeHandler the handler that will be informed when the user interacts with the
    * resize handle.
    */
   public ResizeHandleNW(ResizeHandler resizeHandler) {
      super(resizeHandler, MOUSE_ENTERED_CURSOR);

      DoubleProperty scaleProperty = resizeHandler.scaleProperty();

      this.xProperty().bind(resizeHandler.xPositionProperty().multiply(scaleProperty));
      this.yProperty().bind(resizeHandler.yPositionProperty().multiply(scaleProperty));
   }

}
