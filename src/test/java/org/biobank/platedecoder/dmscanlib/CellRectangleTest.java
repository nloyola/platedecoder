package org.biobank.platedecoder.dmscanlib;

import org.biobank.platedecoder.dmscanlib.CellRectangle;
import org.biobank.platedecoder.model.BarcodePosition;
import org.biobank.platedecoder.model.PlateOrientation;
import org.biobank.platedecoder.model.PlateType;

import static org.junit.Assert.*;

import java.util.Set;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.scene.shape.Rectangle;

public class CellRectangleTest {

    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(CellRectangleTest.class);

    @Test
    public void validCellsForBoundingBox() {
        Rectangle boundingBox = new Rectangle(100, 100, 200, 200);

        for (PlateOrientation orientation : PlateOrientation.values()) {
            for (PlateType plateType : PlateType.values()) {
                for (BarcodePosition barcodePosition : BarcodePosition.values()) {

                    Set<CellRectangle> cells =
                        CellRectangle.getCellsForBoundingBox(boundingBox,
                                                             orientation,
                                                             plateType,
                                                             barcodePosition);

                    for (CellRectangle cell : cells) {
                        for (int i = 0; i < 4; ++i) {
                            assertTrue(boundingBox.contains(cell.getCornerX(i), cell.getCornerY(i)));
                        }
                    }
                }
            }
        }
    }

    @Test
    public void validA1LabelsForPosition() {
        for (PlateOrientation orientation : PlateOrientation.values()) {
            for (PlateType plateType : PlateType.values()) {
                for (BarcodePosition barcodePosition : BarcodePosition.values()) {

                    int a1Row, a1Col;

                    if (barcodePosition == BarcodePosition.TOP) {
                        if (orientation == PlateOrientation.LANDSCAPE) {
                            a1Row = 0;
                            a1Col = 0;
                        } else {
                            a1Row = 0;
                            a1Col = plateType.getRows() - 1;
                        }
                    } else {
                        if (orientation == PlateOrientation.LANDSCAPE) {
                            a1Row = 0;
                            a1Col = plateType.getCols() - 1;
                        } else {
                            a1Row = 0;
                            a1Col = 0;
                        }
                    }

                    String label = CellRectangle.getLabelForPosition(a1Row,
                                                                     a1Col,
                                                                     orientation,
                                                                     plateType,
                                                                     barcodePosition);

                    assertEquals("A1", label);
                }
            }
        }
    }

    @Test
    public void validCompare() {
        CellRectangle cell1 = new CellRectangle("A1", new Rectangle(0, 0, 10, 10));
        CellRectangle cell2 = new CellRectangle("A2", new Rectangle(10, 10, 20, 20));
        CellRectangle cell3 = new CellRectangle("A1", new Rectangle(0, 0, 10, 10));

        assertEquals(-1, cell1.compareTo(cell2));
        assertEquals(1, cell2.compareTo(cell1));
        assertEquals(0, cell1.compareTo(cell3));
    }

}

