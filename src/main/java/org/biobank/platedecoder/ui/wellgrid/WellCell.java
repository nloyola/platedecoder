package org.biobank.platedecoder.ui.wellgrid;

import java.util.Optional;

import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
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

    private final String label;

    private String inventoryId = "";

    private final WellGridHandler wellGridHandler;

    public WellCell(final WellGridHandler wellGridHandler,
                    String label,
                    double x,
                    double y,
                    double width,
                    double height) {
        super(x, y, width, height);

        this.label = label;
        this.wellGridHandler = wellGridHandler;

        setArcWidth(ARC_WIDTH);
        setArcHeight(ARC_HEIGHT);
        setFill(Color.TRANSPARENT);
        setStroke(STROKE_COLOR);
        setStrokeWidth(STROKE_WIDTH);

        setOnMouseEntered(event -> {
            wellGridHandler.setCursor(Cursor.CLOSED_HAND);
        });

        setOnMouseExited(event -> {
            wellGridHandler.setCursor(Cursor.DEFAULT);
        });

        setOnMouseReleased(event -> {
            wellGridHandler.setCursor(Cursor.CLOSED_HAND);
            mouseLocationMaybe = Optional.empty();
        });

        setOnMousePressed(event -> {
            wellGridHandler.setCursor(Cursor.MOVE);
            mouseLocationMaybe = Optional.of(new Point2D(event.getSceneX(), event.getSceneY()));
        });

        setOnMouseDragged(this::mouseDragged);
        setOnMouseClicked(this::doubleClick);
    }

    public String getLabel() {
        return label;
    }

    public String getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(String id) {
        inventoryId = id;
    }

    /**
     * Called  when the user drags a cell.
     */
    private void mouseDragged(MouseEvent event) {
        mouseLocationMaybe.ifPresent(mouseLocation -> {
                double deltaX = event.getSceneX() - mouseLocation.getX();
                double deltaY = event.getSceneY() - mouseLocation.getY();

                wellGridHandler.cellMoved(this, deltaX, deltaY);

                mouseLocationMaybe = Optional.of(
                    new Point2D(event.getSceneX(), event.getSceneY()));
                event.consume();
            });
    }

    /**
     * Called  when the user double clicks on a cell.
     */
    private void doubleClick(MouseEvent event) {
        if (event.getButton().equals(MouseButton.PRIMARY)
            && (event.getClickCount() == 2)) {
            wellGridHandler.manualDecode(this);
        }
    }

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append(label);
        buf.append(": [ inventoryId: \"");
        buf.append(inventoryId);
        buf.append("\",\n\t rect: [ x: ");
        buf.append(getTranslateX());
        buf.append(", y: ");
        buf.append(getTranslateY());
        buf.append(", width: ");
        buf.append(getWidth());
        buf.append(", height: ");
        buf.append(getHeight());
        buf.append(" ] ]");
        return buf.toString();
    }

}
