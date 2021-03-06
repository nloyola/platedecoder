package org.biobank.platedecoder.service;

import org.biobank.platedecoder.dmscanlib.DecodeOptions;
import org.biobank.platedecoder.dmscanlib.DecodeResult;
import org.biobank.platedecoder.model.BarcodePosition;
import org.biobank.platedecoder.model.PlateOrientation;
import org.biobank.platedecoder.model.PlateType;
import org.biobank.platedecoder.ui.wellgrid.WellGrid;

public class DecodeImageTask extends ScanAndDecodeImageTask {

   public DecodeImageTask(WellGrid         wellGrid,
                          long             dpi,
                          PlateOrientation orientation,
                          PlateType        plateType,
                          BarcodePosition  barcodePosition,
                          long             brightness,
                          long             contrast,
                          long             decodeDebugLevel,
                          DecodeOptions    decodeOptions,
                          String           filename) {

      super(wellGrid,
            dpi,
            orientation,
            plateType,
            barcodePosition,
            brightness,
            contrast,
            decodeDebugLevel,
            decodeOptions,
            filename);
   }

   @Override
   protected DecodeResult call() throws Exception {
      return decode();
   }

}
