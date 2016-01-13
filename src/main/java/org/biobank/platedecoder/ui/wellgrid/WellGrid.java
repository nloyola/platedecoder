package org.biobank.platedecoder.ui.wellgrid;

import static org.biobank.dmscanlib.CellRectangle.getLabelForPosition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.biobank.platedecoder.model.PlateModel;
import org.biobank.platedecoder.model.PlateOrientation;
import org.biobank.platedecoder.model.PlateType;
import org.biobank.platedecoder.ui.resize.ResizeHandle;
import org.biobank.platedecoder.ui.resize.ResizeHandleNW;
import org.biobank.platedecoder.ui.resize.ResizeHandleSE;
import org.biobank.platedecoder.ui.resize.ResizeHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.shape.Rectangle;

/**
 * This class maintains the size of the grid and manages the rectangles that could contain an image
 * of a single 2D barcode.
 *
 * <p>The well grid can be resized and moved using the mouse.
 *
 */
public class WellGrid extends Rectangle implements ResizeHandler {

   /* For dragging and resizing see:
    *   http://stackoverflow.com/questions/26298873/resizable-and-movable-rectangle
    */

   @SuppressWarnings("unused")
   private static final Logger LOG = LoggerFactory.getLogger(WellGrid.class);

   private final PlateModel model = PlateModel.getInstance();

   private final WellGridHandler wellGridHandler;

   private Map<String, WellCell> wellCellMap = new HashMap<>();

   private DoubleProperty displayScaleProperty;

   private ResizeHandleNW resizeRectNW;

   private ResizeHandleSE resizeRectSE;

   /**
    * The well grid is superimposed on the image containinig the 2D barcodes. The image is scaled
    * to fit into the window displayed to the user. The {@code scale} is the scaling factor used to
    * display the image.
    *
    * @param wellGridHandler The object to inform of changes to the grid.
    *
    * @param x  The X coordinate of the top left corner of the grid.
    *
    * @param y  The Y coordinate of the top left corner of the grid.
    *
    * @param width  The width of the grid.
    *
    * @param height  The height of the grid.
    *
    * @param scale  The scale used to display the grid.
    */
   public WellGrid(final WellGridHandler wellGridHandler,
                   double    x,
                   double    y,
                   double    width,
                   double    height,
                   double    scale) {
      super(x, y, width, height);

      this.wellGridHandler = wellGridHandler;
      this.displayScaleProperty = new SimpleDoubleProperty(scale);

      createResizeHandles();
      createWellCells();
   }

   /**
    * Called when a user moves one of the cells that makes up the grid.
    *
    * @param cell The cell the user is moving.
    *
    * @param deltaX The distance the user has moved the cell in the X direction.
    *
    * @param deltaY The distance the user has moved the cell in the Y direction.
    */
   public void cellMoved(WellCell cell, double deltaX, double deltaY) {
      double dScale = displayScaleProperty.getValue();
      double adjustedDeltaX = deltaX / dScale;
      double adjustedDeltaY = deltaY / dScale;

      double newX = Math.min(Math.max(0.0, getX() + adjustedDeltaX),
                             wellGridHandler.getImageWidth() - getWidth());
      double newY = Math.min(Math.max(0.0, getY() + adjustedDeltaY),
                             wellGridHandler.getImageHeight() - getHeight());

      setX(newX);
      setY(newY);
      update();
   }

