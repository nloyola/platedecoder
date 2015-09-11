package org.biobank.platedecoder.ui;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;

import org.biobank.platedecoder.model.PlateTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// for dragging and resizing see
// http://stackoverflow.com/questions/26298873/resizable-and-movable-rectangle

public class WellGrid extends Rectangle {

    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(WellGrid.class);

    private final Node parentNode;

    private final ImageView imageView;

    private PlateTypes plateType;

    private Point2D wellDimensions;

    private WellCell [] wellRectangles;

    private DoubleProperty displayScaleProperty = new SimpleDoubleProperty(1.0);

    public WellGrid(Node       parentNode,
                    ImageView  imageView,
                    PlateTypes plateType,
                    double     x,
                    double     y,
                    double     width,
                    double     height) {
        super(x, y, width, height);
        this.parentNode = parentNode;
        this.imageView = imageView;
        this.plateType = plateType;

        wellDimensions = new Point2D(getWidth() / plateType.getCols(),
                                     getHeight() / plateType.getRows());
    }

    public void plateTypeSelectionChanged(PlateTypes plateType) {
        this.plateType = plateType;
        double displayScale = displayScaleProperty.getValue();
        wellDimensions = new Point2D(displayScale * getWidth() / plateType.getCols(),
                                     displayScale * getHeight() / plateType.getRows());
    }

    public Rectangle [] getWellRectangles() {
        int count = 0;
        int rows = plateType.getRows();
        int cols = plateType.getCols();
        final double displayScale = displayScaleProperty.getValue();
        double wellGridX = getX() * displayScale;
        double offsetX = wellGridX;
        double offsetY = getY() * displayScale;
        double wellWidth = wellDimensions.getX();
        double wellHeight = wellDimensions.getY();
        double wellDisplayWidth = wellWidth - 2;
        double wellDisplayHeight = wellHeight - 2;
        wellRectangles = new WellCell [rows * cols];
        WellCell cell;

        for (int row = 0; row < rows; ++row) {
            for (int col = 0; col < cols; ++col) {
                // make the well slightly smaller so that user can see gaps between wells
                cell = new WellCell(
                    parentNode,
                    1,
                    1,
                    wellDisplayWidth,
                    wellDisplayHeight,
                    (deltaX,  deltaY) -> {
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
                        updateCells();
                    });

                cell.setTranslateX(offsetX);
                cell.setTranslateY(offsetY);
                wellRectangles[count] = cell;

                ++count;
                offsetX += wellWidth;
            }

            offsetX = wellGridX;
            offsetY += wellHeight;
        }
        return wellRectangles;
    }

    public Rectangle [] getResizeControls() {
        double size = 10;

        ResizeRectNW resizeRectNW = new ResizeRectNW(
            parentNode,
            size,
            (deltaX, deltaY) -> {
                double displayScale = displayScaleProperty.getValue();
                double x = getX();
                double y = getY();
                double width = getWidth();
                double height = getHeight();
                double adjustedSize = size / displayScale;
                double adjustedDeltaX = deltaX / displayScale;
                double adjustedDeltaY = deltaY / displayScale;

                double newX = Math.min(Math.max(0.0, x + adjustedDeltaX), x + width - adjustedSize);
                double newY = Math.min(Math.max(0.0, y + adjustedDeltaY), y + height - adjustedSize);

                double newWidth = Math.min(Math.max(adjustedSize, width - adjustedDeltaX), x + width);
                double newHeight = Math.min(Math.max(adjustedSize, height - adjustedDeltaY), y + height);

                resized(newX, newY, newWidth, newHeight);
            });

        resizeRectNW.xProperty().bind(xProperty().multiply(displayScaleProperty));
        resizeRectNW.yProperty().bind(yProperty().multiply(displayScaleProperty));

        ResizeRectSE resizeRectSE = new ResizeRectSE(
            parentNode,
            size,
            (deltaX, deltaY) -> {
                double displayScale = displayScaleProperty.getValue();
                double x = getX();
                double y = getY();
                double width = getWidth();
                double height = getHeight();
                double adjustedSize = size / displayScale;
                Image image = imageView.getImage();

                double newWidth = Math.min(Math.max(adjustedSize, width + deltaX / displayScale),
                                           image.getWidth() - x);
                double newHeight = Math.min(Math.max(adjustedSize, height + deltaY / displayScale),
                                            image.getHeight() - y);

                resized(x, y, newWidth, newHeight);
            });

        resizeRectSE.xProperty()
            .bind(xProperty().multiply(displayScaleProperty)
                  .add(widthProperty().multiply(displayScaleProperty)).subtract(size));
        resizeRectSE.yProperty()
            .bind(yProperty().multiply(displayScaleProperty)
                  .add(heightProperty().multiply(displayScaleProperty)).subtract(size));

        return new Rectangle [] { resizeRectNW, resizeRectSE };
    }

    public void setScale(double scale) {
        displayScaleProperty.setValue(scale);
        updateCells();
    }

    private void resized(double x, double y, double width, double height) {
        if ((x < 0.0) || (y < 0.0) || (width < 0.0) || (height < 0.0)) {
            throw new IllegalArgumentException("invalid dimensions");
        }

        // LOG.debug("resized: x: {}, y: {}", x, y);
        // LOG.debug("resized: width: {}, height: {}", width, height);

        setX(x);
        setY(y);
        setWidth(width);
        setHeight(height);
        updateCells();
    }

    private void updateCells() {
        double displayScale = displayScaleProperty.getValue();
        double wellWidth = displayScale * getWidth() / plateType.getCols();
        double wellHeight = displayScale * getHeight() / plateType.getRows();

        int count = 0;
        int rows = plateType.getRows();
        int cols = plateType.getCols();

        double xPosition = getX() * displayScale;
        double offsetX = xPosition;
        double offsetY = getY() * displayScale;

        for (int row = 0; row < rows; ++row) {
            for (int col = 0; col < cols; ++col) {
                Rectangle r = wellRectangles[count];
                r.setTranslateX(offsetX);
                r.setTranslateY(offsetY);
                r.setWidth(wellWidth);
                r.setHeight(wellHeight);

                ++count;
                offsetX += wellWidth;
            }

            offsetX = xPosition;
            offsetY += wellHeight;
        }
    }

}
