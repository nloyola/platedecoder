package org.biobank.dmscanlib;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.biobank.platedecoder.model.BarcodePosition;
import org.biobank.platedecoder.model.PlateOrientation;
import org.biobank.platedecoder.model.PlateType;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.scene.shape.Rectangle;

/**
 * All these tests require that a flatbed scanner be connected to the computer.
 *
 * <p>See the README.md for how to set up Eclim to run these tests.
 */
public class DmScanLibTest {

   // @SuppressWarnings("unused")
   private static Logger LOG = LoggerFactory.getLogger(DmScanLibTest.class);

   private static final String TEST_IMAGES_DIR = System.getProperty("user.dir") + "/testImages";

   private static final int IMAGE_PIXELS_THRESHOLD = 5;

   private static final ScanLib SCAN_LIB;

   private static final String DEVICE_NAME;

   static {
      // these tests need to run on the test images directory
      Path path = Paths.get(TEST_IMAGES_DIR);
      if (!Files.exists(path)) {
         throw new IllegalStateException("testImages directory not present");
      }

      SCAN_LIB = ScanLib.getInstance();
      if (!ScanLib.runningMsWindows()) {
         DeviceNamesResult result = SCAN_LIB.getDeviceNames();
         if (result.getResultCode() != ScanLibResult.Result.SUCCESS) {
            throw new IllegalStateException("could not get scanning library device names");
         }
         Set<String> deviceNames = result.getDeviceNames();
         if (deviceNames.isEmpty()) {
            throw new IllegalStateException("scanning library reports ZERO devices");
         }

         // select the fist device by default
         DEVICE_NAME = deviceNames.iterator().next();
      } else {
         DEVICE_NAME = "";
      }
   }

   @Before
   public void beforeMethod() {
      if (ScanLib.runningMsWindows()) {
         ScanLibResult r = SCAN_LIB.selectSourceAsDefault();
         assertEquals(ScanLibResult.Result.SUCCESS, r.getResultCode());
      }
   }

   @Test
   public void getValidDpis() throws Exception {
      ValidDpisResult result = SCAN_LIB.getValidDpis(DEVICE_NAME);
      assertNotNull(result);
      assertEquals(ScanLibResult.Result.SUCCESS, result.getResultCode());

      Set<Integer> validDpis = result.getValidDpis();
      assertTrue(validDpis.size() > 0);
      LOG.info("valid dpis: {}", validDpis);
   }

   @Test
   public void scanBrightnessLimits() throws Exception {
      int [] brightnessLimits = new int [] {
         ScanLib.MAX_BRIGHTNESS,
         ScanLib.MIN_BRIGHTNESS
      };

      for (int brightness : brightnessLimits) {
         ScanLibResult r = SCAN_LIB.scanImage(0,
                                              DEVICE_NAME,
                                              75,
                                              brightness,
                                              0,
                                              0.0,
                                              0.0,
                                              1.0,
                                              1.0,
                                              "tempscan.png");

         assertNotNull(r);
         assertEquals(ScanLibResult.Result.SUCCESS, r.getResultCode());

         r = SCAN_LIB.scanFlatbed(0,
                                  DEVICE_NAME,
                                  75,
                                  brightness,
                                  0,
                                  "tempscan.png");

         assertNotNull(r);
         assertEquals(ScanLibResult.Result.SUCCESS, r.getResultCode());
      }
   }

   @Test
   public void scanInvalidBrightness() throws Exception {
      int [] invalidValues = new int [] {
         ScanLib.MAX_BRIGHTNESS + 1,
         ScanLib.MIN_BRIGHTNESS - 1
      };

      for (int brightness : invalidValues) {
         ScanLibResult r = SCAN_LIB.scanImage(0,
                                              DEVICE_NAME,
                                              75,
                                              brightness,
                                              0,
                                              0.0,
                                              0.0,
                                              1.0,
                                              1.0,
                                              "tempscan.png");

         assertNotNull(r);
         assertEquals(ScanLibResult.Result.INVALID_BRIGHTNESS, r.getResultCode());

         r = SCAN_LIB.scanFlatbed(0,
                                  DEVICE_NAME,
                                  75,
                                  brightness,
                                  0,
                                  "tempscan.png");

         assertNotNull(r);
         assertEquals(ScanLibResult.Result.INVALID_BRIGHTNESS, r.getResultCode());
      }
   }