   /**
    * Used to update the grid after a user interface change.
    *
    * <p>A user interface change can be one of: user resized the window the grid is being displayed
    * in, the user changed the dimensions, orientation, or the position of the barcodes on the grid.
    */
   public void update() {
      double displayScale = displayScaleProperty.getValue();
      int rows, cols;

      PlateType plateType = model.getPlateType();
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
               cell.setPositionAndSize(offsetX, offsetY, wellWidth, wellHeight);
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

   /**
    * @return the count of the cells where the image has been successfully decoded or manually
    * decoded.
    */
   public int getDecodedCellCount() {
      int count = 0;
      for (WellCell cell : wellCellMap.values()) {
         if (!cell.getInventoryId().isEmpty()) {
            ++count;
         }
      }
      return count;
   }

   /**
    * @return the cells where the image has been successfully decoded or manually decoded.
    */
   public Set<WellCell> getDecodedCells() {
      return wellCellMap.values().stream()
         .filter(c -> !c.getInventoryId().isEmpty())
         .collect(Collectors.toSet());
   }

   /**
    * @return A list of JavaFX widgets do be displayed by this well grid.
    */
   public Node [] getWidgets() {
      List<Node> widgets = new ArrayList<>();
      wellCellMap.values().forEach(cell -> {
            widgets.addAll(cell.getWidgets());
         });
      widgets.add(resizeRectNW);
      widgets.add(resizeRectSE);
      return widgets.toArray(new Node [] {});
   }

   /**
    * Clears the inventory ID from all the cells in the grid.
    */
   public void clearWellCellInventoryId() {
      for (WellCell cell : wellCellMap.values()) {
         cell.setInventoryId("");
      }
   }

   /**
    * Assigns an inventory ID to a cell in the grid.
    *
    * @param label The label for the cell.
    *
    * @param inventoryId The inventory ID for the cell.
    *
    * @param manuallyDecoded True when the cell was decoded with a handheld scanner instead of the
    * decoding library.
    */
   public void setWellCellInventoryId(String  label,
                                      String  inventoryId,
                                      boolean manuallyDecoded) {
      WellCell cell = wellCellMap.get(label);
      if (cell == null) {
         throw new IllegalArgumentException("label is invalid for grid: " + label);
      }
      cell.setInventoryId(inventoryId);
      cell.setManuallyDecoded(manuallyDecoded);
   }

   /**
    * Assigns an inventory ID to a cell in the grid.
    *
    * <p>Used when the decoding library was used to decode the 2D barcode.
    *
    * @param label The label for the cell.
    *
    * @param inventoryId The inventory ID for the cell.
    *
    */
   public void setWellCellInventoryId(String label, String inventoryId) {
      setWellCellInventoryId(label, inventoryId, false);
   }

   /**
    * Returns the label for a cell in the grid.
    *
    * @param row The row the cell is in.
    *
    * @param col The column the cell is in.
    */
   private String getLabelForGridPosition(int row, int col) {
      return getLabelForPosition(row,
                                 col,
                                 model.getPlateOrientation(),
                                 model.getPlateType(),
                                 model.getBarcodePosition());
   }

   /**
    * @return The scale used to display the grid.
    */
   public double getScale() {
      return displayScaleProperty.getValue();
   }

   /**
    * The scale used when displaying the grid.
    *
    * <p>The scale is usually the same as the scale used on the background image.
    *
    * @param scale the scale used to display the grid.
    */
   public void setScale(double scale) {
      displayScaleProperty.setValue(scale);
      update();
   }

   @Override
   public void setResizeCursor(Cursor value) {
      wellGridHandler.setCursor(value);
   }

   @Override
   public void mouseDragged(ResizeHandle resizeHandle, double deltaX, double deltaY) {
      double displayScale = displayScaleProperty.getValue();
      double x = getX();
      double y = getY();
      double width = getWidth();
      double height = getHeight();
      double newWidth;
      double newHeight;

      if (resizeHandle == resizeRectNW) {
         double adjustedDeltaX = deltaX / displayScale;
         double adjustedDeltaY = deltaY / displayScale;

         // NOTE: x and y are re-assigned
         x = Math.min(Math.max(0.0, x + adjustedDeltaX), x + width - ResizeHandle.RESIZE_RECT_SIZE);
         y = Math.min(Math.max(0.0, y + adjustedDeltaY), y + height - ResizeHandle.RESIZE_RECT_SIZE);

         newWidth = Math.min(
            Math.max(ResizeHandle.RESIZE_RECT_SIZE, width - adjustedDeltaX), x + width);
         newHeight = Math.min(
            Math.max(ResizeHandle.RESIZE_RECT_SIZE, height - adjustedDeltaY), y + height);

      } else if (resizeHandle == resizeRectSE) {
         newWidth = Math.min(
            Math.max(ResizeHandle.RESIZE_RECT_SIZE, width + deltaX / displayScale),
            wellGridHandler.getImageWidth() - x);
         newHeight = Math.min(
            Math.max(ResizeHandle.RESIZE_RECT_SIZE, height + deltaY / displayScale),
            wellGridHandler.getImageHeight() - y);
      } else {
         throw new IllegalStateException(
            "invalid callback for resize: " + resizeHandle);
      }

      resized(x, y, newWidth, newHeight);
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

      PlateType plateType = model.getPlateType();
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

            cell.setPosition(offsetX, offsetY);

            wellCellMap.put(label, cell);
            offsetX += wellWidth;
         }

         offsetX = wellGridX;
         offsetY += wellHeight;
      }
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

   public boolean containsDecodedLabel(String label) {
      WellCell cell = wellCellMap.get(label);
      if (cell == null) {
         throw new IllegalArgumentException("label does not exist: " + label);
      }
      return !cell.getInventoryId().isEmpty() && !cell.isManuallyDecoded();
   }

}
