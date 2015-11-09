package org.biobank.platedecoder.ui.scene;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.biobank.platedecoder.dmscanlib.DecodeOptions;
import org.biobank.platedecoder.dmscanlib.DecodeResult;
import org.biobank.platedecoder.dmscanlib.DecodedWell;
import org.biobank.platedecoder.dmscanlib.ScanLibResult;
import org.biobank.platedecoder.model.ImageSource;
import org.biobank.platedecoder.model.Plate;
import org.biobank.platedecoder.model.PlateDecoderPreferences;
import org.biobank.platedecoder.service.DecodeImageTask;
import org.biobank.platedecoder.ui.BarcodePositionChooser;
import org.biobank.platedecoder.ui.ManualDecodeDialog;
import org.biobank.platedecoder.ui.PlateOrientationChooser;
import org.biobank.platedecoder.ui.PlateTypeChooser;
import org.biobank.platedecoder.ui.wellgrid.WellCell;
import org.biobank.platedecoder.ui.wellgrid.WellGrid;
import org.biobank.platedecoder.ui.wellgrid.WellGridHandler;
import org.controlsfx.dialog.ProgressDialog;
import org.controlsfx.tools.Borders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

// TODO: add a clear button, so manually entered cells can be cleared

// TODO: when the image is from a flatbed scan, the file name should say so. Maybe rename file name
// to "image source".

/**
 * Used for decoding the tubes present in an image.
 */
public class DecodeImageScene extends SceneRoot implements WellGridHandler {

   //@SuppressWarnings("unused")
   private static final Logger LOG = LoggerFactory.getLogger(DecodeImageScene.class);

   private ImageView imageView;

   private ImageSource imageSource;

   private Group imageGroup;

   private ScrollPane imagePane;

   private WellGrid wellGrid;

   private Label filenameLabel;

   private Optional<Set<DecodedWell>> decodedWellsMaybe = Optional.empty();

   public DecodeImageScene() {
      super("Align grid with barcodes");

      model.getPlateTypeProperty().addListener((observable, oldValue, newValue) -> {
            LOG.debug("plate type changed: {}", newValue);
            createWellGrid();
         });

      model.getPlateOrientationProperty().addListener((observable, oldValue, newValue) -> {
            createWellGrid();
         });

      model.getBarcodePositionProperty().addListener((observable, oldValue, newValue) -> {
            createWellGrid();
         });
   }

   @Override
   public void onDisplay() {
      // NOTE: could be called with model.getPlate() already populated with decode information

      Set<DecodedWell> decodedWells =
         model.getPlate().getWells().stream().filter(well -> !well.getInventoryId().isEmpty())
         .map(well -> new DecodedWell(well.getLabel(), well.getInventoryId()))
         .collect(Collectors.toSet());

      if (decodedWells.isEmpty()) {
         decodedWellsMaybe = Optional.empty();
      } else {
         decodedWellsMaybe = Optional.of(decodedWells);
      }

      createWellGrid();
      if (wellGrid != null) {
         updateWellGrid();
         wellGrid.update();
      }
      disableNextButton(decodedWells.isEmpty());
   }

   /**
    * Used to specify the source of the image.
    *
    * @param <T> a subclass of {@link ImageSource}.
    *
    * @param imageSource the image source object.
    */
   public <T extends ImageSource> void setImageSource(T imageSource) {
      this.imageSource = imageSource;
      Image image = new Image(imageSource.getImageFileUrl());
      imageView.setImage(image);
      imageView.setCache(true);
      createWellGrid();
   }

   /**
    * Creates a new well grid with the dimensions of the previous one.
    */
   private void createWellGrid() {
      Image image = imageView.getImage();

      if (image == null) return;

      Rectangle r;

      if (wellGrid == null) {
         r = PlateDecoderPreferences.getInstance().getWellRectangle(model.getPlateType());
      } else {
         r = wellGrid;
      }

      wellGrid = new WellGrid(this,
                              imageView,
                              model.getPlateType(),
                              r.getX(),
                              r.getY(),
                              r.getWidth(),
                              r.getHeight(),
                              imageView.getLayoutBounds().getWidth() / image.getWidth());

      wellGrid.setScale(imageView.getLayoutBounds().getWidth() / image.getWidth());

      imageGroup.getChildren().clear();
      imageGroup.getChildren().add(imageView);
      imageGroup.getChildren().addAll(wellGrid.getWellCells());
      imageGroup.getChildren().addAll(wellGrid.getWellDecodedIcons());
      imageGroup.getChildren().addAll(wellGrid.getResizeHandles());

      Set<DecodedWell> wells = decodedWellsMaybe.isPresent()
         ? decodedWellsMaybe.get() : Collections.emptySet();

      updateDecodedWellCount(wells);

      wellGrid.update();

      LOG.debug("decodedWells: {}", wells.size());
   }