   @Test
   public void scanContrastLimits() throws Exception {
      int [] contrastLimits = new int [] {
         ScanLib.MAX_CONTRAST,
         ScanLib.MIN_CONTRAST
      };

      for (int contrast : contrastLimits) {
         ScanLibResult r = SCAN_LIB.scanImage(0,
                                              DEVICE_NAME,
                                              75,
                                              0,
                                              contrast,
                                              0.0,
                                              0.0,
                                              1.0,
                                              1.0,
                                              "tempscan.png");

         assertNotNull(r);
         assertEquals(ScanLibResult.Result.SUCCESS, r.getResultCode());

         r = SCAN_LIB.scanFlatbed(0,
                                  DEVICE_NAME,
                                  75,
                                  0,
                                  contrast,
                                  "tempscan.png");

         assertNotNull(r);
         assertEquals(ScanLibResult.Result.SUCCESS, r.getResultCode());
      }
   }

   @Test
   public void scanInvalidContrast() throws Exception {
      int [] invalidValues = new int [] {
         ScanLib.MAX_CONTRAST + 1,
         ScanLib.MIN_CONTRAST - 1
      };

      for (int contrast : invalidValues) {
         ScanLibResult r = SCAN_LIB.scanImage(0,
                                              DEVICE_NAME,
                                              75,
                                              0,
                                              contrast,
                                              0.0,
                                              0.0,
                                              1.0,
                                              1.0,
                                              "tempscan.png");

         assertNotNull(r);
         assertEquals(ScanLibResult.Result.INVALID_CONTRAST, r.getResultCode());

         r = SCAN_LIB.scanFlatbed(0,
                                  DEVICE_NAME,
                                  75,
                                  0,
                                  contrast,
                                  "tempscan.png");

         assertNotNull(r);
         assertEquals(ScanLibResult.Result.INVALID_CONTRAST, r.getResultCode());
      }
   }

   @Test
   public void scanImage() throws Exception {
      double left = 1.0;
      double top = 1.0;
      double width = 2.0;
      double height = 3.0;

      final int dpi = 300;
      String filename = "tempscan.png";
      File file = new File(filename);
      file.delete(); // dont care if file doesn't exist

      ScanLibResult r = SCAN_LIB.scanImage(2,
                                           DEVICE_NAME,
                                           dpi,
                                           0,
                                           0,
                                           left,
                                           top,
                                           width + left,
                                           height + top,
                                           filename);

      assertNotNull(r);
      assertEquals(ScanLibResult.Result.SUCCESS, r.getResultCode());

      BufferedImage image = ImageIO.read(new File(filename));

      int expectedWidth = new Double(width * dpi).intValue();
      int expectedHeight = new Double(height * dpi).intValue();

      assertTrue(Math.abs(expectedWidth - image.getWidth()) < IMAGE_PIXELS_THRESHOLD);
      assertTrue(Math.abs(expectedHeight - image.getHeight()) < IMAGE_PIXELS_THRESHOLD);
   }

   @Test
   public void scanImageBadParams() throws Exception {
       double left = 1.0;
       double top = 1.0;
       double width = 2.0;
       double height = 3.0;

      ScanLibResult r = SCAN_LIB.scanImage(0,
                                           DEVICE_NAME,
                                           300,
                                           0,
                                           0,
                                           left,
                                           top,
                                           width + left,
                                           height + top,
                                           null);

      assertEquals(ScanLibResult.Result.FAIL, r.getResultCode());

      r = SCAN_LIB.scanImage(0,
                             DEVICE_NAME,
                             175,
                             0,
                             0,
                             left,
                             top,
                             width + left,
                             height + top,
                             "tempscan.png");
      assertEquals(ScanLibResult.Result.INVALID_DPI, r.getResultCode());
   }

   @Test
   public void scanFlatbed() throws Exception {
      final int dpi = 75;
      String filename = "flatbed.png";
      File file = new File(filename);
      file.delete(); // dont care if file doesn't exist

      ScanLibResult r = SCAN_LIB.scanFlatbed(0, DEVICE_NAME, dpi, 0, 0, filename);

      assertNotNull(r);
      assertEquals(ScanLibResult.Result.SUCCESS, r.getResultCode());
   }

