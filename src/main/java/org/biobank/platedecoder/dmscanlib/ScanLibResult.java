package org.biobank.platedecoder.dmscanlib;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.biobank.platedecoder.dmscanlib.ScanLib.ResultCode;

import static org.biobank.platedecoder.dmscanlib.ScanLib.ResultCode.*;

/**
 * A wrapper class for result values returned by the Scanning Library JNI API.
 *
 */
public class ScanLibResult {

   /**
    * An enumerated type that wraps values in {@link ResultCode ResultCode}.
    *
    */
   public enum Result {
      /** See {@link ResultCode#SC_SUCCESS}. */
      SUCCESS(SC_SUCCESS),

      /** See {@link ResultCode#SC_FAIL}. */
      FAIL(SC_FAIL),

      /** See {@link ResultCode#SC_TWAIN_UNAVAIL}. */
      TWAIN_UNAVAIL(SC_TWAIN_UNAVAIL),

      /** See {@link ResultCode#SC_INVALID_DPI}. */
      INVALID_DPI(SC_INVALID_DPI),

      /** See {@link ResultCode#SC_INVALID_NOTHING_DECODED}. */
      INVALID_NOTHING_DECODED(SC_INVALID_NOTHING_DECODED),

      /** See {@link ResultCode#SC_INVALID_IMAGE}. */
      INVALID_IMAGE(SC_INVALID_IMAGE),

      /** See {@link ResultCode#SC_INVALID_NOTHING_TO_DECODE}. */
      INVALID_NOTHING_TO_DECODE(SC_INVALID_NOTHING_TO_DECODE),

      /** See {@link ResultCode#SC_INCORRECT_DPI_SCANNED}. */
      INCORRECT_DPI_SCANNED(SC_INCORRECT_DPI_SCANNED);

      private final int value;

      private static final Map<Integer, Result> VALUES_MAP;

      static {
         Map<Integer, Result> map = new HashMap<Integer, Result>();

         for (Result resultEnum : values()) {
            Result check = map.get(resultEnum.getValue());
            if (check != null) {
               throw new IllegalStateException("permission enum value "
                                               + resultEnum.getValue() + " used multiple times");
            }

            map.put(resultEnum.getValue(), resultEnum);
         }

         VALUES_MAP = Collections.unmodifiableMap(map);
      }

      private Result(int value) {
         this.value = value;
      }

      /**
       * The value associated with this result code.
       *
       * @return the matching {@link Result}.
       */
      public int getValue() {
         return this.value;
      }

      /**
       * A map to convert from {@link ResultCode} to {@link Result}.
       *
       * @return the conversion map.
       */
      public static Map<Integer, Result> valuesMap() {
         return VALUES_MAP;
      }

      public static Result fromValue(Integer value) {
         return valuesMap().get(value);
      }

   }

   private int resultCode;

   private int value; // used by API call to return its value (if any)

   private String message;

   /**
    * Used to create a more meaningful result from the Scanning Library JNI API.
    *
    * <p>Meant for the scanning library JNI. Object of this type are created by the scanning library
    * when it is decoding an image.
    *
    * @param resultCode A value from {@link ResultCode ResultCode}.
    *
    * @param value This value is now deprecated.
    *
    * @param message A string representation of the error message.
    */
   public ScanLibResult(int resultCode, int value, String message) {
      this.resultCode = resultCode;
      this.setValue(value);
      this.message = message;
   }

   /**
    * The result code converted to the {@link Result Result} enumerated type.
    *
    * @return A value from {@link Result Result}.
    *
    */
   public Result getResultCode() {
      return Result.fromValue(resultCode);
   }

   /**
    * Used to assign the result code from a call to the Scanning Library JNI API.
    *
    * <p>Meant for the scanning library JNI. Object of this type are created by the scanning library
    * when it is decoding an image.
    *
    * @param resultCode a value from {@link ResultCode ResultCode}.
    */
   public void setResultCode(int resultCode) {
      this.resultCode = resultCode;
   }

   /**
    * The string representation of the result code from a call to the Scanning Library JNI API.
    *
    * @return  The string representation of the result code.
    */
   public String getMessage() {
      return message;
   }

   /**
    * Used to assign the string representation of the result code from a call to the Scanning
    * Library JNI API.
    *
    * <p>Meant for the scanning library JNI. Object of this type are created by the scanning library
    * when it is decoding an image.
    *
    * @param message The string representation of the result code.
    */
   public void setMessage(String message) {
      this.message = message;
   }

   /**
    * The associated value for the result code from a call to the Scanning Library JNI API.
    *
    * @return The associated value for the result code.
    */
   public int getValue() {
      return value;
   }

   /**
    * Used to assign an associated value for the result code from a call to the Scanning Library JNI API.
    *
    * <p>Meant for the scanning library JNI. Object of this type are created by the scanning library
    * when it is decoding an image.
    *
    * @param value  The associated value for the result code.
    */
   @Deprecated
   public void setValue(int value) {
      this.value = value;
   }

   @Override
   public String toString() {
      StringBuffer buf = new StringBuffer();
      buf.append("[ resultCode: ").append(getResultCode());
      buf.append(", value: ").append(value);
      buf.append(", message: ").append(message);
      buf.append(" ]");
      return buf.toString();
   }

}