   @Override
   protected Region createContents() {
      Node controls = createControlsPane();
      Node imagePane = createImagePane();

      BorderPane borderPane = new BorderPane();
      borderPane.setPadding(new Insets(5, 5, 5, 5));
      borderPane.setLeft(controls);
      borderPane.setCenter(imagePane);

      BorderPane.setMargin(controls, new Insets(5));

      return borderPane;
   }

   @Override
   protected boolean allowNextButtonAction() {
      if (!decodedWellsMaybe.isPresent()) {
         throw new IllegalStateException("missing decoded wells");
      }

      // copy data to model
      Plate plate = model.getPlate();
      decodedWellsMaybe.ifPresent(decodedWells -> {
            decodedWells.forEach(
               well ->
               plate.setWellInventoryId(well.getLabel(), well.getMessage()));
         });

      // save current dimensions of plate to preferences so they are used next time
      PlateDecoderPreferences.getInstance().setWellRectangle(
         model.getPlateType(), wellGrid);
      return true;
   }

   private Node createControlsPane() {
      PlateTypeChooser plateTypeChooser = new PlateTypeChooser();

      Button decodeButton = createDecodeButton();

      GridPane grid = new GridPane();
      grid.setVgap(2);
      grid.setHgap(2);
      grid.add(plateTypeChooser, 0, 0, 2, 1);
      grid.add(new PlateOrientationChooser(model.getPlateOrientationProperty()), 0, 1, 2, 1);
      grid.add(new BarcodePositionChooser(model.getBarcodePositionProperty()), 0, 2, 2, 1);
      grid.add(createInstructionsArea(), 0, 3, 2, 1);
      grid.add(decodeButton, 0, 4);

      return grid;
   }

   private Node createInstructionsArea() {
      StringBuffer buf = new StringBuffer();
      buf.append(
         "Align grid so that each cell contains a 2D barcode, and then press the Decode button.\n\n");
      buf.append(
         "If there are missed cells, double click one to decode it with a hand held scanner.\n\n");
      buf.append("Press the Next button once all cells are decoded.");

      Text text = new Text();
      text.setText(buf.toString());
      text.setWrappingWidth(200);

      return Borders.wrap(text)
         .etchedBorder().build()
         .build();
   }

   private Button createDecodeButton() {
      Button button = new Button("Decode");
      button.setOnAction(this::decodeImageAction);
      button.setMaxWidth(Double.MAX_VALUE);
      return button;
   }

   private Pane createImagePane() {
      imageView = new ImageView();
      imageView.setPreserveRatio(true);
      imageView.setSmooth(true);

      imageGroup = new Group();
      imageGroup.getChildren().add(imageView);
      imagePane = new ScrollPane(imageGroup);

      filenameLabel = new Label("Filename:");

      GridPane grid = new GridPane();
      grid.add(imagePane, 0, 0);
      grid.add(filenameLabel, 0, 1);

      // subtract a few pixels so that scroll bars are not displayed
      imageView.fitWidthProperty().bind(grid.widthProperty().subtract(5));
      imageView.fitHeightProperty().bind(grid.heightProperty()
                                         .subtract(filenameLabel.heightProperty()).subtract(5));

      imageView.fitWidthProperty().addListener((observable, oldValue, newValue) -> {
            // the actual dimensions are in imageView.getLayoutBounds().getWidth()
            Image image = imageView.getImage();
            if (image != null) {
               double newScale = imageView.getLayoutBounds().getWidth() / image.getWidth();
               wellGrid.setScale(newScale);
            }
         });

      imageView.fitHeightProperty().addListener((observable, oldValue, newValue) -> {
            // the actual dimensions are in imageView.getLayoutBounds().getHeight()
            Image image = imageView.getImage();
            if (image != null) {
               double newScale = imageView.getLayoutBounds().getHeight() / image.getHeight();
               wellGrid.setScale(newScale);
            }
         });

      return grid;
   }

   private String getFilenameFromImageSource() {
      try {
         URL imageUrl = new URL(imageSource.getImageFileUrl());
         File file = new File(imageUrl.toURI());
         return file.toString();
      } catch (URISyntaxException | MalformedURLException ex) {
         throw new IllegalStateException("could not convert iamge URL to a filename");
      }
   }

