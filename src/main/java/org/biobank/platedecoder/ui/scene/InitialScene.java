package org.biobank.platedecoder.ui.scene;

import java.util.Optional;

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
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;

public class InitialScene extends AbstractSceneRoot {

    //@SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(InitialScene.class);

    private RadioButton filesystemButton;

	private RadioButton flatbedScanButton;

	private RadioButton withPrevParamsButton;

	private RadioButton modifyConfiguration;

    private Optional<EventHandler<ActionEvent>> withPrevParamsHandlerMaybe = Optional.empty();

	public InitialScene() {
        super("Choose an action");
    }

    @Override
    public void onDisplay() {
        unselectAll();
    }

    @Override
    protected Node creatContents() {
        filesystemButton = new RadioButton("Decode image - decode tubes in an image from the filesystem");
        flatbedScanButton = new RadioButton("Scan and decode - using the flatbed scanner");
        withPrevParamsButton = new RadioButton("Scan and decode with my previous settings");
        modifyConfiguration = new RadioButton("Modify my configuration");

        withPrevParamsButton.setOnAction(this::withPrevParamsAction);

        final ToggleGroup toggleGroup = new ToggleGroup();
        filesystemButton.setToggleGroup(toggleGroup);
        flatbedScanButton.setToggleGroup(toggleGroup);

        final GridPane grid = new GridPane();
        grid.setPadding(new Insets(20, 5, 5, 5));
        grid.setVgap(10);
        grid.setHgap(10);

        grid.add(filesystemButton, 0, 0);
        grid.add(flatbedScanButton, 0, 1);
        grid.add(withPrevParamsButton, 0, 2);
        grid.add(modifyConfiguration, 0, 3);
        return grid;
    }

    public void onFilesystemAction(EventHandler<ActionEvent> handler) {
        filesystemButton.setOnAction(handler);
    }

    public void onFlatbedScanAction(EventHandler<ActionEvent> handler) {
        flatbedScanButton.setOnAction(handler);
    }

    public void onFlatbedScanWithPreviousParamsAction(EventHandler<ActionEvent> handler) {
        withPrevParamsHandlerMaybe = Optional.of(handler);
    }

    public void modifyConfigrationAction(EventHandler<ActionEvent> handler) {
        modifyConfiguration.setOnAction(handler);
    }

    private void withPrevParamsAction(ActionEvent event) {
        Rectangle scanRect = PlateDecoderPreferences.getInstance().getWellRectangle(model.getPlateType());
        ScanAndDecodeImageTask worker =
            new ScanAndDecodeImageTask(scanRect,
                                       model.getFlatbedDpi().getValue(),
                                       model.getPlateOrientation(),
                                       model.getPlateType(),
                                       model.getBarcodePosition(),
                                       PlateDecoderDefaults.FLATBED_PLATE_IMAGE_NAME);

        ProgressDialog dlg = new ProgressDialog(worker);
        dlg.setTitle("Scanning and decoding image");
        dlg.setHeaderText("Scanning and decoding image");

        worker.setOnSucceeded(e -> {
                DecodeResult result = (DecodeResult) worker.getValue();

                if (result.getResultCode() == ScanLibResult.Result.SUCCESS) {
                    Plate plate = model.getPlate();
                    result.getDecodedWells().forEach(
                        well ->
                        plate.setWellInventoryId(well.getLabel(), well.getMessage()));
                }
                withPrevParamsHandlerMaybe.ifPresent(handler -> handler.handle(event));
            });

        worker.setOnFailed(e -> {
                LOG.error("The task failed: {}", e);
                withPrevParamsHandlerMaybe.ifPresent(handler -> handler.handle(event));
            });

        Thread th = new Thread(worker);
        th.setDaemon(true);
        th.start();
    }

    public void unselectAll() {
        filesystemButton.setSelected(false);
        flatbedScanButton.setSelected(false);
    }
};
