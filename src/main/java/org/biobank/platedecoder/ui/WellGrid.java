package org.biobank.platedecoder.ui;

import javafx.geometry.Point2D;
import javafx.scene.Node;
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
        wellDimensions = new Point2D(getWidth() / plateType.getCols(),
                                     getHeight() / plateType.getRows());
    }

    public Rectangle [] getWellRectangles() {
        int count = 0;
        int rows = plateType.getRows();
        int cols = plateType.getCols();
        double wellGridX = getX();
        double offsetX = getX();
        double offsetY = getY();
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
                        double newX = Math.min(Math.max(0.0, getX() + deltaX),
                                               imageView.getFitWidth() - getWidth());
                        double newY = Math.min(Math.max(0.0, getY() + deltaY),
                                               imageView.getFitHeight() - getHeight());

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
        double size = wellDimensions.getX() / 5;

        ResizeRectNW resizeRectNW = new ResizeRectNW(
            parentNode,
            size,
            (deltaX, deltaY) -> {
                double x = getX();
                double y = getY();

                double width = getWidth();
                double height = getHeight();

                double newX = Math.min(Math.max(0.0, x + deltaX), x + width - size);
                double newY = Math.min(Math.max(0.0, y + deltaY), y + height - size);

                double newWidth = Math.min(Math.max(size, width - deltaX), x + width);
                double newHeight = Math.min(Math.max(size, height - deltaY), y + height);

                resized(newX, newY, newWidth, newHeight);
            });

        resizeRectNW.xProperty().bind(xProperty());
        resizeRectNW.yProperty().bind(yProperty());

        ResizeRectSE resizeRectSE = new ResizeRectSE(
            parentNode,
            size,
            (deltaX, deltaY) -> {
                double x = getX();
                double y = getY();

                double width = getWidth();
                double height = getHeight();

                double newWidth = Math.min(Math.max(size, width + deltaX),
                                           imageView.getFitWidth() - x);
                double newHeight = Math.min(Math.max(size, height + deltaY),
                                            imageView.getFitHeight() - y);

                resized(x, y, newWidth, newHeight);
            });

        resizeRectSE.xProperty().bind(
            xProperty().add(widthProperty()).subtract(size));
        resizeRectSE.yProperty().bind(
            yProperty().add(heightProperty()).subtract(size));

        return new Rectangle [] { resizeRectNW, resizeRectSE };
    }

    private void resized(double x, double y, double width, double height) {
        if ((x < 0.0) || (y < 0.0) || (width < 0.0) || (height < 0.0)) {
            throw new IllegalArgumentException("invalid dimensions");
        }

        setX(x);
        setY(y);
        setWidth(width);
        setHeight(height);
        updateCells();
    }

    private void updateCells() {
        double wellWidth = getWidth() / plateType.getCols();
        double wellHeight = getHeight() / plateType.getRows();

        int count = 0;
        int rows = plateType.getRows();
        int cols = plateType.getCols();

        double xPosition = getX();
        double offsetX = xPosition;
        double offsetY = getY();

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
