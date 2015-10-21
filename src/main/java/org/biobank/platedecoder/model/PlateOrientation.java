package org.biobank.platedecoder.model;

public enum PlateOrientation {
    LANDSCAPE("Landscape"),
    PORTRAIT("Portrait");

    private final String displayLabel;

    private PlateOrientation(String displayLabel) {
        this.displayLabel = displayLabel;
    }

    public String getDisplayLabel() {
        return displayLabel;
    }

}
