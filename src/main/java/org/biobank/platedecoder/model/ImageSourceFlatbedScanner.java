package org.biobank.platedecoder.model;

import org.biobank.platedecoder.ui.PlateDecoder;

/**
 * Stores information about the image that was scanned using the flatbed scanner.
 *
 */
public class ImageSourceFlatbedScanner extends ImageSource {

   /**
    * Specifies that the image being decoded was scanned with the flatbed scanner.
    *
    */
   private ImageSourceFlatbedScanner() {
      super(ImageSource.ImageSourceType.FLATBED_SCANNER,
            PlateDecoder.flatbedPlateImageFilenameToUrl());
   }

   public static ImageSourceFlatbedScanner getInstance() {
      return ImageSourceFlatbedScannerHolder.INSTANCE;
   }

   private static class ImageSourceFlatbedScannerHolder {
      private static final ImageSourceFlatbedScanner INSTANCE = new ImageSourceFlatbedScanner();
   }

}
