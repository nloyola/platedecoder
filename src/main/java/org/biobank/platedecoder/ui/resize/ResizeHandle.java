package org.biobank.platedecoder.ui.resize;

import java.util.Optional;

import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * A rectangle, displayed to the user, that allows a parent object to be resized using the mouse.
 */
public abstract class ResizeHandle extends Rectangle {

    protected static final double RESIZE_RECT_SIZE = 30;

    protected final ResizeHandler resizeHandler;

    protected Optional<Point2D> mouseLocationMaybe;

    protected double size;

    public ResizeHandle(final ResizeHandler resizeHandler,
                        double              size,
                        Cursor              mouseEnteredCursor) {
        super(0, 0, size, size);

        this.resizeHandler      = resizeHandler;
        this.mouseLocationMaybe = Optional.empty();

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

}
