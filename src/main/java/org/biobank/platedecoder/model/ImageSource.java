package org.biobank.platedecoder.model;

/**
 * Stores information about the image that 2D barcodes are decoded from.
 *
 */
public abstract class ImageSource {

   /**
    * Where the image comes from.
    *
    */
   protected enum ImageSourceType {

      /** Image comes from the file system. */
      FILE_SYSTEM("file system"),

      /** Image comes from a flatbed scanner. */
      FLATBED_SCANNER("flatbed scanner");

      private final String description;

      private ImageSourceType(String description) {
         this.description = description;
      }

      @Override
      public String toString() {
         return description;
      }
   }

   // TODO add timestamp

   private final ImageSourceType type;

   private final String imageFileUrl;

   /**
    * Stores information about the image that 2D barcodes are decoded from.
    *
    * @param type Where the image being decoded originated from.
    *
    * @param imageFileUrl the URL, as a string, for the file.
    */
   public ImageSource(ImageSourceType type, String imageFileUrl) {
      this.type = type;
      this.imageFileUrl = imageFileUrl;
   }

   /**
    * @return the type
    */
   public ImageSourceType getType() {
      return type;
   }

   /**
    * Where the image being decoded originated from.
    *
    * @return the URL for the file in string format.
    */
   public String getImageFileUrl() {
      return imageFileUrl;
   }

   @Override
   public String toString() {
      StringBuffer buf = new StringBuffer();
      buf.append(type);

      if (type == ImageSourceType.FILE_SYSTEM) {
         buf.append(": ");
         buf.append(imageFileUrl);
      }

      return buf.toString();
   }

}
