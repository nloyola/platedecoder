package org.biobank.platedecoder.model;

/**
 * The plate orientation is used to describe the orientation of the plate in the image.
 *
 * <p>The plate orientation and the position of the barcodes, {@link BarcodePosition} are used to
 * determine the ordering of the tubes in the image.
 *
 * @author Nelson loyola
 */
public enum PlateOrientation {
    /** The image contains a plate in landscape orientation. */
    LANDSCAPE("Landscape"),

    /** The image contains a plate in portrait orientation. */
    PORTRAIT("Portrait");

    private final String displayLabel;

    private PlateOrientation(String displayLabel) {
        this.displayLabel = displayLabel;
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
