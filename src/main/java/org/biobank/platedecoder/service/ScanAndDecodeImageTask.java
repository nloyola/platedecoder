package org.biobank.platedecoder.service;

import static org.biobank.dmscanlib.ScanLib.ResultCode.SC_SUCCESS;

import java.util.Optional;
import java.util.Set;

import org.biobank.dmscanlib.CellRectangle;
import org.biobank.dmscanlib.DecodeOptions;
import org.biobank.dmscanlib.DecodeResult;
import org.biobank.dmscanlib.ScanLib;
import org.biobank.dmscanlib.ScanLibResult;
import org.biobank.platedecoder.model.BarcodePosition;
import org.biobank.platedecoder.model.PlateDecoderDefaults;
import org.biobank.platedecoder.model.PlateDecoderPreferences;
import org.biobank.platedecoder.model.PlateOrientation;
import org.biobank.platedecoder.model.PlateType;
import org.biobank.platedecoder.ui.PlateDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.concurrent.Task;
import javafx.scene.shape.Rectangle;

public class ScanAndDecodeImageTask extends Task<ScanLibResult> {

   // @SuppressWarnings("unused")
   private static final Logger LOG = LoggerFactory.getLogger(ScanAndDecodeImageTask.class);

   private final Rectangle scanRect;

   private final long dpi;

   private final String filename;

   private final PlateOrientation orientation;

   private final PlateType plateType;

   private final BarcodePosition barcodePosition;

   private final long brightness;

   private final long contrast;

   private final long decodeDebugLevel;

   private final DecodeOptions decodeOptions;

   public ScanAndDecodeImageTask(Rectangle        scanRect,
                                 long             dpi,
                                 PlateOrientation orientation,
                                 PlateType        plateType,
                                 BarcodePosition  barcodePosition,
                                 long             brightness,
                                 long             contrast,
                                 long             decodeDebugLevel,
                                 DecodeOptions    decodeOptions,
                                 String           filename) {
      this.scanRect         = scanRect;
      this.dpi              = dpi;
      this.orientation      = orientation;
      this.plateType        = plateType;
      this.brightness       = brightness;
      this.contrast         = contrast;
      this.barcodePosition  = barcodePosition;
      this.decodeDebugLevel = decodeDebugLevel;
      this.decodeOptions    = decodeOptions;
      this.filename         = filename;

      LOG.debug("decodeDebugLevel: {}", decodeDebugLevel);
      LOG.debug("decodeOptions: {}", decodeOptions);
   }

   @Override
   protected ScanLibResult call() throws Exception {
      ScanLibResult result = scanPlate();
      if (result.getResultCode() != ScanLibResult.Result.SUCCESS) {
         return new DecodeResult(result.getResultCode().getValue(),
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
      ScanLibResult result = new ScanLibResult(ScanLib.ResultCode.SC_FAIL, "exception");
      try {
         result = ScanLib.getInstance().scanImage(decodeDebugLevel,
                                                  PlateDecoder.getDeviceName(),
                                                  dpi,
                                                  (int) brightness,
                                                  (int) contrast,
                                                  r.getX(),
                                                  r.getY(),
                                                  r.getWidth(),
                                                  r.getHeight(),
                                                  filename);
      } catch (Exception ex) {
         LOG.error(ex.getMessage());
      }
      return result;
   }

   private ScanLibResult scanPlateLinux() throws InterruptedException {
      Thread.sleep(500);
      if (!PlateDecoder.fileExists(PlateDecoderDefaults.FLATBED_PLATE_IMAGE_NAME)) {
         throw new IllegalStateException(
            "file not present: " + PlateDecoderDefaults.FLATBED_PLATE_IMAGE_NAME);
      }
      return new ScanLibResult(SC_SUCCESS, "");
   }

   protected DecodeResult decode() {
      Set<CellRectangle> cells =
         CellRectangle.getCellsForBoundingBox(scanRect,
                                              orientation,
                                              plateType,
                                              barcodePosition);

      DecodeResult result =
         ScanLib.getInstance().decodeImage(decodeDebugLevel,
                                           filename,
                                           decodeOptions,
                                           cells.toArray(new CellRectangle[] {}));
      return result;
   }

}
