package org.biobank.platedecoder.dmscanlib;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Set;

import javax.imageio.ImageIO;

import static org.junit.Assert.*;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.scene.shape.Rectangle;

import org.biobank.platedecoder.model.BarcodePosition;
import org.biobank.platedecoder.model.PlateType;
import org.biobank.platedecoder.model.PlateOrientation;

public class DmScanLibWindowsTest extends RequiresJniLibraryTest {

    private static Logger log = LoggerFactory.getLogger(DmScanLibWindowsTest.class);

    @Before
    public void beforeMethod() {
        // these tests are valid only when not running on windows
        Assume.assumeTrue(LibraryLoader.runningMsWindows());
    }

    @Test
    public void scanImage() throws Exception {
        ScanLib scanLib = ScanLib.getInstance();
        ScanLibResult r = scanLib.selectSourceAsDefault();
        assertEquals(ScanLibResult.Result.SUCCESS, r.getResultCode());

        Rectangle scanRegion = new Rectangle(1, 1, 2, 3);
        Rectangle scanBbox = ScanLibWia.getWiaBoundingBox(scanRegion);

        final int dpi = 300;
        String filename = "tempscan.png";
        File file = new File(filename);
        file.delete(); // dont care if file doesn't exist

        r = scanLib.scanImage(
            2,
            dpi,
            0,
            0,
            scanBbox.getX(),
            scanBbox.getY(),
            scanBbox.getWidth(),
            scanBbox.getHeight(),
            filename);

        assertNotNull(r);
        assertEquals(ScanLibResult.Result.SUCCESS, r.getResultCode());

        BufferedImage image = ImageIO.read(new File(filename));
        assertEquals(new Double(scanRegion.getWidth() * dpi).intValue(), image.getWidth());
        assertEquals(new Double(scanRegion.getHeight() * dpi).intValue(), image.getHeight());
    }

    @Test
    public void scanImageBadParams() throws Exception {
        ScanLib scanLib = ScanLib.getInstance();
        Rectangle scanBox = new Rectangle(0, 0, 4, 4);

        ScanLibResult r = scanLib.scanImage(
            0, 300, 0, 0, scanBox.getX(), scanBox.getY(), scanBox.getWidth(), scanBox.getHeight(), null);
        assertEquals(ScanLibResult.Result.FAIL, r.getResultCode());

        r = scanLib.scanImage(
            0, 175, 0, 0, scanBox.getX(), scanBox.getY(), scanBox.getWidth(), scanBox.getHeight(), "tempscan.bmp");
        assertEquals(ScanLibResult.Result.INVALID_DPI, r.getResultCode());
    }

    @Test
    public void scanFlatbed() throws Exception {
        ScanLib scanLib = ScanLib.getInstance();

        final int dpi = 300;
        String filename = "flatbed.bmp";
        File file = new File(filename);
        file.delete(); // dont care if file doesn't exist

        ScanLibResult r = scanLib.scanFlatbed(0, dpi, 0, 0, filename);

        assertNotNull(r);
        assertEquals(ScanLibResult.Result.SUCCESS, r.getResultCode());
    }

    @Test
    public void scanFlatbedBadParams() throws Exception {
        ScanLib scanLib = ScanLib.getInstance();

        ScanLibResult r = scanLib.scanFlatbed(0, 300, 0, 0, null);
        assertEquals(ScanLibResult.Result.FAIL, r.getResultCode());

        r = scanLib.scanFlatbed(0, 0, 0, 0, "tempscan.bmp");
        assertEquals(ScanLibResult.Result.FAIL, r.getResultCode());
    }

    @Test
    public void scanAndDecode() throws Exception {
        ScanLib scanLib = ScanLib.getInstance();

        double x = 0.400;
        double y = 0.265;
        double width = 4.566 - x;
        double height = 3.020 - y;
        Rectangle scanRegion = new Rectangle(x, y, width, height);
        Rectangle scanBbox = ScanLibWia.getWiaBoundingBox(scanRegion);
        final int dpi = 300;

        Rectangle wellsBbox = new Rectangle(
            0,
            0,
            Math.floor(dpi * scanRegion.getWidth()),
            Math.floor(dpi * scanRegion.getHeight()));

        Set<CellRectangle> wells = CellRectangle.getCellsForBoundingBox(
            wellsBbox,
            PlateOrientation.LANDSCAPE,
            PlateType.PT_96_WELLS,
            BarcodePosition.BOTTOM);

        DecodeResult dr = scanLib.scanAndDecode(
            3, dpi,
            0,
            0,
            scanBbox.getX(),
            scanBbox.getY(),
            scanBbox.getWidth(),
            scanBbox.getHeight(),
            DecodeOptions.getDefaultDecodeOptions(),
            wells.toArray(new CellRectangle[] {}));

        assertNotNull(dr);
        assertFalse(dr.getDecodedWells().isEmpty());

        for (DecodedWell decodedWell : dr.getDecodedWells()) {
            log.debug("decoded well: {}", decodedWell);
        }

        log.debug("wells decoded: {}", dr.getDecodedWells().size());
    }
}
