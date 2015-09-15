package org.biobank.platedecoder.model;

import javafx.scene.shape.Rectangle;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.biobank.platedecoder.dmscanlib.CellRectangle;
import org.biobank.platedecoder.dmscanlib.DecodeOptions;
import org.biobank.platedecoder.dmscanlib.DecodeResult;
import org.biobank.platedecoder.dmscanlib.DecodedWell;
import org.biobank.platedecoder.dmscanlib.ScanLib;
import org.biobank.platedecoder.dmscanlib.ScanLibResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;

public class Plate {

    private static final Logger LOG = LoggerFactory.getLogger(Plate.class);

    private final PlateType plateType;

    private final PlateOrientation plateOrientation;

    private final BarcodePosition barcodePosition;

    private final int rows;

    private final int cols;

    private final PlateWell[][] plateWells;

    private final Stream<PlateWell> wellsStream;

    private Set<PlateWell> selectedWells = new HashSet<>();

    private PlateWell regionStartWell;

    public Plate(PlateType plateType,
                 PlateOrientation orientation,
                 BarcodePosition barcodePosition) {

        this.plateType = plateType;
        this.plateOrientation = orientation;
        this.barcodePosition = barcodePosition;

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
                             boolean   selectedRegionEnd,
                             boolean   addToSelection) {
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

    public DecodeResult decodeImage(URL url, Rectangle wellRectangle) {
        Set<CellRectangle> cells = CellRectangle.getCellsForBoundingBox(
            wellRectangle,
            plateOrientation,
            plateType,
            barcodePosition);

        // for (CellRectangle cell : cells) {
        //     LOG.debug("cell: {}", cell);
        // }

        try {
            File file = new File(url.toURI());
            DecodeResult result = ScanLib.getInstance().decodeImage(
                1L,
                file.toString(),
                DecodeOptions.getDefaultDecodeOptions(),
                cells.toArray(new CellRectangle[] {}));

            LOG.debug("decode result: {}", result.getResultCode());

            if (result.getResultCode() == ScanLibResult.Result.SUCCESS) {
                for (DecodedWell well : result.getDecodedWells()) {
                    SbsPosition position = new SbsPosition(well.getLabel());
                    PlateWell plateWell = plateWells[position.getRow()][position.getCol()];
                    plateWell.setInventoryId(well.getMessage());
                    LOG.debug("decoded well: {}", plateWell);
                }
            }

            return result;
        } catch (URISyntaxException ex) {
            LOG.error(ex.getMessage());
            return new DecodeResult(ScanLib.SC_FAIL, 0, ex.getMessage());
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
