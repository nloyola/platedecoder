package org.biobank.platedecoder.dmscanlib;

public class ScanLib {

   public static class ResultCode {
      /** The call to ScanLib was successful. */
      public static final int SC_SUCCESS = 0;

      /** The call to ScanLib failed. */
      public static final int SC_FAIL = -1;

      /** The TWAIN driver is unavailable or not installed. */
      public static final int SC_TWAIN_UNAVAIL = -2;

      /** The TWAIN driver does not support the requested DPI. */
      public static final int SC_INVALID_DPI = -3;

      /** No datamatrix barcodes could be decoded from the image. */
      public static final int SC_INVALID_NOTHING_DECODED = -4;

      /** The scanned image is invalid. */
      public static final int SC_INVALID_IMAGE = -5;

      /** The cell regions were not defined or invalid. */
      public static final int SC_INVALID_NOTHING_TO_DECODE = -6;

      /** Incorrect DPI on scanned image. */
      public static final int SC_INCORRECT_DPI_SCANNED = -7;

   }

   public static class Capability {
      /** The TWAIN driver supports WIA. */
      public static final int CAP_IS_WIA = 0x01;

      /** The TWAIN driver supports a DPI of 300. */
      public static final int CAP_DPI_300 = 0x02;

      /** The TWAIN driver supports a DPI of 400. */
      public static final int CAP_DPI_400 = 0x04;

      /** The TWAIN driver supports a DPI of 600. */
      public static final int CAP_DPI_600 = 0x08;

      /** The scanner source has been selected. */
      public static final int CAP_IS_SCANNER = 0x10;
   }

   public static ScanLib getInstance() {
      return ScanLibHolder.INSTANCE;
   }

   private static class ScanLibHolder {
      private static final ScanLib INSTANCE = new ScanLib();
   }

   /**
    * Creates a dialog box to allow the user to select the scanner to use by default.
    *
    * @return SC_SUCCESS when selected by the user, and SC_INVALID_VALUE if the user cancelled the
    *         selection dialog.
    */
   public native ScanLibResult selectSourceAsDefault();

   /**
    * Queries the selected scanner for the driver type and supported dpi.
    *
    * <p>If the driver supports a DPI of 300, then use the value {@code 300} in this API to use it.
    * Similarly for DPI values of {@code 400} and {@code 600}.
    *
    * @return The bits in ScanLibResult.getValue() correspond to:
    *         <dl>
    *           <dt>Bit 1 (LSB)</dt>
    *           <dd>If set, driver type is WIA.</dd>
    *           <dt>Bit 2</dt>
    *           <dd>If set, driver supports 300 dpi.</dd>
    *           <dt>Bit 3</dt>
    *           <dd>If set, driver supports 400 dpi.</dd>
    *           <dt>Bit 4</dt>
    *           <dd>If set, driver supports 600 dpi.</dd>
    *           <dt>Bit 5</dt>
    *           <dd>if set, scanner source has been selected.</dd>
    *         </dl>
    */
   public native ScanLibResult getScannerCapability();

   /**
    * Uses the flatbed scanner to scan an image of the specified dimensions and save it to the specified
    * {@code filename}.
    *
    * <p>{@link #selectSourceAsDefault selectSourceAsDefault} must be called at least once before to
    * select the TWAIN driver.
    *
    * <p>The format of the image file is specified by the filename extensions. Valid values are:
    * {@code jpg, png, bmp, gif}, etc.
    *
    * @param verbose  The amount of debug logging information to generate. 1 is minimal and 9 is very
    *                 detailed. Using a value of 0 does not generate any logging information.
    *
    * @param dpi  The dots per inch for the image. Function {@link #getScannerCapability
    *             getScannerCapability} returns the valid values.
    *
    * @param brightness a value between -1000 and 1000. Only used when using the TWAIN driver.
    *
    * @param contrast a value between -1000 and 1000. Only used when using the TWAIN driver.
    *
    * @param x The left margin in inches.
    *
    * @param y The top margin in inches.
    *
    * @param width The width in inches.
    *
    * @param height The height in inches.
    *
    * @param filename The file name to save the image to. The filename extension specifies the
    *                 format of the image. If no path is specified then the image is saved to the
    *                 current working directory.
    *
    * @return {@link ResultCode#SC_SUCCESS SC_SUCCESS} if the image was created successfully. If a
    * different value is returned, the image could not be scanned and {@link ResultCode ResultCode}
    * should be checked for a reason.
    */
   public native ScanLibResult scanImage(long   verbose,
                                         long   dpi,
                                         int    brightness,
                                         int    contrast,
                                         double x,
                                         double y,
                                         double width,
                                         double height,
                                         String filename);

