package org.biobank.platedecoder.dmscanlib;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.scene.shape.Rectangle;

import org.biobank.platedecoder.model.BarcodePosition;
import org.biobank.platedecoder.model.PlateType;
import org.biobank.platedecoder.model.PlateOrientation;

public class DmScanLibCommonTest extends RequiresJniLibraryTest {

    @SuppressWarnings("unused")
    private static Logger LOG = LoggerFactory.getLogger(DmScanLibCommonTest.class);

    private static final String TEST_IMAGES_DIR =
        System.getProperty("user.dir") + "/testImages";

    @Before
    public void beforeMethod() {
        // these tests need to run on the test images directory
        Path path = Paths.get(TEST_IMAGES_DIR);
        Assume.assumeTrue(Files.exists(path));
    }

    /*
     * Uses files from Dropbox shared folder.
     */
    @Test
    public void decodeImage() throws Exception {
        ScanLib scanLib = ScanLib.getInstance();

        final String fname = System.getProperty("user.dir")
            + "/testImages/8x12/scanned_20140203.png";
        File imageFile = new File(fname);

        BufferedImage image = ImageIO.read(imageFile);
        Rectangle imageBbox = new Rectangle(
            0,
            0,
            image.getWidth(),
            image.getHeight());

        Set<CellRectangle> wells = CellRectangle.getCellsForBoundingBox(
            imageBbox, PlateOrientation.LANDSCAPE, PlateType.PT_96_WELLS,
            BarcodePosition.BOTTOM);

        DecodeResult r = scanLib.decodeImage(0, fname,
            DecodeOptions.getDefaultDecodeOptions(),
            wells.toArray(new CellRectangle[] {}));

        Assert.assertNotNull(r);
        Assert.assertTrue(r.getDecodedWells().size() > 0);
    }

    @Test
    public void decodeBadParams() throws Exception {
        ScanLib scanLib = ScanLib.getInstance();

        final String fname = System.getProperty("user.dir") + "/testImages/8x12/96tubes.bmp";

        DecodeOptions decodeOptions = DecodeOptions.getDefaultDecodeOptions();
        DecodeResult r = scanLib.decodeImage(0, fname, decodeOptions, null);

        Assert.assertNotNull(r);
        Assert.assertEquals(ScanLibResult.Result.FAIL, r.getResultCode());
        Assert.assertEquals(0, r.getDecodedWells().size());

        // do not fill in the well information
        CellRectangle[] wells = new CellRectangle[8 * 12];

        r = scanLib.decodeImage(0, fname, decodeOptions, wells);

        Assert.assertNotNull(r);
        Assert.assertEquals(ScanLibResult.Result.INVALID_NOTHING_TO_DECODE,
            r.getResultCode());
        Assert.assertEquals(0, r.getDecodedWells().size());

        // try and invalid filename
        wells = new CellRectangle[] {
            new CellRectangle("A12", new Rectangle(10, 20, 120, 110)),
        };

        r = scanLib.decodeImage(0, new UUID(128, 256).toString(), decodeOptions, wells);

        Assert.assertNotNull(r);
        Assert.assertEquals(ScanLibResult.Result.INVALID_IMAGE, r.getResultCode());
        Assert.assertEquals(0, r.getDecodedWells().size());
    }

}
