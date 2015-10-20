package org.biobank.platedecoder.ui.scanregion;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

import java.util.Optional;

import org.biobank.platedecoder.ui.resize.ResizeHandler;
import org.biobank.platedecoder.ui.resize.ResizeHandle;
import org.biobank.platedecoder.ui.resize.ResizeHandleNW;
import org.biobank.platedecoder.ui.resize.ResizeHandleSE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class maintains the size of the grid and manages the rectangles corresponding to each
 * well that may contain the image of a tube.
 *
 * The well grid can be resized and moved using the mouse.
 *
 * For dragging and resizing see:
 *   http://stackoverflow.com/questions/26298873/resizable-and-movable-rectangle
 */
public class ScanRegion extends Rectangle implements ResizeHandler {

    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(ScanRegion.class);

    private ScanRegionHandler scanRegionHandler;

    private static final double RESIZE_RECT_SIZE = 30;

    private static final double STROKE_WIDTH = 1;

    private static final Paint STROKE_COLOR = Color.RED;

    private final ImageView imageView;

    /** Used to display the scanning region. */
    private Rectangle displayRegion;

    /** The scale used to display the image within the node that contains it. */
    private DoubleProperty displayScaleProperty;

    /** The scale used for the containing node. */
    private double imageZoomScale;

    private ResizeHandleNW  resizeRectNW;

    private ResizeHandleSE  resizeRectSE;

    protected Optional<Point2D> mouseLocationMaybe = Optional.empty();

    /**
     * The well grid is superimposed on the image containinig the 2D barcodes. The image is scaled
     * to fit into the window displayed to the user. The {@code scale} is the scaling factor used to
     * display the image.
     */
    public ScanRegion(final  ScanRegionHandler scanRegionHandler,
                      ImageView                imageView,
                      double                   x,
                      double                   y,
                      double                   width,
                      double                   height,
                      double                   scale) {
        super(x, y, width, height);displayRegion = new Rectangle(scale * x,
                                      scale * y,
                                      scale * width,
                                      scale * height);
        displayRegion.setFill(Color.TRANSPARENT);
        displayRegion.setStroke(STROKE_COLOR);
        displayRegion.setStrokeWidth(STROKE_WIDTH);

        imageZoomScale = 1.0;

        this.scanRegionHandler = scanRegionHandler;
        this.imageView = imageView;
        this.displayScaleProperty = new SimpleDoubleProperty(scale);

        setOnMouseEntered(event -> {
            scanRegionHandler.setCursor(Cursor.CLOSED_HAND);
        });

        setOnMouseExited(event -> {
            scanRegionHandler.setCursor(Cursor.DEFAULT);
        });

        setOnMouseReleased(event -> {
            scanRegionHandler.setCursor(Cursor.CLOSED_HAND);
            mouseLocationMaybe = Optional.empty();
        });

        setOnMousePressed(event -> {
            scanRegionHandler.setCursor(Cursor.MOVE);
            mouseLocationMaybe = Optional.of(new Point2D(event.getSceneX(), event.getSceneY()));
        });

        setOnMouseDragged(this::mouseDragged);
        setDisplayScale(scale);

        createResizeControls();
    }

    private void createResizeControls() {
        resizeRectNW = new ResizeHandleNW(this, xProperty(), yProperty(), displayScaleProperty);

        resizeRectSE = new ResizeHandleSE(this,
                                          xProperty(),
                                          yProperty(),
                                          widthProperty(),
                                          heightProperty(),
                                          displayScaleProperty);
    }

    public Rectangle getDisplayRegion() {
        return displayRegion;
    }

    /**
     * The user may be zoomed into or out of the image, this value tracks the amount of zoom.
     *
     * @param imageZoomScale the imageZoomScale to set
     */
    public void setImageZoomScale(double scale) {
        imageZoomScale = scale;
    }

