package org.biobank.platedecoder.model;

/**
 * Stores information about the image that was scanned using the flatbed scanner.
 *
 */
public class ImageSourceFlatbedScanner extends ImageSource {

   /**
    * Specifies that the image being decoded was scanned with the flatbed scanner.
    *
    */
   public ImageSourceFlatbedScanner() {
      super(ImageSource.ImageSourceType.FLATBED_SCANNER, null);
   }

}
