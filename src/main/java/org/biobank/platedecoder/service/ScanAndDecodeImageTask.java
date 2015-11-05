package org.biobank.platedecoder.service;

import java.util.Optional;
import java.util.Set;

import org.biobank.platedecoder.dmscanlib.CellRectangle;
import org.biobank.platedecoder.dmscanlib.DecodeOptions;
import org.biobank.platedecoder.dmscanlib.DecodeResult;
import org.biobank.platedecoder.dmscanlib.ScanLib;
import org.biobank.platedecoder.dmscanlib.ScanLibResult;
import org.biobank.platedecoder.model.BarcodePosition;
import org.biobank.platedecoder.model.PlateDecoderPreferences;
import org.biobank.platedecoder.model.PlateOrientation;
import org.biobank.platedecoder.model.PlateType;
import org.biobank.platedecoder.ui.PlateDecoder;

import javafx.concurrent.Task;
import javafx.scene.shape.Rectangle;

import static org.biobank.platedecoder.dmscanlib.ScanLib.ResultCode.*;

public class ScanAndDecodeImageTask extends Task<ScanLibResult> {

    private final Rectangle scanRect;

    private final long dpi;

    private final String filename;

    private PlateOrientation orientation;

    private PlateType plateType;

    private BarcodePosition barcodePosition;

    public ScanAndDecodeImageTask(Rectangle        scanRect,
                                  long             dpi,
                                  PlateOrientation orientation,
                                  PlateType        plateType,
                                  BarcodePosition  barcodePosition,
                                  String           filename) {
        this.scanRect        = scanRect;
        this.dpi             = dpi;
        this.orientation     = orientation;
        this.plateType       = plateType;
        this.barcodePosition = barcodePosition;
        this.filename        = filename;
    }

    @Override
    protected ScanLibResult call() throws Exception {
        ScanLibResult result = scanPlate();
        if (result.getResultCode() != ScanLibResult.Result.SUCCESS) {
            return new DecodeResult(result.getResultCode().getValue(),
                                    result.getValue(),
                                    result.getMessage());
        }
        return decode();
    }

    protected ScanLibResult scanPlate() throws InterruptedException {
        if (PlateDecoder.IS_LINUX) {
            return scanPlateLinux();
        }
        return scanPlateWindows();
    }

    private ScanLibResult scanPlateWindows() {
        Optional<Rectangle> rectMaybe = PlateDecoderPreferences.getInstance().getScanRegion();

        if (!rectMaybe.isPresent()) {
            throw new IllegalStateException("scanning region not defined");
        }

        Rectangle r = rectMaybe.get();
        return ScanLib.getInstance().scanImage(0L,
                                               dpi,
                                               0,
                                               0,
                                               r.getX(),
                                               r.getY(),
                                               r.getWidth(),
                                               r.getHeight(),
                                               filename);
    }

    private ScanLibResult scanPlateLinux() throws InterruptedException {
        Thread.sleep(500);
        if (!PlateDecoder.fileExists(PlateDecoder.flatbedPlateImageFilename())) {
            throw new IllegalStateException("file not present: "
                                            + PlateDecoder.flatbedPlateImageFilename());
        }
        return new ScanLibResult(SC_SUCCESS, 0, "");
    }

    protected DecodeResult decode() {
        Set<CellRectangle> cells = CellRectangle.getCellsForBoundingBox(
            scanRect,
            orientation,
            plateType,
            barcodePosition);

        DecodeResult result = ScanLib.getInstance().decodeImage(
            0L,
            filename,
            DecodeOptions.getDefaultDecodeOptions(),
            cells.toArray(new CellRectangle[] {}));
        return result;
    }

}
