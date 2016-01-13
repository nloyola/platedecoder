package org.biobank.dmscanlib;

/**
 * Used by the scanning library to return an integer value.
 *
 */
public class ValueResult extends ScanLibResult {

   private final int value;

   /**
    * Used by the scanning library to return an integer value.
    *
    * @param resultCode  See {@link org.biobank.dmscanlib.ScanLib.ResultCode ResultCode}
    *
    * @param message  The string representation of the resultCode.
    *
    * @param value  The value returned by the scanning library.
    */
   public ValueResult(int resultCode, String message, int value) {
      super(resultCode, message);
      this.value = value;
   }

   /**
    * @return the value
    */
   public int getValue() {
      return value;
   }
}
