package org.biobank.platedecoder.ui.scene;

import static org.biobank.platedecoder.ui.JavaFxHelper.errorDialog;

import org.biobank.platedecoder.model.FlatbedDpi;
import org.biobank.platedecoder.model.PlateDecoderDefaults;
import org.biobank.platedecoder.model.PlateDecoderPreferences;
import org.biobank.platedecoder.service.ScanPlateTask;
import org.biobank.platedecoder.ui.FlatbedDpiChooser;
import org.controlsfx.dialog.ProgressDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;

public class ScanPlateScene extends SceneRoot {

   // @SuppressWarnings("unused")
   private static final Logger LOG = LoggerFactory.getLogger(ScanPlateScene.class);

   public ScanPlateScene() {
      super("Scanning options");
   }

   @Override
   protected Region createContents() {
      FlatbedDpiChooser dpiChooser = new FlatbedDpiChooser(model.getFlatbedDpiProperty());

      GridPane grid = new GridPane();
      grid.setPadding(new Insets(20, 5, 5, 5));
      grid.setVgap(10);
      grid.setHgap(10);
      grid.add(dpiChooser, 0, 0);
      grid.setAlignment(Pos.TOP_CENTER);

      Platform.runLater(() -> nextButtonRequestFocus());
      return grid;
   }

   @Override
   protected boolean allowNextButtonAction() {
      ScanPlateTask worker =
         new ScanPlateTask(model.getFlatbedDpi().getValue(),
                           model.getFlatbedBrightness(),
                           model.getFlatbedContrast(),
                           model.getDecoderDebugLevel(),
                           PlateDecoderDefaults.FLATBED_PLATE_IMAGE_NAME);
      ProgressDialog dlg = new ProgressDialog(worker);
      dlg.setTitle("Scanning plate");
      dlg.setHeaderText("Scanning plate");

      worker.setOnSucceeded(e -> {
            FlatbedDpi dpi = model.getFlatbedDpi();
            PlateDecoderPreferences.getInstance().setFlatbedDpi(dpi);
            performNextButtonAction();
         });

      worker.setOnFailed(e -> {
            LOG.error("The task failed: {}", e);
            errorDialog("Could not scan image", "Image scanning problem", null);
         });

      Thread th = new Thread(worker);
      th.setDaemon(true);
      th.start();

      // return false here so that the action is not take, it is taken if the worker succeeds
      return false;
   }

}