   private void decodeImageAction(@SuppressWarnings("unused") ActionEvent e) {
      DecodeOptions decodeOptions = new DecodeOptions(model.getMinEdgeFactor(),
                                                      model.getMaxEdgeFactor(),
                                                      model.getScanGapFactor(),
                                                      model.getEdgeThreshold(),
                                                      model.getSquareDeviation(),
                                                      model.getDecoderCorrections(),
                                                      DecodeOptions.DEFAULT_SHRINK);
      DecodeImageTask worker =
         new DecodeImageTask(wellGrid,
                             model.getFlatbedDpi().getValue(),
                             model.getPlateOrientation(),
                             model.getPlateType(),
                             model.getBarcodePosition(),
                             model.getDecoderDebugLevel(),
                             decodeOptions,
                             getFilenameFromImageSource());
      ProgressDialog dlg = new ProgressDialog(worker);
      dlg.setTitle("Decoding image");
      dlg.setHeaderText("Decoding image");

      worker.setOnSucceeded(event -> {
            DecodeResult result = (DecodeResult) worker.getValue();

            if (result.getResultCode() == ScanLibResult.Result.SUCCESS) {
               if (decodedWellsMaybe.isPresent()) {
                  Set<DecodedWell> prevDecodedWells = decodedWellsMaybe.get();
                  Set<DecodedWell> currentDecodedWells = result.getDecodedWells();

                  // compare this new result with the previous one
                  if (DecodeResult.compareDecodeResults(prevDecodedWells,
                                                        currentDecodedWells)) {
                     // merge the two results
                     prevDecodedWells.addAll(currentDecodedWells);
                     updateDecodedWellCount(prevDecodedWells);
                     decodedWellsMaybe = Optional.of(prevDecodedWells);

                  } else {
                     if (decodeMismatchErrorDialog()) {
                        decodedWellsMaybe = Optional.of(currentDecodedWells);
                        updateDecodedWellCount(currentDecodedWells);
                        wellGrid.clearWellCellInventoryId();
                     }
                  }
               } else {
                  Set<DecodedWell> decodedWells = result.getDecodedWells();
                  decodedWellsMaybe = Optional.of(decodedWells);
                  updateDecodedWellCount(decodedWells);
               }

               updateWellGrid();
               disableNextButton(false);
            } else {
               LOG.error("decode failed: {}", result.getResultCode());
            }
         });

      worker.setOnFailed(event -> {
            LOG.error("The task failed: {}", event);
         });

      Thread th = new Thread(worker);
      th.setDaemon(true);
      th.start();
   }

   private void updateWellGrid() {
      decodedWellsMaybe.ifPresent(decodedWells -> {
            decodedWells.forEach(
               well ->
               wellGrid.setWellCellInventoryId(well.getLabel(), well.getMessage()));
            wellGrid.update();
         });
   }

   private void updateDecodedWellCount(Set<DecodedWell> decodedWells) {
      StringBuffer buf = new StringBuffer();
      buf.append(imageSource);
      if (!decodedWells.isEmpty()) {
         buf.append(", tubes decoded: ");
         buf.append(decodedWells.size());
      }
      filenameLabel.setText(buf.toString());
   }

   /**
    *
    * See http://code.makery.ch/blog/javafx-dialogs-official/ for dialog examples.
    *
    * @return TRUE if these results should be used instead. FALSE if they should be discarded.
    */
   private boolean decodeMismatchErrorDialog() {
      ButtonType buttonTypeUseResult = new ButtonType("Use this result instead");
      ButtonType buttonTypeDiscardResult = new ButtonType("Discard this result");

      // display error message to user
      Alert dlg = new Alert(AlertType.CONFIRMATION);
      dlg.setTitle("Decode Mismatch");
      dlg.getDialogPane().setHeaderText("The results from this decode do not match the previous results.");
      dlg.getDialogPane().setContentText("What would you like to do?");
      dlg.getButtonTypes().setAll(buttonTypeUseResult, buttonTypeDiscardResult);

      Optional<ButtonType> result = dlg.showAndWait();

      return (result.get() == buttonTypeUseResult);
   }

   @Override
   public void cellMoved(WellCell cell, double deltaX, double deltaY) {
      if (wellGrid == null) {
         throw new IllegalStateException("well grid is null");
      }
      wellGrid.cellMoved(cell, deltaX, deltaY);
   }

   @Override
   public void manualDecode(WellCell cell) {
      if (!cell.getInventoryId().isEmpty()) {
         // exit if this cell already has an invetory ID
         return;
      }

      decodedWellsMaybe.ifPresent(wells -> {
            String label = cell.getLabel();
            Optional<String> inventoryIdMaybe = getManualDecodeInventoryId(label);

            inventoryIdMaybe.ifPresent(inventoryId -> {
                  DecodedWell newWell = new DecodedWell(label, inventoryId);
                  wells.add(newWell);

                  wellGrid.setWellCellInventoryId(cell.getLabel(), inventoryId);
                  wellGrid.update();
                  updateDecodedWellCount(wells);
               });
         });
   }

   private Optional<String> getManualDecodeInventoryId(String label) {
      Set<String> deniedInventoryIds = decodedWellsMaybe.get()
         .stream()
         .map(well -> well.getMessage())
         .collect(Collectors.toSet());
      ManualDecodeDialog dlg = new ManualDecodeDialog(label, deniedInventoryIds);
      return dlg.showAndWait();
   }
}
