package org.biobank.platedecoder.ui.scene;

import java.util.Optional;

import org.biobank.platedecoder.dmscanlib.DecodeOptions;
import org.biobank.platedecoder.dmscanlib.DecodeResult;
import org.biobank.platedecoder.dmscanlib.ScanLibResult;
import org.biobank.platedecoder.model.Plate;
import org.biobank.platedecoder.model.PlateDecoderDefaults;
import org.biobank.platedecoder.model.PlateDecoderPreferences;
import org.biobank.platedecoder.service.ScanAndDecodeImageTask;
import org.controlsfx.dialog.ProgressDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;

public class InitialScene extends SceneRoot {

   // @SuppressWarnings("unused")
   private static final Logger LOG = LoggerFactory.getLogger(InitialScene.class);

   private RadioButton filesystemButton;

   private RadioButton flatbedScanButton;

   private RadioButton withPrevParamsButton;

   private RadioButton modifyFlatbedConfiguration;

   private RadioButton modifyDecodingConfiguration;

   private Optional<Runnable> withPrevParamsRunnableMaybe = Optional.empty();

   public InitialScene() {
      super("Choose an action");
   }

   @Override
   public void onDisplay() {
      unselectAll();
   }

   @Override
   protected Region createContents() {
      filesystemButton = new RadioButton("Decode image - decode tubes in an image from the filesystem");
      flatbedScanButton = new RadioButton("Scan and decode - using the flatbed scanner");
      withPrevParamsButton = new RadioButton("Scan and decode with my previous settings");
      modifyFlatbedConfiguration = new RadioButton("Modify flatbed scanner configuration");
      modifyDecodingConfiguration = new RadioButton("Modify 2D barcode decoding configuration");

      modifyFlatbedConfiguration.setPadding(new Insets(20, 0, 0, 0));

      withPrevParamsButton.setOnAction(this::withPrevParamsAction);

      final GridPane grid = new GridPane();
      grid.setPadding(new Insets(20, 5, 5, 5));
      grid.setVgap(10);
      grid.setHgap(10);

      grid.add(filesystemButton, 0, 0);
      grid.add(flatbedScanButton, 0, 1);
      grid.add(withPrevParamsButton, 0, 2);
      grid.add(modifyFlatbedConfiguration, 0, 3);
      grid.add(modifyDecodingConfiguration, 0, 4);
      grid.setAlignment(Pos.TOP_CENTER);
      return grid;
   }

   public void onFilesystemAction(Runnable runnable) {
      filesystemButton.setOnAction(e -> runnable.run());
   }

   public void onFlatbedScanAction(Runnable runnable) {
      flatbedScanButton.setOnAction(e -> runnable.run());
   }

   public void onFlatbedScanWithPreviousParamsAction(Runnable runnable) {
      withPrevParamsRunnableMaybe = Optional.of(runnable);
   }

   public void modifyFlatbedConfigAction(Runnable runnable) {
      modifyFlatbedConfiguration.setOnAction(e -> runnable.run());
   }

   public void modifyDecoderConfigAction(Runnable runnable) {
      modifyDecodingConfiguration.setOnAction(e -> runnable.run());
   }

   private void withPrevParamsAction(@SuppressWarnings("unused") ActionEvent event) {
      Rectangle scanRect = PlateDecoderPreferences.getInstance().getWellRectangle(model.getPlateType());

      DecodeOptions decodeOptions = new DecodeOptions(model.getMinEdgeFactor(),
                                                      model.getMaxEdgeFactor(),
                                                      model.getScanGapFactor(),
                                                      model.getEdgeThreshold(),
                                                      model.getSquareDeviation(),
                                                      model.getDecoderCorrections(),
                                                      DecodeOptions.DEFAULT_SHRINK);

      ScanAndDecodeImageTask worker =
         new ScanAndDecodeImageTask(scanRect,
                                    model.getFlatbedDpi().getValue(),
                                    model.getPlateOrientation(),
                                    model.getPlateType(),
                                    model.getBarcodePosition(),
                                    model.getFlatbedBrightness(),
                                    model.getFlatbedContrast(),
                                    model.getDecoderDebugLevel(),
                                    decodeOptions,
                                    PlateDecoderDefaults.FLATBED_PLATE_IMAGE_NAME);

      ProgressDialog dlg = new ProgressDialog(worker);
      dlg.setTitle("Scanning and decoding image");
      dlg.setHeaderText("Scanning and decoding image");

      worker.setOnSucceeded(e -> {
            DecodeResult result = (DecodeResult) worker.getValue();

            if (result.getResultCode() == ScanLibResult.Result.SUCCESS) {
               Plate plate = model.getPlate();
               result.getDecodedWells().forEach(
                  well -> plate.setWellInventoryId(well.getLabel(), well.getMessage()));
            }
            withPrevParamsRunnableMaybe.ifPresent(runnable -> runnable.run());
         });

      worker.setOnFailed(e -> {
            LOG.error("The task failed: {}", e);
            withPrevParamsRunnableMaybe.ifPresent(runnable -> runnable.run());
         });

      Thread th = new Thread(worker);
      th.setDaemon(true);
      th.start();
   }

   public void unselectAll() {
      filesystemButton.setSelected(false);
      flatbedScanButton.setSelected(false);
      withPrevParamsButton.setSelected(false);
      modifyFlatbedConfiguration.setSelected(false);
      modifyDecodingConfiguration.setSelected(false);
   }
};
