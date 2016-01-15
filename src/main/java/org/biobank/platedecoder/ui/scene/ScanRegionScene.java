package org.biobank.platedecoder.ui.scene;

import static org.biobank.platedecoder.ui.JavaFxHelper.createButton;
import static org.biobank.platedecoder.ui.JavaFxHelper.errorDialog;

import java.util.Optional;

import org.biobank.dmscanlib.ScanLibResult;
import org.biobank.platedecoder.model.FlatbedDpi;
import org.biobank.platedecoder.model.PlateDecoderDefaults;
import org.biobank.platedecoder.model.PlateDecoderPreferences;
import org.biobank.platedecoder.service.ScanRegionTask;
import org.biobank.platedecoder.ui.PlateDecoder;
import org.biobank.platedecoder.ui.ZoomingPane;
import org.biobank.platedecoder.ui.scanregion.ScanRegion;
import org.biobank.platedecoder.ui.scanregion.ScanRegionHandler;
import org.controlsfx.dialog.ProgressDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;

public class ScanRegionScene extends SceneRoot implements ScanRegionHandler {

   // @SuppressWarnings("unused")
   private static final Logger LOG = LoggerFactory.getLogger(ScanRegionScene.class);

   private static final String TITLE_AREA_MESSAGE =
      "Place a plate on your flatbed scanner and then press the scan button. "
      + "Align the rectangle so that it contains all the tubes on the plate. "
      + "Use the scroll wheel on the mouse to zoom into / out of the image. "
      + "Once aligned press the continue button.";

   private ImageView imageView;

   private Group imageGroup;

   private ScanRegion scanRegion;

   public ScanRegionScene() {
      super("Define scanning region");
      setTitleAreaMessage(TITLE_AREA_MESSAGE);
   }

   @Override
   public void onDisplay() {
      createScanRegion();
      disableNextButton(true);
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

   private Node createControlsPane() {
      Button scanButton = createButton("Scan", this::scanAction);
      scanButton.setMaxWidth(Double.MAX_VALUE);

      HBox hbox = new HBox(5);
      hbox.setPadding(new Insets(0, 20, 10, 20));
      hbox.getChildren().addAll(scanButton);

      GridPane grid = new GridPane();
      grid.setVgap(2);
      grid.setHgap(2);
      grid.add(hbox, 0, 0);

      return grid;
   }

   private Node createImagePane() {
      imageView = new ImageView();
      imageView.setPreserveRatio(true);
      imageView.setSmooth(true);

      imageGroup = new Group();
      imageGroup.getChildren().add(imageView);
      ZoomingPane imagePane = new ZoomingPane(imageGroup);

      imageView.fitWidthProperty().bind(imagePane.widthProperty());
      imageView.fitHeightProperty().bind(imagePane.heightProperty());

      imageView.fitWidthProperty().addListener((observable, oldValue, newValue) -> {
            // the actual dimensions are in imageView.getLayoutBounds().getWidth()
            Image image = imageView.getImage();
            if (image != null) {
               double newScale = imageView.getLayoutBounds().getWidth() / image.getWidth();
               scanRegion.setDisplayScale(newScale);
            }
         });

      imageView.fitHeightProperty().addListener((observable, oldValue, newValue) -> {
            // the actual dimensions are in imageView.getLayoutBounds().getHeight()
            Image image = imageView.getImage();
            if (image != null) {
               double newScale = imageView.getLayoutBounds().getHeight() / image.getHeight();
               scanRegion.setDisplayScale(newScale);
            }
         });

      imagePane.getZoomScaleProperty().addListener((observable, oldValue, newValue) -> {
            if (scanRegion != null) {
               scanRegion.setImageZoomScale(newValue.doubleValue());
            }
         });

      return imagePane;
   }

   private void createScanRegion() {
      Image image = imageView.getImage();

      if (image == null) return;

      Rectangle r;

      if (scanRegion == null) {
         long dpi = FlatbedDpi.valueOf(PlateDecoderDefaults.DEFAULT_FLATBED_DPI).getValue();
         Optional<Rectangle> rectMaybe = PlateDecoderPreferences.getInstance().getScanRegion();
         if (rectMaybe.isPresent()) {
            r = inchesToPixels(rectMaybe.get(), dpi);
         } else {
            r = inchesToPixels(PlateDecoderDefaults.getDefaultScanRegion(), dpi);
         }
      } else {
         r = scanRegion;
      }

      double scale = imageView.getLayoutBounds().getWidth() / image.getWidth();
      scanRegion = new ScanRegion(this,
                                  imageView,
                                  r.getX(),
                                  r.getY(),
                                  r.getWidth(),
                                  r.getHeight(),
                                  scale);
   }

   private void scanAction(@SuppressWarnings("unused") ActionEvent e) {
      ScanRegionTask worker = new ScanRegionTask(model.getFlatbedBrightness(),
                                                 model.getFlatbedContrast(),
                                                 model.getDecoderDebugLevel());

      ProgressDialog dlg = new ProgressDialog(worker);
      dlg.setTitle("Scanning flatbed");
      dlg.setHeaderText("Scanning flatbed");

      worker.setOnSucceeded(event -> {
            ScanLibResult result = worker.getValue();

            if (result.getResultCode() == ScanLibResult.Result.SUCCESS) {
               Image image = new Image(PlateDecoder.flatbedImageFilenameToUrl());
               imageView.setImage(image);
               imageView.setCache(true);

               createScanRegion();

               imageGroup.getChildren().clear();
               imageGroup.getChildren().add(imageView);
               imageGroup.getChildren().add(scanRegion.getDisplayRegion());
               imageGroup.getChildren().addAll(scanRegion.getResizeHandles());

               disableNextButton(false);
            }
         });

      worker.setOnFailed(event -> {
            LOG.error("The task failed: {}", event);
            errorDialog("Could not scan image", "Image scanning problem", null);
         });

      Thread th = new Thread(worker);
      th.setDaemon(true);
      th.start();
   }

   @Override
   protected void nextButtonAction() {
      long dpi = FlatbedDpi.valueOf(PlateDecoderDefaults.DEFAULT_FLATBED_DPI).getValue();
      Rectangle r = pixelsToInches(scanRegion, dpi);
      PlateDecoderPreferences.getInstance().setScanRegion(r);
      super.nextButtonAction();
   }

   private Rectangle pixelsToInches(Rectangle r, long dotsPerInch) {
      return new Rectangle(r.getX() / dotsPerInch,
                           r.getY() / dotsPerInch,
                           r.getWidth() / dotsPerInch,
                           r.getHeight() / dotsPerInch);
   }

   private Rectangle inchesToPixels(Rectangle r, long dotsPerInch) {
      return new Rectangle(r.getX() * dotsPerInch,
                           r.getY() * dotsPerInch,
                           r.getWidth() * dotsPerInch,
                           r.getHeight() * dotsPerInch);
   }
}
