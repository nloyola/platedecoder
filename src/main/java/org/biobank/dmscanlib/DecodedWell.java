package org.biobank.dmscanlib;

import org.biobank.platedecoder.model.SbsLabeling;

/**
 * Stores the value decoded from the image of a 2D barcode in a region of an image.
 *
 * <p>The region in the image is defined by a {@link CellRectangle} with a matching label.
 * The decoded 2D barcode message is returned by {@link #getMessage getMessage}.
 *
 * @author Nelson Loyola
 *
 */
public final class DecodedWell implements Comparable<DecodedWell> {

   private final String label;

   private final String message;

   /**
    * Stores the value decoded from the image of a 2D barcode in a region of an image.
    *
    * <p>Meant for the scanning library JNI. Object of this type are created by the scanning library
    * when it is decoding an image.
    *
    * @param label  The SBS label this object belongs to.
    *
    * @param message The decoded string extracted from the 2D barcode.
    */
   public DecodedWell(String label, String message) {
      this.label = label;
      this.message = message;
   }

   /**
    * Stores the value decoded from the image of a 2D barcode in a region of an image.
    *
    * <p>Meant for the scanning library JNI. Object of this type are created by the scanning library
    * when it decodes an image.
    *
    * @param row  corresponds to the {@link CellRectangle} with a matching row.
    *
    * @param col  corresponds to the {@link CellRectangle} with a matching column.
    *
    * @param message The decoded string extracted from the 2D barcode.
    *
    */
   public DecodedWell(int row, int col, String message) {
      this(SbsLabeling.fromRowCol(row, col), message);
   }

   /**
    * Corresponds to the {@link CellRectangle} with a matching label.
    *
    * @return The label for this well.
    */
   public String getLabel() {
      return label;
   }

   /**
    * Gets the contents decoded from the 2D barcode.
    *
    * @return the contents decoded from the 2D barcode.
    */
   public String getMessage() {
      if (message == null) {
         return "";
      }
      return message;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof DecodedWell) {
         DecodedWell that = (DecodedWell) obj;
         return this.label.equals(that.label);
      }
      return false;
   }

   @Override
   public int hashCode() {
      return 17 + label.hashCode();
   }

   /**
    * Used to sort decoded wells by label.
    */
   @Override
   public int compareTo(DecodedWell o) {
      return label.compareTo(o.label);
   }

   @Override
   public String toString() {
      StringBuffer sb = new StringBuffer();
      sb.append(label).append(": ").append(message);
      return sb.toString();
   }
}