   /**
    * Uses the flatbed scanner to scan an image of the whole flatbed region and save it to the specified
    * {@code filename}.
    *
    * <p>{@link #selectSourceAsDefault selectSourceAsDefault} must be called at least once before to
    * select the TWAIN driver.
    *
    * @param verbose  The amount of debug logging information to generate. 1 is minimal and 9 is very
    *                 detailed. Using a value of 0 does not generate any logging information.
    *
    * @param dpi  The dots per inch for the image. Function {@link #getScannerCapability
    *             getScannerCapability} returns the valid values.
    *
    * @param brightness a value between -1000 and 1000. Only used when using the TWAIN driver.
    *
    * @param contrast a value between -1000 and 1000. Only used when using the TWAIN driver.
    *
    * @param filename The file name to save the image to. The filename extension specifies the
    *                 format of the image. If no path is specified then the image is saved to the
    *                 current working directory.
    *
    * @return {@link ResultCode#SC_SUCCESS SC_SUCCESS} if the image was created successfully. If a
    * different value is returned, the image could not be scanned and {@link ResultCode ResultCode}
    * should be checked for a reason.
    */
   public native ScanLibResult scanFlatbed(long   verbose,
                                           long   dpi,
                                           int    brightness,
                                           int    contrast,
                                           String filename);

   /**
    * Used to scan a region of the flatbed containing a plate with 2d barcodes and decodes
    * individual rectangles within the image.
    *
    * <p>{@link #selectSourceAsDefault selectSourceAsDefault} must be called at least once before to
    * select the TWAIN driver.
    *
    * @param verbose  The amount of debug logging information to generate. 1 is minimal and 9 is very
    *                 detailed. Using a value of 0 does not generate any logging information.
    *
    * @param dpi  The dots per inch for the image. Function {@link #getScannerCapability
    *             getScannerCapability} returns the valid values.
    *
    * @param brightness a value between -1000 and 1000. Only used when using the TWAIN driver.
    *
    * @param contrast a value between -1000 and 1000. Only used when using the TWAIN driver.
    *
    * @param x The left margin in inches.
    *
    * @param y The top margin in inches.
    *
    * @param width The width in inches.
    *
    * @param height The height in inches.
    *
    * @param decodeOptions See the constructor for {@link DecodeOptions} for a description of these
    *                      settings.
    *
    * @param wells An array of {@link CellRectangle} objects defining the the regions of the image
    *              containing 2D barcode tubes.
    *
    * @return The results of the decode in a {@link DecodeResult} object.
    */
   public native DecodeResult scanAndDecode(long            verbose,
                                            long            dpi,
                                            int             brightness,
                                            int             contrast,
                                            double          x,
                                            double          y,
                                            double          width,
                                            double          height,
                                            DecodeOptions   decodeOptions,
                                            CellRectangle[] wells);

   /**
    * Used to decode regions of an image containing 2D barcodes.
    *
    * <p>The regions of the image containing the barcodes are defined in {@code wells}.
    *
    * @param verbose  The amount of debug logging information to generate. 1 is minimal and 9 is very
    *                 detailed. Using a value of 0 does not generate any logging information.
    *
    * @param filename The filename containing an image with 2D barcodes. Valid image format values
    *                 are: {@code jpg, png, bmp, gif}, etc. If no path is specified then the image
    *                 is read from the current working directory.
    *
    * @param decodeOptions See the {@link DecodeOptions} for a description of these settings.
    *
    * @param wells An array of {@link CellRectangle} objects defining the the regions of the image
    *              containing 2D barcode tubes.
    *
    * @return The results of the decode in a {@link DecodeResult} object.
    */
   public native DecodeResult decodeImage(long            verbose,
                                          String          filename,
                                          DecodeOptions   decodeOptions,
                                          CellRectangle[] wells);

}
