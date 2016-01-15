package org.biobank.platedecoder.model;

import javafx.scene.shape.Rectangle;

/**
 * The default values used by the application.
 *
 */
public class PlateDecoderDefaults {

   /** The DPI used for scanning the entire flatbed. */
   public static final String DEFAULT_FLATBED_DPI = FlatbedDpi.DPI_75.name();

   /** The default DPI used when scanning a plate image with the flatbed scanner. */
   public static final String DEFAULT_FLATBED_IMAGE_DPI = FlatbedDpi.DPI_300.name();

   /** The default brightness used when scanning a plate image with the flatbed scanner. */
   public static final long DEFAULT_FLATBED_BRIGHTNESS = 0;

   /** The default contrast used when scanning a plate image with the flatbed scanner. */
   public static final long DEFAULT_FLATBED_CONTRAST = 0;

   public static final long DEFAULT_DECODER_DEBUG_LEVEL = 0;

   /** The name of the file scanned images are saved to. */
   public static final String FLATBED_IMAGE_NAME = "flatbed.png";

   /** The name of the file the flatbed region images are saved to. */
   public static final String FLATBED_PLATE_IMAGE_NAME = "plate.png";

   /** The default dimensions of the plate scanning region. */
   public static final double [] DEFAULT_SCAN_REGION_INCHES = new double [] {
      0.25, 0.25, 3.0, 2.0
   };

   /** The default size of the well grid used to delimit individual 2D barcodes. */
   public static final double [] DEFAULT_WELL_GRID = new double [] {
      0, 0, 1500, 1000
   };

   /** The default size for the application's window. */
   public static final double [] DEFAULT_APP_WINDOW_SIZE = new double [] {
      1000, 500
   };

   /** The default plate type. */
   public static final String DEFAULT_PLATE_TYPE = PlateType.PT_96_WELLS.name();

   /** The default plate orientation. */
   public static final String DEFAULT_PLATE_ORIENTATION = PlateOrientation.LANDSCAPE.name();

   /** The default setting for barcode position. */
   public static final String DEFAULT_BARCODE_POSITION = BarcodePosition.BOTTOM.name();

   /** The default value used for the divider in the specimen link scene. */
   public static final double DEFAULT_SPECIMEN_LINK_DIVIDER_POSITION = 0.3;

   /**
    * Returns the default scan region.
    *
    * <p>It is used as a starting point so that the user can modify it.
    *
    * @return a rectangle with the default dimensions if the scanning region in INCHES.
    */
   public static Rectangle getDefaultScanRegion() {
      return new Rectangle(DEFAULT_SCAN_REGION_INCHES[0],
                           DEFAULT_SCAN_REGION_INCHES[1],
                           DEFAULT_SCAN_REGION_INCHES[2],
                           DEFAULT_SCAN_REGION_INCHES[3]);
   }
}
