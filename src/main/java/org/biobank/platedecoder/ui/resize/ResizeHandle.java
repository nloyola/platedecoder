package org.biobank.platedecoder.ui.resize;

import java.util.Optional;

import javafx.beans.property.DoubleProperty;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * A rectangle, displayed to the user, that allows a parent object to be resized using the mouse.
 */
public abstract class ResizeHandle extends Rectangle {

   public static final double RESIZE_RECT_RATIO = 0.03;

   protected final ResizeHandler resizeHandler;

   protected Optional<Point2D> mouseLocationMaybe;

   protected double size;

   /**
    * Manages a rectangle that can be used to resize a parent rectangle.
    *
    * The resize control is placed in the north west corner of the parent rectangle.
    *
    * @param resizeHandler the handler that will be informed when the user interacts with the
    * resize handle.
    *
    * @param mouseEnteredCursor The cursor to display when the user hovers the mouse of the resize
    * handle.
    */
   public ResizeHandle(final ResizeHandler resizeHandler, Cursor mouseEnteredCursor) {
      super(0, 0, 1, 1);

      this.resizeHandler      = resizeHandler;
      this.mouseLocationMaybe = Optional.empty();

      DoubleProperty parentWidthProperty = resizeHandler.widthProperty();
      DoubleProperty scaleProperty = resizeHandler.scaleProperty();

      this.widthProperty().bind(
         parentWidthProperty.multiply(scaleProperty.multiply(RESIZE_RECT_RATIO)));
      this.heightProperty().bind(
         parentWidthProperty.multiply(scaleProperty.multiply(RESIZE_RECT_RATIO)));

      setFill(Color.LIGHTGREEN);

      setOnMouseEntered(event -> {
            this.resizeHandler.setResizeCursor(mouseEnteredCursor);
         });

      setOnMouseExited(event -> {
            this.resizeHandler.setResizeCursor(Cursor.DEFAULT);
         });

      setOnMousePressed(event -> {
            this.resizeHandler.setResizeCursor(Cursor.CLOSED_HAND);
            mouseLocationMaybe = Optional.of(
               new Point2D(event.getSceneX(), event.getSceneY()));
         });

      setOnMouseReleased(event -> {
            this.resizeHandler.setResizeCursor(Cursor.DEFAULT);
            mouseLocationMaybe = Optional.empty();
         });

      setOnMouseDragged(event -> {
            mouseLocationMaybe.ifPresent(mouseLocation -> {
                  resizeHandler.setResizeCursor(Cursor.CLOSED_HAND);

                  double deltaX = event.getSceneX() - mouseLocation.getX();
                  double deltaY = event.getSceneY() - mouseLocation.getY();

                  resizeHandler.mouseDragged(this, deltaX, deltaY);

                  mouseLocationMaybe = Optional.of(
                     new Point2D(event.getSceneX(), event.getSceneY()));
                  event.consume();
               });
         });
   }

   public double getSize() {
      return getWidth();
   }

}
