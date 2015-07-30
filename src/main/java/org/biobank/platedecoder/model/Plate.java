package org.biobank.platedecoder.model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;

public class Plate {

    //private static final Logger LOG = LoggerFactory.getLogger(Plate.class);

    private final int rows;

    private final int cols;

    private final PlateWell[][] plateWells;

    private final Stream<PlateWell> wellsStream;

    private Set<PlateWell> selectedWells = new HashSet<>();

    private PlateWell regionStartWell;

    public Plate(PlateTypes plateType) {
        rows = plateType.getRows();
        cols = plateType.getCols();

        plateWells = new PlateWell[rows][cols];
        for (int r = 0; r < rows; ++r) {
            for (int c = 0; c < cols; ++c) {
                plateWells[r][c] = new PlateWell(this, r, c);
            }
        }

        wellsStream = Arrays.stream(plateWells).flatMap(x -> Arrays.stream(x));
    }

    public PlateWell getWell(int row, int col) {
        checkRowAndCol(row, col);
        return plateWells[row][col];
    }

    public List<PlateWell> getSelected() {
        return wellsStream.filter(w -> w.isSelected()).collect(Collectors.toList());
    }

    public void wellSelected(PlateWell well,
        boolean selectedRegionEnd,
        boolean addToSelection) {
        if (!selectedRegionEnd && !addToSelection) {
            // deselet all but the selected cell
            selectedWells.stream().filter(w -> w != well).forEach(w -> w.setSelected(false));

            selectedWells = new HashSet<>(Arrays.asList(well));
            well.setSelected(true);
            regionStartWell = well;
        } else if (selectedRegionEnd) {
            if (regionStartWell != null) {
                int startRow = regionStartWell.getRow(), startCol = regionStartWell.getCol(), endRow = well.getRow(), endCol = well.getCol();

                int minRow = Math.min(startRow, endRow), minCol = Math.min(startCol, endCol), maxRow = Math.max(startRow, endRow), maxCol = Math.max(startCol, endCol);

                selectedWells.addAll(selectWellRange(minRow, minCol, maxRow, maxCol));
                selectedWells.stream().forEach(w -> w.setSelected(true));
            }
        } else {
            selectedWells.add(well);
            well.setSelected(!well.isSelected());
            regionStartWell = well;
        }
    }

    private void checkRowAndCol(int row, int col) {
        if (row >= rows) {
            throw new IllegalArgumentException("row exceeds maximum: rows: " + row
                + ", maxRows: " + rows);
        }

        if (col >= cols) {
            throw new IllegalArgumentException("col exceeds maximum: cols: " + col
                + ", maxRows: " + cols);
        }
    }

    private Set<PlateWell> selectWellRange(int startRow,
        int startCol,
        int endRow,
        int endCol) {
        Set<PlateWell> result = new HashSet<>();
        for (int row = startRow; row <= endRow; ++row) {
            for (int col = startCol; col <= endCol; ++col) {
                result.add(plateWells[row][col]);
            }
        }
        return result;
    }
}
