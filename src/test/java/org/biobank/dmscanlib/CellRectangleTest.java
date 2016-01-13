package org.biobank.dmscanlib;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.biobank.dmscanlib.CellRectangle;
import org.biobank.platedecoder.model.BarcodePosition;
import org.biobank.platedecoder.model.PlateOrientation;
import org.biobank.platedecoder.model.PlateType;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.geometry.Bounds;
import javafx.scene.shape.Rectangle;

public class CellRectangleTest {

    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(CellRectangleTest.class);

    @Test
    public void validCellsForBoundingBox() {
        Rectangle boundingBox = new Rectangle(1000, 1000, 3000, 3000);

        for (PlateOrientation orientation : PlateOrientation.values()) {
            for (PlateType plateType : PlateType.values()) {
                for (BarcodePosition barcodePosition : BarcodePosition.values()) {

                    Set<CellRectangle> cells =
                        CellRectangle.getCellsForBoundingBox(boundingBox,
                                                             orientation,
                                                             plateType,
                                                             barcodePosition);

                    List<CellRectangle> rectsSorted = sortCells(cells);

                    for (CellRectangle cell : rectsSorted) {
                        assertTrue(boundingBox.contains(cell.getX(), cell.getY()));
                        assertTrue(boundingBox.contains(cell.getX() + cell.getWidth(),
                                                        cell.getY()));
                        assertTrue(boundingBox.contains(cell.getX() + cell.getWidth(),
                                                        cell.getY() + cell.getHeight()));
                        assertTrue(boundingBox.contains(cell.getX(),
                                                        cell.getY() + cell.getHeight()));
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

    /**
     * If the bounds are offset from the origin, so should the first well
     */
    @Test
    public void boundsOffsetFromOrigin() {
        Rectangle bounds = new Rectangle(50, 50, 1000, 1000);

        Set<CellRectangle> cellSet = CellRectangle.getCellsForBoundingBox(
            bounds,
            PlateOrientation.LANDSCAPE,
            PlateType.PT_96_WELLS,
            BarcodePosition.TOP);

        List<CellRectangle> cellsSorted = sortCells(cellSet);

        //logWellRectangle(cellSet);

        Bounds firstRect = cellsSorted.get(0).getBoundsRectangle();
        assertEquals(bounds.getX(), firstRect.getMinX(), 0.1);
    }

    @Test
    public void landscape8x12Top() {
        Rectangle bounds = new Rectangle(0, 0, 1000, 1000);

        Set<CellRectangle> cellSet = CellRectangle.getCellsForBoundingBox(
            bounds,
            PlateOrientation.LANDSCAPE,
            PlateType.PT_96_WELLS,
            BarcodePosition.TOP);

        List<CellRectangle> cellsSorted = sortCells(cellSet);

        // logWellRectangle(cellsSorted);

        assertEquals("A1", cellsSorted.get(0).getLabel());
        assertEquals("H12", cellsSorted.get(cellsSorted.size() - 1).getLabel());

        // check that rectangles do not not intersect
        for (int i = 0, n = cellsSorted.size(); i < n - 1; ++i) {
            CellRectangle cell1 = cellsSorted.get(i);
            Bounds boundsRect1 = cell1.getBoundsRectangle();

            assertTrue(bounds.getBoundsInParent().contains(boundsRect1));

            for (int j = i + 1; j < n; ++j) {
                CellRectangle cell2 = cellsSorted.get(j);
                Bounds boundsRect2 = cell2.getBoundsRectangle();
                assertFalse(
                    String.format("r1: %s, r2: %s", cell1, cell2),
                    boundsRect1.intersects(boundsRect2));
            }
        }
    }

    @Test
    public void portrait8x12Top() {
        Rectangle bounds = new Rectangle(0, 0, 1000, 1000);

        Set<CellRectangle> cellSet = CellRectangle.getCellsForBoundingBox(
            bounds,
            PlateOrientation.PORTRAIT,
            PlateType.PT_96_WELLS,
            BarcodePosition.TOP);

        List<CellRectangle> cellsSorted = new ArrayList<CellRectangle>(cellSet);
        Collections.sort(cellsSorted);

        //logWellRectangle(cellSet);

        assertEquals("A1", cellsSorted.get(0).getLabel());
        assertEquals("H12", cellsSorted.get(cellsSorted.size() - 1).getLabel());

        // check that rectangles do not not intersect
        for (int i = 0, n = cellsSorted.size(); i < n - 1; ++i) {
            CellRectangle cell1 = cellsSorted.get(i);
            Bounds boundsRect1 = cell1.getBoundsRectangle();

            assertTrue(bounds.getBoundsInParent().contains(boundsRect1));

            for (int j = i + 1; j < n; ++j) {
                CellRectangle cell2 = cellsSorted.get(j);
                Bounds boundsRect2 = cell2.getBoundsRectangle();
                assertFalse(
                    String.format("r1: %s, r2: %s", cell1, cell2),
                    boundsRect1.intersects(boundsRect2));
            }
        }
    }

    @Test
    public void landscape8x12Bottom() {
        Rectangle bounds = new Rectangle(0, 0, 1000, 1000);

        Set<CellRectangle> cellSet = CellRectangle.getCellsForBoundingBox(
            bounds,
            PlateOrientation.LANDSCAPE,
            PlateType.PT_96_WELLS,
            BarcodePosition.BOTTOM);

        List<CellRectangle> cellsSorted = sortCells(cellSet);

        // logWellRectangle(cellsSorted);

        assertEquals("A1", cellsSorted.get(0).getLabel());
        assertEquals("H12", cellsSorted.get(cellsSorted.size() - 1).getLabel());

        // check that rectangles do not not intersect
        for (int i = 0, n = cellsSorted.size(); i < n - 1; ++i) {
            CellRectangle cell1 = cellsSorted.get(i);
            Bounds boundsRect1 = cell1.getBoundsRectangle();

            assertTrue(bounds.getBoundsInParent().contains(boundsRect1));

            for (int j = i + 1; j < n; ++j) {
                CellRectangle cell2 = cellsSorted.get(j);
                Bounds boundsRect2 = cell2.getBoundsRectangle();
                assertFalse(
                    String.format("r1: %s, r2: %s", cell1, cell2),
                    boundsRect1.intersects(boundsRect2));
            }
        }
    }

    @Test
    public void portrait8x12Bottom() {
        Rectangle bounds = new Rectangle(0, 0, 1000, 1000);

        Set<CellRectangle> cellSet = CellRectangle.getCellsForBoundingBox(
            bounds,
            PlateOrientation.PORTRAIT,
            PlateType.PT_96_WELLS,
            BarcodePosition.BOTTOM);

        List<CellRectangle> cellsSorted = sortCells(cellSet);

        // logWellRectangle(cellsSorted);

        assertEquals("A1", cellsSorted.get(0).getLabel());
        assertEquals("H12", cellsSorted.get(cellsSorted.size() - 1).getLabel());

        // check that rectangles do not not intersect
        for (int i = 0, n = cellsSorted.size(); i < n - 1; ++i) {
            CellRectangle cell1 = cellsSorted.get(i);
            Bounds boundsRect1 = cell1.getBoundsRectangle();

            assertTrue(bounds.getBoundsInParent().contains(boundsRect1));

            for (int j = i + 1; j < n; ++j) {
                CellRectangle cell2 = cellsSorted.get(j);
                Bounds boundsRect2 = cell2.getBoundsRectangle();
                assertFalse(
                    String.format("r1: %s, r2: %s", cell1, cell2),
                    boundsRect1.intersects(boundsRect2));
            }
        }
    }

    private List<CellRectangle> sortCells(Set<CellRectangle> cells) {
        assertNotNull(cells);
        assertFalse(cells.isEmpty());
        return CellRectangle.sortCells(cells);
    }

}
