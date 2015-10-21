package org.biobank.platedecoder.ui.scene;

import java.util.Optional;

import org.biobank.platedecoder.dmscanlib.ScanLib;
import org.biobank.platedecoder.dmscanlib.ScanLibResult;
import org.biobank.platedecoder.model.FlatbedDpi;
import org.biobank.platedecoder.model.PlateDecoderPreferences;
import org.biobank.platedecoder.ui.FlatbedDpiChooser;
import org.biobank.platedecoder.ui.PlateDecoder;
import org.controlsfx.dialog.ProgressDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;

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
        Task<ScanLibResult> worker = new Task<ScanLibResult>() {
                @Override
                protected ScanLibResult call() throws Exception {
                    if (PlateDecoder.IS_LINUX) {
                        return scanPlateLinux();
                    }
                    return scanPlateWindows();
                }
            };

        ProgressDialog dlg = new ProgressDialog(worker);
        dlg.setTitle("Scanning plate");
        dlg.setHeaderText("Scanning plate");

        worker.setOnSucceeded(event -> {
                LOG.error("scan copleted:");
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

    private ScanLibResult scanPlateWindows() {
        Optional<Rectangle> rectMaybe = PlateDecoderPreferences.getInstance().getScanRegion();

        if (!rectMaybe.isPresent()) {
            throw new IllegalStateException("scanning region not defined");
        }


        Rectangle r = rectMaybe.get();
        return ScanLib.getInstance().scanImage(0L,
                                               model.getFlatbedDpi().getValue(),
                                               0,
                                               0,
                                               r.getX(),
                                               r.getY(),
                                               r.getWidth(),
                                               r.getHeight(),
                                               PlateDecoder.flatbedPlateImageFilename());
    }

    private ScanLibResult scanPlateLinux() throws InterruptedException {
        LOG.error("dpi: {}", model.getFlatbedDpi().getValue());
        Thread.sleep(500);
        return new ScanLibResult(ScanLib.SC_SUCCESS, 0, "");
    }

    public void onScanCompleteAction(EventHandler<ActionEvent> scanCompleteHandler) {
        scanCompleteHandlerMaybe = Optional.of(scanCompleteHandler);
    }
}
