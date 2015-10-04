package org.biobank.platedecoder.dmscanlib;

import javafx.scene.shape.Rectangle;
import javafx.geometry.Bounds;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.biobank.platedecoder.model.SbsLabeling;
import org.biobank.platedecoder.model.PlateOrientation;
import org.biobank.platedecoder.model.PlateType;
import org.biobank.platedecoder.model.BarcodePosition;

/**
 * Defines rectangular coordinates, in pixels, for a region of image that contains a single 2D
 * barcode. The region also contains a label used to refer to it. This region of the image will then
 * be examined and if it contains a valid 2D barcode it will be decoded.
 *
 * @author Nelson Loyola
 *
 */
public final class CellRectangle implements Comparable<CellRectangle> {

    //@SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(CellRectangle.class);

    private final String label;

    private final Rectangle rectangle;

    public CellRectangle(String label, Rectangle rectangle) {
        this.label = label;
        this.rectangle = new Rectangle(rectangle.getX(),
                                       rectangle.getY(),
                                       rectangle.getWidth(),
                                       rectangle.getHeight());
    }

    public String getLabel() {
        return label;
    }

    public final double getX() {
        return rectangle.getX();
    }

    public final double getY() {
        return rectangle.getY();
    }

    public final double getWidth() {
        return rectangle.getWidth();
    }

    public final double getHeight() {
        return rectangle.getHeight();
    }

    public boolean containsPoint(double x, double y) {
        return rectangle.contains(x, y);
    }

    public Bounds getBoundsRectangle() {
        return rectangle.getBoundsInParent();
    }

    /**
     * Called by JNI.
     *
     * @param cornerId Corner one is where X and Y are minimum then the following corners go in a
     *            counter clockwise direction.
     * @return
     */
    public double getCornerX(int cornerId) {
        switch (cornerId) {
            case 0:
            case 1:
                return getX();
            case 2:
            case 3:
                return getX() + getWidth();
        }
        throw new IllegalArgumentException("invalid value for corner: " + cornerId);
    }

    /**
     * Called by JNI.
     *
     * @param cornerId Corner one is where X and Y are minimum then the following corners go in a
     *            counter clockwise direction.
     * @return
     */
    public double getCornerY(int cornerId) {
        switch (cornerId) {
            case 0:
            case 3:
                return getY();
            case 1:
            case 2:
                return getY() + getHeight();
        }
        throw new IllegalArgumentException("invalid value for corner: " + cornerId);
    }

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append(label);
        buf.append(": ");
        buf.append(rectangle.toString());
        return buf.toString();
    }

    /**
     * Generates each cell of the grid based on the parameters passed in.
     *
     * @param bbox The dimensions of the image the cells overlap onto (in pixels).
     * @param orientation The orientation of the pallet: either landscape or portrait.
     * @param plateType The dimensions of the pallet in terms of number of tubes it holds.
     * @param barcodePosition Where the barcodes are placed on the tubes: either the top or bottom.
     * @return The grid cells.
     * @note The units are in pixels.
     */
    public static Set<CellRectangle> getCellsForBoundingBox(Rectangle bbox,
                                                            PlateOrientation orientation,
                                                            PlateType plateType,
                                                            BarcodePosition barcodePosition) {

        int rows, cols;

        switch (orientation) {
        case LANDSCAPE:
            rows = plateType.getRows();
            cols = plateType.getCols();
            break;
        case PORTRAIT:
            rows = plateType.getCols();
            cols = plateType.getRows();
            break;
        default:
            throw new IllegalArgumentException("invalid orientation value: " + orientation);
        }

        double bboxX = bbox.getX();
        double xOffset = bbox.getX();
        double yOffset = bbox.getY();

        // make cells slightly smaller so that they all fit within the image
        double cellWidth  = bbox.getWidth() / cols;
        double cellHeight = bbox.getHeight() / rows;

        double xInset = 0.0001 * cellWidth;
        double yInset = 0.0001 * cellHeight;

        double cellWidthInset = cellWidth - 2 * xInset;
        double cellHeightInset = cellHeight - 2 * yInset;

        // LOG.debug("getCellsForBoundingBox: {}, {}, {}, {}",
        //           new Object [] {
        //               xInset, yInset,  cellWidthInset, cellHeightInset
        //           });

        Rectangle cellRect = new Rectangle(bbox.getX(), bbox.getY(), cellWidthInset, cellHeightInset);

        Set<CellRectangle> cells = new HashSet<CellRectangle>();
        for (int row = 0; row < rows; ++row) {
            for (int col = 0; col < cols; ++col) {
                String label = getLabelForPosition(row, col, orientation, plateType, barcodePosition);
                cellRect.setX(xOffset + xInset);
                cellRect.setY(yOffset + yInset);
                CellRectangle cell = new CellRectangle(label, cellRect);
                cells.add(cell);

                xOffset += cellWidth;
            }

            yOffset += cellHeight;
            xOffset = bboxX;
        }

        return cells;
    }

    @Override
    public int compareTo(CellRectangle that) {
        Pair<Integer, Integer> thisPos = SbsLabeling.toRowCol(this.label);
        Pair<Integer, Integer> thatPos = SbsLabeling.toRowCol(that.label);
        if (thisPos.getKey().equals(thatPos.getKey())) {
            return thisPos.getValue().compareTo(thatPos.getValue());
        }
        return thisPos.getKey().compareTo(thatPos.getKey());
    }

    public static String getLabelForPosition(int row,
                                             int col,
                                             PlateOrientation orientation,
                                             PlateType plateType,
                                             BarcodePosition barcodePosition) {
        switch (barcodePosition) {
        case TOP:
            switch (orientation) {
            case LANDSCAPE:
                return SbsLabeling.fromRowCol(row, col);
            case PORTRAIT:
                return SbsLabeling.fromRowCol(plateType.getRows() - 1 - col, row);

            default:
                throw new IllegalStateException("invalid value for orientation: " + orientation);
            }

        case BOTTOM:
            switch (orientation) {
            case LANDSCAPE:
                return SbsLabeling.fromRowCol(row, plateType.getCols() - 1 - col);
            case PORTRAIT:
                return SbsLabeling.fromRowCol(col, row);

            default:
                throw new IllegalStateException("invalid value for orientation: " + orientation);
            }

        default:
            throw new IllegalStateException("invalid value for barcode position: "
                + barcodePosition);
        }
    }

    public static List<CellRectangle> sortCells(Set<CellRectangle> cells) {
        List<CellRectangle> sorted = new ArrayList<CellRectangle>(cells);
        Collections.sort(sorted);
        return sorted;
    }

    public static void debugCells(Set<CellRectangle> cells) {
        for (CellRectangle cell : sortCells(cells)) {
            debugCell(cell);
        }
    }

    public static void debugCell(CellRectangle cell) {
        LOG.debug("cell: {}, {}, {}, {}, {} ",
                  new Object [] {
                      cell.getLabel(),
                      cell.getX(),
                      cell.getY(),
                      cell.getX() + cell.getWidth(),
                          cell.getY() + cell.getHeight()
                  });
    }

}
