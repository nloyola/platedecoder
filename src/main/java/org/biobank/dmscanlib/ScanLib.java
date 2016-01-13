package org.biobank.dmscanlib;

public class ScanLib {

   public static final int MAX_BRIGHTNESS = 1000;

   public static final int MIN_BRIGHTNESS = -1000;

   public static final int MAX_CONTRAST = 1000;

   public static final int MIN_CONTRAST = -1000;

   private static final boolean IS_MS_WINDOWS = System.getProperty("os.name").startsWith("Windows");

   private static final boolean IS_LINUX = System.getProperty("os.name").startsWith("Linux");

   private static final boolean IS_ARCH_64_BIT = System.getProperty("os.arch").equals("amd64");

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

      /** An invalid device was specified. */
      public static final int SC_INVALID_DEVICE            = -8;

      /** An invalid brightness value was requested. */
      public static final int SC_INVALID_BRIGHTNESS        = -9;

      /** An invalid contrast value was requested. */
      public static final int SC_INVALID_CONTRAST          = -10;
   }

   public static class Capability {
      /** The TWAIN driver supports WIA. */
      public static final int CAP_IS_WIA = 0x01;

      /** The scanner source has been selected. */
      public static final int CAP_IS_SCANNER = 0x02;
   }

   static {
      if (IS_MS_WINDOWS) {
         System.loadLibrary("dmscanlib");
      } else if (IS_LINUX && IS_ARCH_64_BIT) {
         System.loadLibrary("dmscanlib64");
      }
   }

   /**
    * Returns true if running on MS Windows.
    *
    * @return {@code true} if the operating system is running MS Windows.
    */
   public static boolean runningMsWindows() {
      return IS_MS_WINDOWS;
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
    * Returns the names of all the flatbed scanners available on this computer.
    *
    * <p>This method returns valid results under Linux only.
    *
    * @return The list of device names.
    */
   public native DeviceNamesResult getDeviceNames();

   /**
    * Returns the set of DPIs that can be used with this scanning device.
    *
    * <p>Should be called after a scanning device has been selected.
    *
    * @param deviceName the device the user wishes to retrieve the DPIs for.
    *
    * @return A list of valid DPIs.
    */
   public native ValidDpisResult getValidDpis(String deviceName);

   /**
    * Queries the selected scanner for the driver type and if the source has been selected.
    *
    * @return The bits in ValueResult.getValue() correspond to:
    *         <dl>
    *           <dt>Bit 1 (LSB)</dt>
    *           <dd>If set, driver type is WIA.</dd>
    *           <dt>Bit 2</dt>
    *           <dd>if set, scanner source has been selected.</dd>
    *         </dl>
    */
   public native ValueResult getScannerCapability();

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
    * @parma deviceName The device name associated with the flatbed scanner. This value can be an
    *                   empty string when running on MS Windows.
    *
    * @param dpi  The dots per inch for the image. Function {@link #getScannerCapability
    *             getScannerCapability} returns the valid values.
    *
    * @param brightness a value between -1000 and 1000.
    *
    * @param contrast a value between -1000 and 1000.
    *
    * @param left The left coordinate in inches.
    *
    * @param top The top coordinate in inches.
    *
    * @param right The right coordinate in inches.
    *
    * @param bottom The bottom coordinate in inches.
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
                                         String deviceName,
                                         long   dpi,
                                         int    brightness,
                                         int    contrast,
                                         double left,
                                         double top,
                                         double right,
                                         double bottom,
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
    * @parma deviceName The device name associated with the flatbed scanner. This value can be an
    *                   empty string when running on MS Windows.
    *
    * @param dpi  The dots per inch for the image. Function {@link #getScannerCapability
    *             getScannerCapability} returns the valid values.
    *
    * @param brightness a value between -1000 and 1000.
    *
    * @param contrast a value between -1000 and 1000.
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
                                           String deviceName,
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
    * @parma deviceName The device name associated with the flatbed scanner. This value can be an
    *                   empty string when running on MS Windows.
    *
    * @param dpi  The dots per inch for the image. Function {@link #getScannerCapability
    *             getScannerCapability} returns the valid values.
    *
    * @param brightness a value between -1000 and 1000.
    *
    * @param contrast a value between -1000 and 1000.
    *
    * @param left The left coordinate in inches.
    *
    * @param top The top coordinate in inches.
    *
    * @param right The right coordinate in inches.
    *
    * @param bottom The bottom coordinate in inches.
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
                                            String          deviceName,
                                            long            dpi,
                                            int             brightness,
                                            int             contrast,
                                            double          left,
                                            double          top,
                                            double          right,
                                            double          bottom,
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
