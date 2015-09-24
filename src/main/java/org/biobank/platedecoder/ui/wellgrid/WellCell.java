package org.biobank.platedecoder.ui.wellgrid;

import java.util.Optional;

import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WellCell extends Rectangle {

    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(WellCell.class);

    private static final double ARC_WIDTH = 10;

    private static final double ARC_HEIGHT = 10;

    private static final double STROKE_WIDTH = 2;

    private static final Paint STROKE_COLOR = Color.GREEN;

    protected Optional<Point2D> mouseLocationMaybe = Optional.empty();

    private String inventoryId = "";

    public WellCell(final Node         parentNode,
                    double             x,
                    double             y,
                    double             width,
                    double             height,
                    final MovedHandler movedHandler) {
        super(x, y, width, height);

        setArcWidth(ARC_WIDTH);
        setArcHeight(ARC_HEIGHT);
        setFill(Color.TRANSPARENT);
        setStroke(STROKE_COLOR);
        setStrokeWidth(STROKE_WIDTH);

        setOnMouseEntered(event -> {
                parentNode.setCursor(Cursor.CLOSED_HAND);
            });

        setOnMouseExited(event -> {
                parentNode.setCursor(Cursor.DEFAULT);
            });

        setOnMouseReleased(event -> {
                parentNode.setCursor(Cursor.CLOSED_HAND);
                mouseLocationMaybe = Optional.empty();
            });

        setOnMousePressed(event -> {
                parentNode.setCursor(Cursor.MOVE);
                mouseLocationMaybe = Optional.of(
                    new Point2D(event.getSceneX(), event.getSceneY()));
            });

        setOnMouseDragged(event -> {
                mouseLocationMaybe.ifPresent(mouseLocation -> {
                        double deltaX = event.getSceneX() - mouseLocation.getX();
                        double deltaY = event.getSceneY() - mouseLocation.getY();

                        movedHandler.moved(deltaX, deltaY);

                        mouseLocationMaybe = Optional.of(
                            new Point2D(event.getSceneX(), event.getSceneY()));
                        event.consume();
                    });
            });
    }

    public String getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(String id) {
        inventoryId = id;
    }

}
