package org.biobank.platedecoder.model;

/**
 * The types of well plates this application supports.
 *
 * <p> If more well types are required, they can be added here.
 *
 * @author Nelson Loyola
 */
public enum PlateType {
    /** A 96 well plate with 8 rows and 12 columns. */
    PT_96_WELLS("96 wells (8x12)", 8, 12),

    /** A 81 well plate with 9 rows and 9 columns. */
    PT_81_WELLS("81 wells (9x9)", 9, 9),

    /** A 100 well plate with 10 rows and 10 columns. */
    PT_100_WELLS("100 wells (10x10)", 10, 10),

    /** A 144 well plate with 12 rows and 12 columns. */
    PT_144_WELLS("144 wells (12x12)", 12, 12);

    private final String description;
    private final int rows;
    private final int cols;

    private PlateType(String description, int rows, int cols) {
        this.description = description;
        this.rows = rows;
        this.cols = cols;
    }

    /**
     * A string description of this value.
     *
     * @return A string descption of the value.
     */
    public String getDescription() {
        return description;
    }

    /**
     * How many rows a plate of this type has.
     *
     * @return The number of rows for this value.
     */
    public int getRows() {
        return rows;
    }

    /**
     * How many columns a plate of this type has.
     *
     * @return The number of columns for this value.
     */
    public int getCols() {
        return cols;
    }

    @Override
    public String toString() {
        return description;
    }

}
