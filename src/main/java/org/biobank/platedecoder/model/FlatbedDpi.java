package org.biobank.platedecoder.model;

/**
 * The valid DPIs to chose from in the application.
 *
 */
public enum FlatbedDpi {

    DPI_300(300),
    DPI_600(600);

    private final long value;

    private FlatbedDpi(long value) {
        this.value = value;
    }

    public long getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

}
