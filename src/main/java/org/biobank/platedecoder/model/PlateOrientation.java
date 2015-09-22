package org.biobank.platedecoder.model;

public enum PlateOrientation {
    LANDSCAPE("Landscape", "Landscape"),
    PORTRAIT("Portrait", "Portrait");

    public static final int size = values().length;

    private final String id;
    private final String displayLabel;

    private PlateOrientation(String id, String displayLabel) {
        this.id = id;
        this.displayLabel = displayLabel;
    }

    public String getId() {
        return id;
    }

    public String getDisplayLabel() {
        return displayLabel;
    }

}
