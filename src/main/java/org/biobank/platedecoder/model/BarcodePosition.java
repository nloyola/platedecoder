package org.biobank.platedecoder.model;

/**
 * Where the barcodes are positioned on the tubes in an image of a plate.
 *
 * <p>The 2D barcodes can be present on the top or bottoms of the tubes. This enumeration is used to
 * define if the image was taken of the top or bottom of the pallet. The position of the camera is
 * important since it effects the ordering used to identify the tubes in the image.
 *
 * @author Nelson loyola
 */
public enum BarcodePosition {
    /** The barcodes are on the tops of the tubes. */
    TOP("Tube tops"),

    /** The barcodes are on the bottom of the tubes. */
    BOTTOM("Tube bottoms");

    private final String displayLabel;

    private BarcodePosition(String displayString) {
        this.displayLabel = displayString;
    }

    /**
     * A string representation of the value that can be used in the UI.
     *
     * @return a string representation of the value.
     */
    public String getDisplayLabel() {
        return displayLabel;
    }

}
