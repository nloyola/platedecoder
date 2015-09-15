package org.biobank.platedecoder.model;

import javafx.util.Pair;

public class SbsPosition implements Comparable<SbsPosition> {

    private final Integer row;
    private final Integer col;

    public SbsPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public SbsPosition(String label) {
        Pair<Integer, Integer> position = SbsLabeling.toRowCol(label);
        this.row = position.getKey();
        this.col = position.getValue();
    }

    public Integer getRow() {
        return row;
    }

    public Integer getCol() {
        return col;
    }

    public String getLabel() {
        return SbsLabeling.fromRowCol(row, col);
    }

    public boolean equals(Integer row, Integer col) {
        return (this.row.equals(row) && this.col.equals(col));
    }

    @Override
    public int compareTo(SbsPosition that) {
        if (this.row == that.row) {
            return this.col > that.col ? 1 : this.col < that.col ? -1 : 0;
        }
        return this.row > that.row ? 1 : -1;
    }
}
