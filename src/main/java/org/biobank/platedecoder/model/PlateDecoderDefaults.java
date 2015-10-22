package org.biobank.platedecoder.model;

public class PlateDecoderDefaults {

    public static final String FLATBED_IMAGE_NAME = "flatbed.png";

    public static final String FLATBED_PLATE_IMAGE_NAME = "plate.png";

    public static final String DEFAULT_DRIVER_TYPE = DriverType.NONE.name();

    public static final double [] DEFAULT_SCAN_REGION_INCHES = new double [] {
        0.25, 0.25, 3.0, 2.0
    };

    public static final double [] DEFAULT_WELL_GRID = new double [] {
        0, 0, 1500, 1000
    };

    public static final double [] DEFAULT_APP_WINDOW_SIZE = new double [] {
        1000, 500
    };

    public static final String DEFAULT_PLATE_TYPE = PlateType.PT_96_WELLS.name();

    public static final String DEFAULT_PLATE_ORIENTATION = PlateOrientation.LANDSCAPE.name();

    public static final String DEFAULT_BARCODE_POSITION = BarcodePosition.BOTTOM.name();

    public static final double DEFAULT_SPECIMEN_LINK_DIVIDER_POSITION = 0.3;

    public static final String DEFAULT_FLATBED_DPI = FlatbedDpi.DPI_300.name();

}
