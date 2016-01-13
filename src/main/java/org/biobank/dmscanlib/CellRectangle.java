package org.biobank.dmscanlib;

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
 * Defines rectangular coordinates, in pixels, for a region of the plate image that contains a
 * single 2D barcode.
 *
 * <p>Associated with the region is a label that is used to reference it. This region of the image
 * is examined and if it contains a valid 2D barcode it is be decoded.
 *
 * @author Nelson Loyola
 *
 */
public final class CellRectangle implements Comparable<CellRectangle> {

   @SuppressWarnings("unused")
   private static final Logger LOG = LoggerFactory.getLogger(CellRectangle.class);

   private final String label;

   private final Rectangle rectangle;

   /**
    * A region of the image that contains a single 2D barcode.
    *
    * @param label the label to associate to this rectangle.
    *
    * @param rectangle the dimensions of this rectangle. The dimensions are in pixels that fit
    * within the image.
    */
   public CellRectangle(String label, Rectangle rectangle) {
      this.label = label;
      this.rectangle = new Rectangle(rectangle.getX(),
                                     rectangle.getY(),
                                     rectangle.getWidth(),
                                     rectangle.getHeight());
   }

   /**
    * The label associated to this rectangle.
    *
    * @return the label for this region of the image.
    */
   public String getLabel() {
      return label;
   }

   /**
    * The X coordinate of the top left corner of this rectangle.
    *
    * @return the X coordinate of the top left corner of this rectangle.
    */
   public double getX() {
      return rectangle.getX();
   }

   /**
    * The Y coordinate of the top left corner of this rectangle.
    *
    * @return the Y coordinate of the top left corner of this rectangle.
    */
   public double getY() {
      return rectangle.getY();
   }

   /**
    * The width of this rectangle.
    *
    * @return The width of this rectangle.
    */
   public double getWidth() {
      return rectangle.getWidth();
   }

   /**
    * The height of this rectangle.
    *
    * @return The height of this rectangle.
    */
   public double getHeight() {
      return rectangle.getHeight();
   }

   /**
    * Used to determine if a point is inside this rectangle.
    *
    * @param x the X coordinate of the point to be tested.
    *
    * @param y the Y coordinate of the point to be tested.
    *
    * @return {@code true} if the point is inside the rectangle.
    */
   public boolean containsPoint(double x, double y) {
      return rectangle.contains(x, y);
   }

   /**
    * The bounding box for this rectangle.
    *
    * <p> It may be that the rectangle is not aligned to the X and Y axis.
    *
    * @return the bounding box for this rectangle.
    */
   public Bounds getBoundsRectangle() {
      return rectangle.getBoundsInParent();
   }

   /**
    * Returns the X coordinate of the requested corner.
    *
    * <p><em>Called by JNI.</em>
    *
    * @param cornerId  Corner one is where X and Y are minimum then the following corners go in a
    *                  counter clockwise direction.
    *
    * @return The X coordinate for the requested corner.
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
    * Returns the Y coordinate of the requested corner.
    *
    * <p><em>Called by JNI.</em>
    *
    * @param cornerId Corner one is where X and Y are minimum then the following corners go in a
    *        counter clockwise direction.
    *
    * @return The Y coorinate for the requested corner.
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

   /**
    * Compares two cell rectangles.
    *
    * <p>Cell rectangles are ordered by the rows and colums derived from their labels.
    *
    * @param that  The other rectangle to compare against.
    */
   @Override
   public int compareTo(CellRectangle that) {
      Pair<Integer, Integer> thisPos = SbsLabeling.toRowCol(this.label);
      Pair<Integer, Integer> thatPos = SbsLabeling.toRowCol(that.label);
      if (thisPos.getKey().equals(thatPos.getKey())) {
         return thisPos.getValue().compareTo(thatPos.getValue());
      }
      return thisPos.getKey().compareTo(thatPos.getKey());
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
    * <p><em>The units are in pixels.</em>
    *
    * @param bbox The dimensions of the image the cells overlap onto (in pixels).
    *
    * @param orientation The orientation of the pallet: either landscape or portrait.
    *
    * @param plateType The dimensions of the pallet in terms of number of tubes it holds.
    *
    * @param barcodePosition Where the barcodes are placed on the tubes: either the top or bottom.
    *
    * @return The grid cells.
    *
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

   /**
    * Gets a label for a row and column based on the plate's settings.
    *
    * @param row  The row the position is at. The top row is row 0.
    *
    * @param col  The column the position is at. The leftmost column is 0.
    *
    * @param orientation  The plate's orientation.
    *
    * @param plateType  The dimensions of the plate in terms of number of wells.
    *
    * @param barcodePosition  The location of the 2D barcode on the tubes of the plate.
    *
    * @return The SBS label for the position.
    */
   public static String getLabelForPosition(int              row,
                                            int              col,
                                            PlateOrientation orientation,
                                            PlateType        plateType,
                                            BarcodePosition  barcodePosition) {
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

   /**
    * Sorts the set of cells by labels.
    *
    * @param cells the cells to be sorted.
    *
    * @return a new set with each cell sorted by label.
    */
   public static List<CellRectangle> sortCells(Set<CellRectangle> cells) {
      List<CellRectangle> sorted = new ArrayList<CellRectangle>(cells);
      Collections.sort(sorted);
      return sorted;
   }

}
