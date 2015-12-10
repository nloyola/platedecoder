package org.biobank.platedecoder.ui.wellgrid;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

/**
 * Displays the outline of a single cell in a well grid.
 *
 */
public class WellCell {

   // @SuppressWarnings("unused")
   private static final Logger LOG = LoggerFactory.getLogger(WellCell.class);

   private static final Color A1_CELL_FILL_COLOR = Color.rgb(189, 237, 255, 0.35);

   private static final Color DECODED_CELL_FILL_COLOR = Color.rgb(153, 198, 142, .25);

   private static final double ARC_WIDTH = 10;

   private static final double ARC_HEIGHT = 10;

   private static final double STROKE_WIDTH = 2;

   private static final Paint STROKE_COLOR = Color.GREEN;

   private static final Image DECODED_IMAGE =
      new Image(WellCell.class.getResourceAsStream("decoded.png"));

   private static final Image MANUALLY_DECODED_IMAGE =
      new Image(WellCell.class.getResourceAsStream("manuallyDecoded.png"));

   private final WellGridHandler wellGridHandler;

   private final Rectangle rect;

   private ImageView decodedImageView;

   private ImageView manuallyDecodedImageView;

   private final String label;

   private String inventoryId = "";

   private boolean manuallyDecoded;

   protected Optional<Point2D> mouseLocationMaybe = Optional.empty();

   /**
    * Displays the outline of a single cell in a well grid.
    *
    * @param wellGridHandler The object that is displaying the well grid.
    *
    * @param label The label associated with this cell in the grid.
    *
    * @param x the X position for this cell.
    *
    * @param y the Y position for this cell.
    *
    * @param width The width for this cell.
    *
    * @param height The height for this cell.
    */
   public WellCell(final WellGridHandler wellGridHandler,
                   String label,
                   double x,
                   double y,
                   double width,
                   double height) {
      this.label = label;
      this.wellGridHandler = wellGridHandler;
      this.manuallyDecoded = false;

      decodedImageView = createImageView(DECODED_IMAGE);
      manuallyDecodedImageView = createImageView(MANUALLY_DECODED_IMAGE);

      rect = new Rectangle(x, y, width, height);
      rect.setArcWidth(ARC_WIDTH);
      rect.setArcHeight(ARC_HEIGHT);
      rect.setFill(Color.TRANSPARENT);
      rect.setStroke(STROKE_COLOR);
      rect.setStrokeWidth(STROKE_WIDTH);

      rect.setOnMouseEntered(event -> {
            wellGridHandler.setCursor(Cursor.CLOSED_HAND);
         });

      rect.setOnMouseExited(event -> {
            wellGridHandler.setCursor(Cursor.DEFAULT);
         });

      rect.setOnMouseReleased(event -> {
            wellGridHandler.setCursor(Cursor.CLOSED_HAND);
            mouseLocationMaybe = Optional.empty();
         });

      rect.setOnMousePressed(event -> {
            wellGridHandler.setCursor(Cursor.MOVE);
            mouseLocationMaybe = Optional.of(new Point2D(event.getSceneX(), event.getSceneY()));
         });

      rect.setOnMouseDragged(this::mouseDragged);
      rect.setOnMouseClicked(this::doubleClick);

      if (label.equals("A1")) {
         rect.setFill(A1_CELL_FILL_COLOR);
      }

      Tooltip.install(rect, new Tooltip(label));
   }

   /**
    * The label associated with this cell.
    *
    * @return the cell's label.
    */
   public String getLabel() {
      return label;
   }

   /**
    * The label inventory ID associated with this cell. Decoded from the image of the 2D barcode in
    * the image this cell contains.
    *
    * @return the cell's inventory ID.
    */
   public String getInventoryId() {
      return inventoryId;
   }

   /**
    * Assigns the inventory ID corresponding to the message decoded from the image of the 2D barcode
    * contained in this cell.
    *
    * @param id the inventory ID.
    */
   public void setInventoryId(String id) {
      inventoryId = id;

      if (!label.equals("A1")) {
         Color fillColor = id.isEmpty()
            ? Color.TRANSPARENT : DECODED_CELL_FILL_COLOR;
         rect.setFill(fillColor);

         if (label.equals("A2")) {
            if (id.isEmpty()) {
               LOG.debug("setInventoryId: fill color transparent");
            } else {
               LOG.debug("setInventoryId: fill color");
            }
         }
      }

      StringBuffer labelBuf = new StringBuffer();
      labelBuf.append(label);

      if (!inventoryId.isEmpty()) {
         labelBuf.append(": ").append(inventoryId);
      }

      Tooltip.install(rect, new Tooltip(labelBuf.toString()));
   }