    public void setDisplayScale(double scale) {
        displayScaleProperty.setValue(scale);
        resized(getX(), getY(), getWidth(), getHeight());
    }

    public double getDisplayScale() {
        return displayScaleProperty.getValue();
    }

    public Rectangle [] getResizeHandles() {
        return new Rectangle [] { resizeRectNW, resizeRectSE };
    }

    @Override
    public void setResizeCursor(Cursor value) {
        scanRegionHandler.setCursor(value);
    }

    /**
     * Called  when the user drags a cell.
     */
    private void mouseDragged(MouseEvent event) {
        mouseLocationMaybe.ifPresent(mouseLocation -> {
                event.consume();

                double deltaX = event.getSceneX() - mouseLocation.getX();
                double deltaY = event.getSceneY() - mouseLocation.getY();

                mouseLocationMaybe = Optional.of(
                    new Point2D(event.getSceneX(), event.getSceneY()));

                double dScale = displayScaleProperty.getValue();
                double adjustedDeltaX = deltaX / dScale;
                double adjustedDeltaY = deltaY / dScale;
                Image image = imageView.getImage();

                double newX = Math.min(Math.max(0.0, getX() + adjustedDeltaX),
                                       image.getWidth() - getWidth());
                double newY = Math.min(Math.max(0.0, getY() + adjustedDeltaY),
                                       image.getHeight() - getHeight());

                setX(newX);
                setY(newY);
            });
    }

    @Override
    public void mouseDragged(ResizeHandle resizeRect, double deltaX, double deltaY) {
        double displayScale = displayScaleProperty.getValue();
        double x = getX();
        double y = getY();
        double width = getWidth();
        double height = getHeight();
        double adjustedSize = RESIZE_RECT_SIZE / displayScale;
        double newWidth;
        double newHeight;

        // the user may be zoomed into or out of the image, need to adjust the resize by this scale.
        deltaX /= imageZoomScale;
        deltaY /= imageZoomScale;

        if (resizeRect == resizeRectNW) {
            double adjustedDeltaX = deltaX / displayScale;
            double adjustedDeltaY = deltaY / displayScale;

            // NOTE: x and y are re-assigned
            x = Math.min(Math.max(0.0, x + adjustedDeltaX), x + width - adjustedSize);
            y = Math.min(Math.max(0.0, y + adjustedDeltaY), y + height - adjustedSize);

            newWidth = Math.min(Math.max(adjustedSize, width - adjustedDeltaX), x + width);
            newHeight = Math.min(Math.max(adjustedSize, height - adjustedDeltaY), y + height);

        } else if (resizeRect == resizeRectSE) {
            Image image = imageView.getImage();

            newWidth = Math.min(Math.max(adjustedSize, width + deltaX / displayScale),
                                image.getWidth() - x);
            newHeight = Math.min(Math.max(adjustedSize, height + deltaY / displayScale),
                                 image.getHeight() - y);
        } else {
            throw new IllegalStateException(
                "invalid callback for resize: " + resizeRect);
        }

        resized(x, y, newWidth, newHeight);
    }

    private void resized(double x, double y, double width, double height) {
        if ((x < 0.0) || (y < 0.0) || (width < 0.0) || (height < 0.0)) {
            throw new IllegalArgumentException("invalid dimensions: " + x);
        }

        setX(x);
        setY(y);
        setWidth(width);
        setHeight(height);

        double displayScale = displayScaleProperty.getValue();

        displayRegion.setX(displayScale * x);
        displayRegion.setY(displayScale * y);
        displayRegion.setWidth(displayScale * width);
        displayRegion.setHeight(displayScale * height);
    }

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("[ x: ").append(getX());
        buf.append(", y: ").append(getY());
        buf.append(", width: ").append(getWidth());
        buf.append(", height: ").append(getHeight());
        buf.append(", scale: ").append(displayScaleProperty.getValue());
        buf.append(" ]");
        return buf.toString();
    }

}