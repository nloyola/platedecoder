package org.biobank.platedecoder.model;

/**
 * Images of pallets saved to disk can be decoded, rather than being scanned with a flatbed scanner.
 * Also, the 2D barcodes can be present on the top or bottoms of the tubes. This enumeration is used
 * to define if the image was taken of the top or bottom of the pallet. The position of the camera
 * is important since it effects the ordering used to identify the tubes in the image.
 *
 * @author loyola
 *
 */
public enum BarcodePosition {
    TOP("Tube tops"),
    BOTTOM("Tube bottoms");

    public static final int size = values().length;

    private final String displayLabel;

    private BarcodePosition(String displayString) {
        this.displayLabel = displayString;
    }

    public String getDisplayLabel() {
        return displayLabel;
    }

}
