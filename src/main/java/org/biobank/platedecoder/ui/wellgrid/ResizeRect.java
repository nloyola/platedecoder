package org.biobank.platedecoder.ui.wellgrid;

import java.util.Optional;

import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * A rectangle, displayed to the user, that allows a parent object to be resized using the mouse.
 */
public abstract class ResizeRect extends Rectangle {

    protected final Node parentNode;

    protected Optional<Point2D> mouseLocationMaybe;

    protected double size;

    public ResizeRect(Node          parentNode,
                      double        size,
                      Cursor        mouseEnteredCursor,
                      final ResizeHandler resizeHandler) {
        super(0, 0, size, size);

        this.parentNode         = parentNode;
        this.size               = size;
        this.mouseLocationMaybe = Optional.empty();

        setFill(Color.LIGHTGREEN);

        setOnMouseEntered(event -> {
                this.parentNode.setCursor(mouseEnteredCursor);
            });

        setOnMouseExited(event -> {
                this.parentNode.setCursor(Cursor.DEFAULT);
            });

        setOnMousePressed(event -> {
                this.parentNode.setCursor(Cursor.CLOSED_HAND);
                mouseLocationMaybe = Optional.of(
                    new Point2D(event.getSceneX(), event.getSceneY()));
            });

        setOnMouseReleased(event -> {
                this.parentNode.setCursor(Cursor.DEFAULT);
                mouseLocationMaybe = Optional.empty();
            });

        setOnMouseDragged(event -> {
                mouseLocationMaybe.ifPresent(mouseLocation -> {
                        parentNode.setCursor(Cursor.CLOSED_HAND);

                        double deltaX = event.getSceneX() - mouseLocation.getX();
                        double deltaY = event.getSceneY() - mouseLocation.getY();

                        resizeHandler.mouseDragged(deltaX, deltaY);

                        mouseLocationMaybe = Optional.of(
                            new Point2D(event.getSceneX(), event.getSceneY()));
                        event.consume();
                    });
            });
    }

}
