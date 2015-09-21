package org.biobank.platedecoder.dmscanlib;

import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.util.Pair;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.biobank.platedecoder.model.SbsLabeling;
import org.biobank.platedecoder.model.PlateOrientation;
import org.biobank.platedecoder.model.PlateType;
import org.biobank.platedecoder.model.BarcodePosition;

/**
 * Defines rectangular coordinates, in inches, for a region of image that contains a single 2D
 * barcode. The region also contains a label used to refer to it. This region of the image will then
 * be examined and if it contains a valid 2D barcode it will be decoded.
 *
 * @author Nelson Loyola
 *
 */
public final class CellRectangle implements Comparable<CellRectangle> {

    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(CellRectangle.class);

    private final String label;

    private final Path polygon;

    private final Map<Integer, Point2D> points;

    public CellRectangle(String label, Rectangle rectangle) {
        this.label = label;
        this.polygon = rectToPoly(rectangle);
        this.points = rectToPoints(rectangle);
    }

    /*
     * Corner one is where X and Y are minimum then the following corners go in a counter clockwise
     * direction.
     */
    private Path rectToPoly(Rectangle rectangle) {
        Double maxX = rectangle.getX() + rectangle.getWidth();
        Double maxY = rectangle.getY() + rectangle.getHeight();

        Path polygon = new Path();
        polygon.getElements().add(new MoveTo(rectangle.getX(), rectangle.getY()));
        polygon.getElements().add(new LineTo(rectangle.getX(), maxY));
        polygon.getElements().add(new LineTo(maxX, maxY));
        polygon.getElements().add(new LineTo(maxX, rectangle.getY()));
        return polygon;
    }

    /*
     * Corner one is where X and Y are minimum then the following corners go in a counter clockwise
     * direction.
     */
    private Map<Integer, Point2D> rectToPoints(Rectangle rectangle) {
        Map<Integer, Point2D> result = new HashMap<Integer, Point2D>(4);

        Double maxX = rectangle.getX() + rectangle.getWidth();
        Double maxY = rectangle.getY() + rectangle.getHeight();

        result.put(0, new Point2D(rectangle.getX(), rectangle.getY()));
        result.put(1, new Point2D(rectangle.getX(), maxY));
        result.put(2, new Point2D(maxX, maxY));
        result.put(3, new Point2D(maxX, rectangle.getY()));
        return result;
    }

    public String getLabel() {
        return label;
    }

    public Path getPolygon() {
        return polygon;
    }

    public boolean containsPoint(double x, double y) {
        return polygon.contains(x, y);
    }

    public Rectangle getBoundsRectangle() {
        Bounds b = polygon.getBoundsInLocal();
        return new Rectangle(b.getMinX(), b.getMinY(), b.getWidth(), b.getHeight());
    }

    private Point2D getPoint(int pointId) {
        Point2D point = points.get(pointId);
        if (point == null) {
            throw new IllegalArgumentException("invalid value for corner: " + pointId);
        }
        return point;
    }

    /**
     *
     * @param cornerId Corner one is where X and Y are minimum then the following corners go in a
     *            counter clockwise direction.
     * @return
     */
    public double getCornerX(int cornerId) {
        return getPoint(cornerId).getX();
    }

    /**
     *
     * @param cornerId Corner one is where X and Y are minimum then the following corners go in a
     *            counter clockwise direction.
     * @return
     */
    public double getCornerY(int cornerId) {
        return getPoint(cornerId).getY();
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(label).append(" ");
        for (Entry<Integer, Point2D> entry : points.entrySet()) {
            sb.append(entry.getKey()).append(": ");
            sb.append("(").append(entry.getValue().getX()).append(", ");
            sb.append(entry.getValue().getY()).append("), ");
        }
        return sb.toString();
    }

    /**
     * Generates each cell of the grid based on the parameters passed in.
     *
     * @param bbox The dimensions of the image the cells overlap onto.
     * @param orientation The orientation of the pallet: either landscape or portrait.
     * @param plateType The dimensions of the pallet in terms of number of tubes it holds.
     * @param barcodePosition Where the barcodes are placed on the tubes: either the top or bottom.
     * @return The grid cells.
     * @note The units are in inches.
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
        double cellWidth = 0.9999 * Math.floor(bbox.getWidth()) / cols;
        double cellHeight = 0.9999 * Math.floor(bbox.getHeight()) / rows;

        Rectangle cellRect = new Rectangle(bbox.getX(), bbox.getY(), cellWidth, cellHeight);

        Set<CellRectangle> cells = new HashSet<CellRectangle>();
        for (int row = 0; row < rows; ++row) {
            for (int col = 0; col < cols; ++col) {
                String label = getLabelForPosition(row, col, orientation, plateType, barcodePosition);
                cellRect.setX(xOffset);
                cellRect.setY(yOffset);
                CellRectangle cell = new CellRectangle(label, cellRect);
                cells.add(cell);

                xOffset += cellWidth;
            }

            yOffset += cellHeight;
            xOffset = bboxX;
        }

        return cells;
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

    @Override
    public int compareTo(CellRectangle that) {
        Pair<Integer, Integer> thisPos = SbsLabeling.toRowCol(this.label);
        Pair<Integer, Integer> thatPos = SbsLabeling.toRowCol(that.label);
        if (thisPos.getKey().equals(thatPos.getKey())) {
            return thisPos.getValue().compareTo(thatPos.getValue());
        }
        return thisPos.getKey().compareTo(thatPos.getKey());
    }
}
