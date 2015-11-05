package org.biobank.platedecoder.model;

/**
 * Stores information about the file that was selected by the user to decode 2D barcodes from.
 *
 */
public class ImageSourceFileSystem extends ImageSource {

   /**
    * Stores information about the file that was selected by the user to decode 2D barcodes from.
    *
    * @param imageFileUrl The URL, as a string, for file selected by the user.
    */
   public ImageSourceFileSystem(String imageFileUrl) {
      super(ImageSource.ImageSourceType.FILE_SYSTEM, imageFileUrl);
   }

}