   @Test
   public void scanFlatbedBadParams() throws Exception {
      // use null for filename
      ScanLibResult r = SCAN_LIB.scanFlatbed(0, DEVICE_NAME, 300, 0, 0, null);
      assertEquals(ScanLibResult.Result.FAIL, r.getResultCode());

      // use a bad DPI
      r = SCAN_LIB.scanFlatbed(0, DEVICE_NAME, 0, 0, 0, "tempscan.png");
      assertEquals(ScanLibResult.Result.FAIL, r.getResultCode());
   }

   @Test
   public void scanAndDecode() throws Exception {
      double x = 0.400;
      double y = 0.265;
      double width = 4.2;
      double height = 2.8;
      final int dpi = 300;

      Rectangle wellsBbox = new Rectangle(0,
                                          0,
                                          Math.floor(dpi * width),
                                          Math.floor(dpi * height));

      Set<CellRectangle> wells =
         CellRectangle.getCellsForBoundingBox(wellsBbox,
                                              PlateOrientation.LANDSCAPE,
                                              PlateType.PT_96_WELLS,
                                              BarcodePosition.BOTTOM);

      DecodeResult dr = SCAN_LIB.scanAndDecode(3,
                                               DEVICE_NAME,
                                               dpi,
                                               0,
                                               0,
                                               x,
                                               y,
                                               width + x,
                                               height + y,
                                               DecodeOptions.getDefaultDecodeOptions(),
                                               wells.toArray(new CellRectangle[] {}));

      assertNotNull(dr);
      assertFalse(dr.getDecodedWells().isEmpty());

      for (DecodedWell decodedWell : dr.getDecodedWells()) {
         LOG.debug("decoded well: {}", decodedWell);
      }

      LOG.debug("wells decoded: {}", dr.getDecodedWells().size());
   }

   /*
    * Uses files from Dropbox shared folder.
    */
   @Test
   public void decodeImage() throws Exception {
      final String fname =
         System.getProperty("user.dir") + "/testImages/8x12/scanned_20140203.png";
      File imageFile = new File(fname);

      BufferedImage image = ImageIO.read(imageFile);
      Rectangle imageBbox = new Rectangle(0, 0, image.getWidth(), image.getHeight());

      Set<CellRectangle> wells =
         CellRectangle.getCellsForBoundingBox(imageBbox,
                                              PlateOrientation.LANDSCAPE,
                                              PlateType.PT_96_WELLS,
                                              BarcodePosition.BOTTOM);

      DecodeResult r = SCAN_LIB.decodeImage(0,
                                            fname,
                                            DecodeOptions.getDefaultDecodeOptions(),
                                            wells.toArray(new CellRectangle[] {}));

      assertNotNull(r);
      assertTrue(r.getDecodedWells().size() > 0);
   }

   @Test
   public void decodeBadParams() throws Exception {
      final String fname = System.getProperty("user.dir") + "/testImages/8x12/96tubes.png";

      DecodeOptions decodeOptions = DecodeOptions.getDefaultDecodeOptions();
      DecodeResult r = SCAN_LIB.decodeImage(0, fname, decodeOptions, null);

      assertNotNull(r);
      assertEquals(ScanLibResult.Result.FAIL, r.getResultCode());
      assertEquals(0, r.getDecodedWells().size());

      // do not fill in the well information
      CellRectangle[] wells = new CellRectangle[8 * 12];

      r = SCAN_LIB.decodeImage(0, fname, decodeOptions, wells);

      assertNotNull(r);
      assertEquals(ScanLibResult.Result.INVALID_NOTHING_TO_DECODE, r.getResultCode());
      assertEquals(0, r.getDecodedWells().size());

      // try and invalid filename
      wells = new CellRectangle[] {
         new CellRectangle("A12", new Rectangle(10, 20, 120, 110)),
      };

      r = SCAN_LIB.decodeImage(0, new UUID(128, 256).toString(), decodeOptions, wells);

      assertNotNull(r);
      assertEquals(ScanLibResult.Result.INVALID_IMAGE, r.getResultCode());
      assertEquals(0, r.getDecodedWells().size());
   }

}
