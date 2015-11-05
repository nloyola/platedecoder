package org.biobank.platedecoder.model;

import javafx.util.Pair;

public class SbsLabeling {

    public static final String SBS_ROW_LABELLING_PATTERN = "ABCDEFGHIJKLMNOP";

    public static final int SBS_MAX_COLS = 24;

    public static final int ROW_DEFAULT = 8;
    public static final int COL_DEFAULT = 12;

    public static String fromRowCol(int row, int col) {
        if (row >= SBS_ROW_LABELLING_PATTERN.length()) {
            throw new IllegalArgumentException("invalid row size for position: " + row);
        }
        if (col >= SBS_MAX_COLS) {
            throw new IllegalArgumentException("invalid column size for position: " + row);
        }
        StringBuffer sb = new StringBuffer();
        sb.append(SBS_ROW_LABELLING_PATTERN.charAt(row));
        sb.append(col + 1);
        return sb.toString();

    }

    /**
     * Get the row and column position corresponding to the given SBS standard 2 or 3 char string
     * position. Eg. could be A2 or F12.
     *
     * @param label  The label to be converted to row and column.
     *
     * @return a pair containing the row and column corresponding to the label.
     */
    public static Pair<Integer, Integer> toRowCol(String label) {
        if ((label.length() < 2) || (label.length() > 3)) {
            throw new IllegalArgumentException("invalid length for label string: " + label);
        }

        int row = SBS_ROW_LABELLING_PATTERN.indexOf(label.charAt(0));
        if (row == -1) {
            throw new IllegalArgumentException("row is invalid in label string: " + label);
        }

        int col = Integer.parseInt(label.substring(1)) - 1;

        if (col >= SBS_MAX_COLS) {
            throw new IllegalArgumentException("column is invalid in label string: " + label);
        }
        return new Pair<>(row, col);
    }

}
