package org.biobank.platedecoder.ui.scene;

import java.util.Optional;

import org.biobank.platedecoder.model.FlatbedDpi;
import org.biobank.platedecoder.model.PlateDecoderDefaults;
import org.biobank.platedecoder.model.PlateDecoderPreferences;
import org.biobank.platedecoder.service.ScanPlateTask;
import org.biobank.platedecoder.ui.FlatbedDpiChooser;
import org.biobank.platedecoder.ui.PlateDecoder;
import org.controlsfx.dialog.ProgressDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

public class ScanPlateScene extends AbstractSceneRoot {

    //@SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(ScanPlateScene.class);

    private Optional<EventHandler<ActionEvent>> scanCompleteHandlerMaybe = Optional.empty();

    public ScanPlateScene() {
        super("Scan plate");
    }

    @Override
    public void onDisplay() {
        // do nothing
    }

    @Override
    protected Node creatContents() {
        FlatbedDpiChooser dpiChooser = new FlatbedDpiChooser();

        Button scanButton = createScanButton();

        GridPane grid = new GridPane();
        grid.setVgap(2);
        grid.setHgap(2);
        grid.add(dpiChooser, 0, 0);
        grid.add(scanButton, 0, 1);

        return grid;
    }

    private Button createScanButton() {
        Button button = new Button("Scan");
        button.setOnAction(this::scanPlate);
        button.setMaxWidth(Double.MAX_VALUE);
        return button;
    }

    private void scanPlate(ActionEvent e) {
        if (!checkFilePresentLinux()) {
            PlateDecoder.errorDialog(
                "Simulating the flatbed scan of a plate will not work. "
                + "To correct this, please copy an image to: "
                + PlateDecoder.flatbedPlateImageFilenameToUrl(),
                "Unable to simulate action",
                "File is missing.");
            return;
        }

        ScanPlateTask worker = new ScanPlateTask(PlateDecoderDefaults.FLATBED_IMAGE_DPI);
        ProgressDialog dlg = new ProgressDialog(worker);
        dlg.setTitle("Scanning plate");
        dlg.setHeaderText("Scanning plate");

        worker.setOnSucceeded(event -> {
            FlatbedDpi dpi = model.getFlatbedDpi();
            PlateDecoderPreferences.getInstance().setFlatbedDpi(dpi);
            scanCompleteHandlerMaybe.ifPresent(handler -> handler.handle(e));
        });

        worker.setOnFailed(event -> {
            LOG.error("The task failed: {}", event);
        });

        Thread th = new Thread(worker);
        th.setDaemon(true);
        th.start();
    }

    private boolean checkFilePresentLinux() {
        if (PlateDecoder.IS_LINUX) {
            return PlateDecoder.fileExists(PlateDecoder.flatbedPlateImageFilename());
        }
        return true;
    }

    public void onScanCompleteAction(EventHandler<ActionEvent> scanCompleteHandler) {
        scanCompleteHandlerMaybe = Optional.of(scanCompleteHandler);
    }
}