   /**
    * @return TRUE if the cell was manually entered by the user.
    */
   public boolean isManuallyDecoded() {
      return manuallyDecoded;
   }

   /**
    * @param value TRUE if the cell was manually entered by the user.
    */
   public void setManuallyDecoded(boolean value) {
      this.manuallyDecoded = value;
   }

   /**
    * @return A list of JavaFX widgets do be displayed by this cell.
    */
   public List<Node> getWidgets() {
      return Arrays.asList(rect, decodedImageView, manuallyDecodedImageView);
   }

   /**
    * Adjusts the position of this cell.
    *
    * <p>Usually called when the user resizes the window the grid is being displayed in.
    *
    * @param x the X position of the cell.
    *
    * @param y the Y position of the cell.
    */
   public void setPosition(double x, double y) {
      rect.setTranslateX(x);
      rect.setTranslateY(y);

      updateIcons();
   }

   /**
    * Adjusts the position and size of this cell.
    *
    * <p>Usually called when the user resizes the window the grid is being displayed in.
    *
    * @param x the X position of the cell.
    *
    * @param y the Y position of the cell.
    *
    * @param width The new width for this cell.
    *
    * @param height the new height for this cell.
    */
   public void setPositionAndSize(double x, double y, double width, double height) {
      rect.setTranslateX(x);
      rect.setTranslateY(y);
      rect.setWidth(width);
      rect.setHeight(height);

      updateIcons();
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof WellCell) {
         WellCell that = (WellCell) obj;
         return this.label.equals(that.label);
      }
      return false;
   }

   @Override
   public int hashCode() {
      return 17 + label.hashCode();
   }

   @Override
   public String toString() {
      StringBuffer buf = new StringBuffer();
      buf.append(label);
      buf.append(": [ inventoryId: \"");
      buf.append(inventoryId);
      buf.append("\",\n\t rect: [ x: ");
      buf.append(rect.getTranslateX());
      buf.append(", y: ");
      buf.append(rect.getTranslateY());
      buf.append(", width: ");
      buf.append(rect.getWidth());
      buf.append(", height: ");
      buf.append(rect.getHeight());
      buf.append(" ] ]");
      return buf.toString();
   }

   /**
    * Called when the user double clicks on a cell.
    */
   private void doubleClick(MouseEvent event) {
      if (event.getButton().equals(MouseButton.PRIMARY)
          && (event.getClickCount() == 2)) {
         wellGridHandler.manualDecode(this);
      }
   }

   /**
    * Called when the user drags a cell.
    */
   private void mouseDragged(MouseEvent event) {
      mouseLocationMaybe.ifPresent(mouseLocation -> {
            double deltaX = event.getSceneX() - mouseLocation.getX();
            double deltaY = event.getSceneY() - mouseLocation.getY();

            wellGridHandler.cellMoved(this, deltaX, deltaY);

            mouseLocationMaybe = Optional.of(new Point2D(event.getSceneX(), event.getSceneY()));
            event.consume();
         });
   }

   private ImageView createImageView(Image image) {
      ImageView iv = new ImageView(image);
      iv.setPreserveRatio(true);
      iv.setSmooth(true);
      iv.setVisible(false);
      return iv;
   }

   private void updateIcons() {
      double fitWidth = rect.getWidth() / 4;
      double fitHeight = rect.getHeight() / 4;

      ImageView [] imageViews = new ImageView [] { decodedImageView, manuallyDecodedImageView };
      for (ImageView iv : imageViews) {
         iv.setX(rect.getX() + rect.getTranslateX());
         iv.setY(rect.getY() + rect.getTranslateY() + rect.getHeight() - fitHeight);
         iv.setFitWidth(fitWidth);
      }

      decodedImageView.setVisible(!inventoryId.isEmpty() && !manuallyDecoded);
      manuallyDecodedImageView.setVisible(!inventoryId.isEmpty() && manuallyDecoded);
   }

}
