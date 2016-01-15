package org.biobank.platedecoder.ui.resize;

import javafx.beans.property.DoubleProperty;
import javafx.scene.Cursor;

/**
 * These methods are invoked by the resize handles when the user uses them to perform an action.
 *
 */
public interface ResizeHandler {

   /**
    * Set the cursor.
    *
    * <p>Used by the resize handlers to resize the grid.
    *
    * @param value the type of mouse cursor to display.
    */
   public void setResizeCursor(Cursor value);

   /**
    * Used when a resize handle ls is moved by dragging it with the mouse.
    *
    * @param handle The resize handle being dragged by the user.
    *
    * @param deltaX The amount it was dragged in the X direction.
    *
    * @param deltaY The amount it was dragged in the Y direction.
    */
   public void mouseDragged(ResizeHandle handle, double deltaX, double deltaY);

   /**
    * The property that holds the X coordinate of the upper-left corner of the
    * parent rectangle.
    */
   public DoubleProperty xPositionProperty();

   /**
    * The property that holds the Y coordinate of the upper-left corner of the
    * parent rectangle.
    */
   public DoubleProperty yPositionProperty();

   /**
    * The property that holds the width value of the parent rectangle.
    */
   public DoubleProperty widthProperty();

   /**
    * The property that holds the height value of the parent rectangle.
    */
   public DoubleProperty heightProperty();

   /**
    * The property that holds the scale value to display the parent rectangle.
    *
    */
   public DoubleProperty scaleProperty();

}
