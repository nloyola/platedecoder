package org.biobank.platedecoder.model;

/**
 * The types of well plates this app supports.
 */
public enum PlateType {
    PT_96_WELLS("96 wells (8x12)", 8, 12),
    PT_81_WELLS("81 wells (9x9)", 9, 9),
    PT_100_WELLS("100 wells (10x10)", 10, 10),
    PT_144_WELLS("144 wells (12x12)", 12, 12);

    private final String description;
    private final int rows;
    private final int cols;

    private PlateType(String description, int rows, int cols) {
        this.description = description;
        this.rows = rows;
        this.cols = cols;
    }

    public String getDescription() {
        return description;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    @Override
    public String toString() {
        return description;
    }

}
