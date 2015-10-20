package org.biobank.platedecoder.ui.wellgrid;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Cursor;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import static org.biobank.platedecoder.dmscanlib.CellRectangle.*;
import org.biobank.platedecoder.model.PlateModel;
import org.biobank.platedecoder.model.PlateOrientation;
import org.biobank.platedecoder.model.PlateType;
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
public class WellGrid extends Rectangle implements ResizeHandler {

    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(WellGrid.class);

    private static final Color A1_CELL_FILL_COLOR = Color.rgb(189, 237, 255, 0.35);

    private static final Color DECODED_CELL_FILL_COLOR = Color.rgb(153, 198, 142, .25);

    private final PlateModel model = PlateModel.getInstance();

    private final WellGridHandler wellGridHandler;

    private final ImageView imageView;

    private PlateType plateType;

    private Map<String, WellCell> wellCellMap = new HashMap<>();

    private Map<String, ImageView> wellDecodedIconMap = new HashMap<>();

    private DoubleProperty displayScaleProperty;

    private final Image wellDecodedImage;

    private ResizeHandleNW resizeRectNW;

    private ResizeHandleSE resizeRectSE;

    /**
     * The well grid is superimposed on the image containinig the 2D barcodes. The image is scaled
     * to fit into the window displayed to the user. The {@code scale} is the scaling factor used to
     * display the image.
     */
    public WellGrid(final WellGridHandler wellGridHandler,
                    ImageView  imageView,
                    PlateType plateType,
                    double     x,
                    double     y,
                    double     width,
                    double     height,
                    double     scale) {
        super(x, y, width, height);

        this.wellGridHandler = wellGridHandler;
        this.imageView = imageView;
        this.plateType = plateType;
        this.displayScaleProperty = new SimpleDoubleProperty(scale);

        wellDecodedImage = new Image(WellGrid.class.getResourceAsStream("accept.png"));

        createResizeHandles();
        createWellCells();
        createWellDecodedIcons();
    }

    private void createResizeHandles() {
        resizeRectNW = new ResizeHandleNW(this, xProperty(), yProperty(), displayScaleProperty);

        resizeRectSE = new ResizeHandleSE(this,
                                          xProperty(),
                                          yProperty(),
                                          widthProperty(),
                                          heightProperty(),
                                          displayScaleProperty);
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
                String label = getLabelForGridPosition(row, col);

                // make the well slightly smaller so that user can see gaps between wells
                cell = new WellCell(wellGridHandler,
                                    label,
                                    1,
                                    1,
                                    wellDisplayWidth,
                                    wellDisplayHeight);

                cell.setTranslateX(offsetX);
                cell.setTranslateY(offsetY);

                if (label.equals("A1")) {
                    cell.setFill(A1_CELL_FILL_COLOR);
                }

                Tooltip.install(cell, new Tooltip(label));

                wellCellMap.put(label, cell);
                offsetX += wellWidth;
            }

