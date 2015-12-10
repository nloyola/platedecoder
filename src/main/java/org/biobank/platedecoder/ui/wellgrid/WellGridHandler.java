package org.biobank.platedecoder.ui.wellgrid;

import javafx.scene.Cursor;

/**
 * This interface is used by {@link WellGrid} to communicate with the object that is displaying it.
 *
 */
public interface WellGridHandler {

   /**
    * Called to update the mouse cursor. Usually called when the user hovers over a cell in the grid.
    *
    * @param value The new mouse cursor to be displayed.
    */
   public void setCursor(Cursor value);

   /**
    * Called when the user has clicked on a cell and dragged it.
    *
    * @param cell The cell the user is moving.
    *
    * @param deltaX The distance the user has moved the cell in the X direction.
    *
    * @param deltaY The distance the user has moved the cell in the Y direction.
    */
   public void cellMoved(WellCell cell, double deltaX, double deltaY);

   /**
    * This method is called when the user double clicks on a cell in the grid.
    *
    * @param cell the cell the user double clicked on.
    */
   public void manualDecode(WellCell cell);

   /**
    * Returns the width of the image that is displayed under the grid.
    *
    * <p>This is usually the image containing 2D barcodes.
    *
    * @return returns the width of the image containing all the barcodes.
    */
   public double getImageWidth();

   /**
    * Returns the height of the image that is displayed under the grid.
    *
    * <p>This is usually the image containing 2D barcodes.
    *
    * @return returns the height of the image containing all the barcodes.
    */
   public double getImageHeight();

}
