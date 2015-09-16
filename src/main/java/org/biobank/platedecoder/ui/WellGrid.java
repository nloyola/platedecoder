package org.biobank.platedecoder.ui;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.HashMap;
import java.util.Map;

import org.biobank.platedecoder.model.BarcodePosition;
import org.biobank.platedecoder.model.PlateModel;
import org.biobank.platedecoder.model.PlateOrientation;
import org.biobank.platedecoder.model.PlateType;
import org.biobank.platedecoder.model.SbsLabeling;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// for dragging and resizing see
// http://stackoverflow.com/questions/26298873/resizable-and-movable-rectangle

/**
 * This class maintains the size of the grid and manages the rectangles corresponding to each
 * well that may contain the image of a tube.
 *
 * The well grid can be resized and moved using the mouse.
 */
public class WellGrid extends Rectangle {

    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(WellGrid.class);

    private final PlateModel model = PlateModel.getInstance();

    private final Node parentNode;

    private final ImageView imageView;

    private PlateType plateType;

    private Map<String, WellCell> wellRectangleMap = new HashMap<>();

    private DoubleProperty displayScaleProperty;

    /**
     * The well grid is superimposed on the image containinig the 2D barcodes. The image is scaled
     * to fit into the window displayed to the user. The {@code scale} is the scaling factor used to
     * display the image.
     */
    public WellGrid(Node       parentNode,
                    ImageView  imageView,
                    PlateType plateType,
                    double     x,
                    double     y,
                    double     width,
                    double     height,
                    double     scale) {
        super(x, y, width, height);
        this.parentNode = parentNode;
        this.imageView = imageView;
        this.plateType = plateType;
        this.displayScaleProperty = new SimpleDoubleProperty(scale);

        createWellCells();
    }

    private void createWellCells() {
        double displayScale = displayScaleProperty.getValue();
        int rows, cols;

        if (model.getPlateOrientation() == PlateOrientation.LANDSCAPE) {
            rows = plateType.getRows();
            cols = plateType.getCols();
        } else {
            rows = plateType.getCols();
            cols = plateType.getRows();
        }

        double wellWidth = displayScale * getWidth() / cols;
        double wellHeight = displayScale * getHeight() / rows;

        double wellGridX = displayScale * getX();
        double offsetX = wellGridX;
        double offsetY = displayScale * getY();
        double wellDisplayWidth = wellWidth - 2;
        double wellDisplayHeight = wellHeight - 2;
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
                String label = getLabelForGridPosition(row, col);

                if (label.equals("A1")) {
                    cell.setFill(Color.rgb(213, 236, 199, .7));
                }

                Tooltip.install(cell, new Tooltip(label));
                wellRectangleMap.put(label, cell);

                offsetX += wellWidth;
            }

            offsetX = wellGridX;
            offsetY += wellHeight;
        }
    }

    private void updateCells() {
        double displayScale = displayScaleProperty.getValue();
        int rows, cols;

        if (model.getPlateOrientation() == PlateOrientation.LANDSCAPE) {
            rows = plateType.getRows();
            cols = plateType.getCols();
        } else {
            rows = plateType.getCols();
            cols = plateType.getRows();
        }

        double wellWidth = displayScale * getWidth() / cols;
        double wellHeight = displayScale * getHeight() / rows;

        double xPosition = displayScale * getX();
        double offsetX = xPosition;
        double offsetY = displayScale * getY();

        for (int row = 0; row < rows; ++row) {
            for (int col = 0; col < cols; ++col) {
                String label = getLabelForGridPosition(row, col);
                Rectangle r = wellRectangleMap.get(label);
                if (r != null) {
                    r.setTranslateX(offsetX);
                    r.setTranslateY(offsetY);
                    r.setWidth(wellWidth);
                    r.setHeight(wellHeight);
                } else {
                    throw new IllegalStateException(
                        "updateCells: rectangle for label not found: " + label);
                }

                offsetX += wellWidth;
            }

            offsetX = xPosition;
            offsetY += wellHeight;
        }
    }

    public void plateTypeSelectionChanged(PlateType plateType) {
        this.plateType = plateType;
    }

    public Rectangle [] getWellCells() {
        return wellRectangleMap.values().toArray(new WellCell [] {});
    }

    private String getLabelForGridPosition(int row, int col) {
        int plateTypeCols = plateType.getCols();
        PlateOrientation plateOrientation = model.getPlateOrientation();
        BarcodePosition barcodePosition = model.getBarcodePosition();

        if (plateOrientation == PlateOrientation.LANDSCAPE) {
            if (barcodePosition == BarcodePosition.TOP) {
                return SbsLabeling.fromRowCol(row, col);
            }
            return SbsLabeling.fromRowCol(row, plateTypeCols - col - 1);
        }

        // orientation is PORTRAIT
        if (barcodePosition == BarcodePosition.TOP) {
            return SbsLabeling.fromRowCol(col, plateTypeCols - row - 1);
        }
        return SbsLabeling.fromRowCol(col, row);
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

    public void setScale(double scale) {
        LOG.debug("setScale: {}", scale);
        displayScaleProperty.setValue(scale);
        updateCells();
    }

    public double getScale() {
        return displayScaleProperty.getValue();
    }

}