            offsetX = wellGridX;
            offsetY += wellHeight;
        }
    }

    public void cellMoved(@SuppressWarnings("unused") WellCell cell,
                          double deltaX,
                          double deltaY) {
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
        update();
    }

    public void createWellDecodedIcons() {
        for (Entry<String, WellCell> entry : wellCellMap.entrySet()) {
            String label = entry.getKey();

            ImageView iv = new ImageView();
            iv.setImage(wellDecodedImage);
            iv.setPreserveRatio(true);
            iv.setSmooth(true);
            iv.setVisible(false);
            wellDecodedIconMap.put(label, iv);
        }
    }

    public void update() {
        updateCells();
        updateWellDecodedIcons();
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
                WellCell cell = wellCellMap.get(label);
                if (cell != null) {
                    cell.setTranslateX(offsetX);
                    cell.setTranslateY(offsetY);
                    cell.setWidth(wellWidth);
                    cell.setHeight(wellHeight);

                    String inventoryId = cell.getInventoryId();

                    StringBuffer labelBuf = new StringBuffer();
                    labelBuf.append(label);

                    if (!inventoryId.isEmpty()) {
                        labelBuf.append(": ").append(inventoryId);
                    }

                    if (!label.equals("A1")) {
                        Color fillColor = inventoryId.isEmpty()
                            ? Color.TRANSPARENT : DECODED_CELL_FILL_COLOR;
                        cell.setFill(fillColor);
                    }

                    Tooltip.install(cell, new Tooltip(labelBuf.toString()));
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

    private void updateWellDecodedIcons() {
        double imageWidth = wellDecodedImage.getWidth();
        double imageHeight = wellDecodedImage.getWidth();

        for (Entry<String, WellCell> entry : wellCellMap.entrySet()) {
            String label = entry.getKey();
            WellCell cell = entry.getValue();

            ImageView iv = wellDecodedIconMap.get(label);

            double halfWidth = cell.getWidth() / 2;
            double halfHeight = cell.getHeight() / 2;

            double fitWidth = halfWidth < imageWidth ? halfWidth : imageWidth;
            double fitHeight = halfHeight < imageHeight ? halfWidth : imageHeight;

            iv.setX(cell.getX() + cell.getTranslateX());
            iv.setY(cell.getY() + cell.getTranslateY() + cell.getHeight() - fitHeight);
            iv.setFitWidth(fitWidth);
            iv.setVisible(!cell.getInventoryId().isEmpty());
        }
    }

    public void plateTypeSelectionChanged(PlateType plateType) {
        this.plateType = plateType;
    }

    public Rectangle [] getWellCells() {
        return wellCellMap.values().toArray(new WellCell [] {});
    }

    public ImageView [] getWellDecodedIcons() {
        return wellDecodedIconMap.values().toArray(new ImageView [] {});
    }

    public void clearWellCellInventoryId() {
        for (WellCell cell : wellCellMap.values()) {
            cell.setInventoryId("");
        }
    }

    public void setWellCellInventoryId(String label, String inventoryId) {
        WellCell cell = wellCellMap.get(label);
        if (cell == null) {
            throw new IllegalArgumentException("label is invalid for grid: " + label);
        }
        cell.setInventoryId(inventoryId);
    }

    private String getLabelForGridPosition(int row, int col) {
        return getLabelForPosition(row,
                                   col,
                                   model.getPlateOrientation(),
                                   plateType,
                                   model.getBarcodePosition());
    }

    public Rectangle [] getResizeHandles() {
        return new Rectangle [] { resizeRectNW, resizeRectSE };
    }

    public void setScale(double scale) {
        displayScaleProperty.setValue(scale);
        update();
    }

    public double getScale() {
        return displayScaleProperty.getValue();
    }

    @Override
    public void setResizeCursor(Cursor value) {
        wellGridHandler.setCursor(value);
    }

    @Override
    public void mouseDragged(ResizeHandle resizeRect, double deltaX, double deltaY) {
        double displayScale = displayScaleProperty.getValue();
        double x = getX();
        double y = getY();
        double width = getWidth();
        double height = getHeight();
        double newWidth;
        double newHeight;

        if (resizeRect == resizeRectNW) {
            double adjustedDeltaX = deltaX / displayScale;
            double adjustedDeltaY = deltaY / displayScale;

            // NOTE: x and y are re-assigned
            x = Math.min(Math.max(0.0, x + adjustedDeltaX), x + width - ResizeHandle.RESIZE_RECT_SIZE);
            y = Math.min(Math.max(0.0, y + adjustedDeltaY), y + height - ResizeHandle.RESIZE_RECT_SIZE);

            newWidth = Math.min(
                Math.max(ResizeHandle.RESIZE_RECT_SIZE, width - adjustedDeltaX), x + width);
            newHeight = Math.min(
                Math.max(ResizeHandle.RESIZE_RECT_SIZE, height - adjustedDeltaY), y + height);

        } else if (resizeRect == resizeRectSE) {
            Image image = imageView.getImage();

            newWidth = Math.min(
                Math.max(ResizeHandle.RESIZE_RECT_SIZE, width + deltaX / displayScale),
                image.getWidth() - x);
            newHeight = Math.min(
                Math.max(ResizeHandle.RESIZE_RECT_SIZE, height + deltaY / displayScale),
                image.getHeight() - y);
        } else {
            throw new IllegalStateException(
                "invalid callback for resize: " + resizeRect);
        }

        resized(x, y, newWidth, newHeight);
    }

    private void resized(double x, double y, double width, double height) {
        if ((x < 0.0) || (y < 0.0) || (width < 0.0) || (height < 0.0)) {
            throw new IllegalArgumentException("invalid dimensions");
        }

        setX(x);
        setY(y);
        setWidth(width);
        setHeight(height);
        update();
    }

}
