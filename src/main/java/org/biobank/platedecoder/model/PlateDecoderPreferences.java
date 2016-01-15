package org.biobank.platedecoder.model;

import static org.biobank.dmscanlib.DecodeOptions.*;
import static org.biobank.platedecoder.model.PlateDecoderDefaults.*;

import java.util.Optional;
import java.util.prefs.Preferences;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.geometry.Point2D;
import javafx.scene.shape.Rectangle;

/**
 * Uses the Java Preferences API to store the user's preferences.
 *
 * <p>Preferences in Linux are in {@code $HOME/.java/.userPrefs/} and then look for the package name.
 */
public class PlateDecoderPreferences {

   @SuppressWarnings("unused")
   private static final Logger LOG = LoggerFactory.getLogger(PlateDecoderPreferences.class);

   private final Preferences prefs;

   private static final String PREFS_APP_WINDOW_WIDTH = "PREFS_APP_WINDOW_WIDTH";

   private static final String PREFS_APP_WINDOW_HEIGHT = "PREFS_APP_WINDOW_HEIGHT";

   private static final String PREFS_SCAN_REGION_X = "PREFS_SCAN_REGION_X";

   private static final String PREFS_SCAN_REGION_Y = "PREFS_SCAN_REGION_Y_";

   private static final String PREFS_SCAN_REGION_WIDTH = "PREFS_SCAN_REGION_WIDTH";

   private static final String PREFS_SCAN_REGION_HEIGHT = "PREFS_SCAN_REGION_HEIGHT";

   private static final String PREFS_WELL_GRID_X = "PREFS_WELL_GRID_X";

   private static final String PREFS_WELL_GRID_Y = "PREFS_WELL_GRID_Y_";

   private static final String PREFS_WELL_GRID_WIDTH = "PREFS_WELL_GRID_WIDTH";

   private static final String PREFS_WELL_GRID_HEIGHT = "PREFS_WELL_GRID_HEIGHT";

   private static final String PREFS_PLATE_TYPE = "PREFS_PLATE_TYPE";

   private static final String PREFS_PLATE_ORIENTATION = "PREFS_PLATE_ORIENTATION";

   private static final String PREFS_BARCODE_POSITION = "PREFS_BARCODE_POSITION";

   private static final String PREFS_FLATBED_LAST_USED_DPI = "PREFS_FLATBED_LAST_USED_DPI";

   private static final String PREFS_FLATBED_BRIGHTNESS = "PREFS_FLATBED_BRIGHTNESS";

   private static final String PREFS_FLATBED_CONTRAST = "PREFS_FLATBED_CONTRAST";

   private static final String PREFS_DECODER_DEBUG_LEVEL = "PREFS_DECODER_DEBUG_LEVEL";

   private static final String PREFS_MIN_EDGE_FACTOR  = "PREFS_MIN_EDGE_FACTOR";

   private static final String PREFS_MAX_EDGE_FACTOR  = "PREFS_MAX_EDGE_FACTOR";

   private static final String PREFS_SCAN_GAP_FACTOR  = "PREFS_SCAN_GAP_FACTOR";

   private static final String PREFS_SQUARE_DEVIATION = "PREFS_SQUARE_DEVIATION";

   private static final String PREFS_EDGE_THRESHOLD   = "PREFS_EDGE_THRESHOLD";

   private static final String PREFS_CORRECTIONS      = "PREFS_CORRECTIONS";

   private static final String PREFS_SPECIMEN_LINK_DIVIDER_POSITION =
      "PREFS_SPECIMEN_LINK_DIVIDER_POSITION";

   /**
    * Returns a reference to this singleton.
    *
    * @return the singleton.
    */
   public static PlateDecoderPreferences getInstance() {
      return PlateDecoderPreferencesHolder.INSTANCE;
   }

   private PlateDecoderPreferences() {
      prefs = Preferences.userNodeForPackage(PlateDecoderPreferences.class);
   }

   /**
    * The size of the application window as stored in the preferences.
    *
    * @return the size of the application window.
    */
   public Point2D getAppWindowSize() {
      double width  = prefs.getDouble(PREFS_APP_WINDOW_WIDTH, DEFAULT_APP_WINDOW_SIZE[0]);
      double height = prefs.getDouble(PREFS_APP_WINDOW_HEIGHT, DEFAULT_APP_WINDOW_SIZE[1]);
      return new Point2D(width, height);
   }

   /**
    * Saves the size of the application window in the preferences.
    *
    * @param width The width of the application window.
    *
    * @param height The height of the application window.
    */
   public void setAppWindowSize(double width, double height) {
      prefs.putDouble(PREFS_APP_WINDOW_WIDTH,  width);
      prefs.putDouble(PREFS_APP_WINDOW_HEIGHT, height);
   }

   /**
    * The dimensions of the grid in INCHES stored in the preferences.
    *
    * @return If the dimensions are found in the preferences, the dimensions of the grid in INCHES
    * are returned. If the dimensions not present in the preferences, empty is returned.
    */
   public Optional<Rectangle> getScanRegion() {
      double x      = prefs.getDouble(PREFS_SCAN_REGION_X,      -1);
      double y      = prefs.getDouble(PREFS_SCAN_REGION_Y,      -1);
      double width  = prefs.getDouble(PREFS_SCAN_REGION_WIDTH,  -1);
      double height = prefs.getDouble(PREFS_SCAN_REGION_HEIGHT, -1);

      if ((x < 0) || (y < 0) || (width < 0) || (height < 0)) {
         return Optional.empty();
      }

      return Optional.of(new Rectangle(x, y, width, height));
   }

   /**
    * Sets the dimensions of the scan region, in INCHES, to the preferences..
    *
    * @param region the rectangle containing the dimensions of the region.
    */
   public void setScanRegion(Rectangle region) {
      prefs.putDouble(PREFS_SCAN_REGION_X, region.getX());
      prefs.putDouble(PREFS_SCAN_REGION_Y, region.getY());
      prefs.putDouble(PREFS_SCAN_REGION_WIDTH, region.getWidth());
      prefs.putDouble(PREFS_SCAN_REGION_HEIGHT, region.getHeight());
   }

   /**
    * The size of the well grid, in pixels, stored in the preferences.
    *
    * <p>Each plate type has it's own well grid.
    *
    * @param plateType The plate type the grid is for.
    *
    * @return The size of the well grid stored in the preferences.
    */
   public Rectangle getWellRectangle(PlateType plateType) {
      double x      = prefs.getDouble(geKeyForWellRectangle(plateType, PREFS_WELL_GRID_X),
                                      DEFAULT_WELL_GRID[0]);
      double y      = prefs.getDouble(geKeyForWellRectangle(plateType, PREFS_WELL_GRID_Y),
                                      DEFAULT_WELL_GRID[1]);
      double width  = prefs.getDouble(geKeyForWellRectangle(plateType, PREFS_WELL_GRID_WIDTH),
                                      DEFAULT_WELL_GRID[2]);
      double height = prefs.getDouble(geKeyForWellRectangle(plateType, PREFS_WELL_GRID_HEIGHT),
                                      DEFAULT_WELL_GRID[3]);

      return new Rectangle(x, y, width, height);
   }

   /**
    * The dimensions of the grid, in pixels, stored in the preferences.
    *
    * <p>Each plate type has it's own well grid.
    *
    * @param plateType The plate type the grid is for.
    *
    * @param region The size of the well grid in pixels.
    */
   public void setWellRectangle(PlateType plateType, Rectangle region) {
      prefs.putDouble(geKeyForWellRectangle(plateType, PREFS_WELL_GRID_X), region.getX());
      prefs.putDouble(geKeyForWellRectangle(plateType, PREFS_WELL_GRID_Y), region.getY());
      prefs.putDouble(geKeyForWellRectangle(plateType, PREFS_WELL_GRID_WIDTH), region.getWidth());
      prefs.putDouble(geKeyForWellRectangle(plateType, PREFS_WELL_GRID_HEIGHT), region.getHeight());
   }

   /**
    * The plate type stored in the preferences.
    *
    * @return The plate type stored in the preferences.
    */
   public PlateType getPlateType() {
      return PlateType.valueOf(prefs.get(PREFS_PLATE_TYPE, DEFAULT_PLATE_TYPE));
   }

   /**
    * Saves the plate type to the preferences.
    *
    * @param newValue the value to save.
    */
   public void setPlateType(PlateType newValue) {
      prefs.put(PREFS_PLATE_TYPE, newValue.name());
   }

   /**
    * The plate orientation stored in the preferences.
    *
    * @return plate orientation stored in the preferences.
    */
   public PlateOrientation getPlateOrietation() {
      return PlateOrientation.valueOf(prefs.get(PREFS_PLATE_ORIENTATION, DEFAULT_PLATE_ORIENTATION));
   }

   /**
    * Saves the plate orientation in the preferences.
    *
    * @param newValue the plate orientation to save.
    */
   public void setPlateOrientation(PlateOrientation newValue) {
      prefs.put(PREFS_PLATE_ORIENTATION, newValue.name());
   }

   /**
    * The barcode position stored in the preferences.
    *
    * @return The barcode position stored in the preferences.
    */
   public BarcodePosition getBarcodePosition() {
      return BarcodePosition.valueOf(prefs.get(PREFS_BARCODE_POSITION, DEFAULT_BARCODE_POSITION));
   }

   /**
    * The barcode position stored in the preferences.
    *
    * @param newValue the value to store in the preferences.
    */
   public void setBarcodePosition(BarcodePosition newValue) {
      prefs.put(PREFS_BARCODE_POSITION, newValue.name());
   }

   /**
    * The position of the divider used in the specimen link scene.
    *
    * @return The position of the divider.
    */
   public double getSpecimenLinkDividerPosition() {
      return prefs.getDouble(PREFS_SPECIMEN_LINK_DIVIDER_POSITION,
                             DEFAULT_SPECIMEN_LINK_DIVIDER_POSITION);
   }

   /**
    * The position of the divider stored in the preferences.
    *
    * <p>It is used by the specimen link scene.
    *
    * @param position the value for the position of the divider.
    */
   public void setSpecimenLinkDividerPosition(double position) {
      prefs.putDouble(PREFS_SPECIMEN_LINK_DIVIDER_POSITION, position);
   }

   /**
    * The DPI value stored in the preferences.
    *
    * @return The DPI value stored in the preferences.
    */
   public FlatbedDpi getFlatbedDpi() {
      return FlatbedDpi.valueOf(prefs.get(PREFS_FLATBED_LAST_USED_DPI, DEFAULT_FLATBED_IMAGE_DPI));
   }

   public void setFlatbedDpi(FlatbedDpi dpi) {
      prefs.put(PREFS_FLATBED_LAST_USED_DPI, dpi.name());
   }

   /**
    * The brightness value stored in the preferences, used with the flatbed scanner.
    *
    * @return The brightness value stored in the preferences.
    */
   public long getFlatbedBrightness() {
      return prefs.getLong(PREFS_FLATBED_BRIGHTNESS, DEFAULT_FLATBED_BRIGHTNESS);
   }

   public void setFlatbedBrightness(long brightness) {
      prefs.put(PREFS_FLATBED_BRIGHTNESS, String.valueOf(brightness));
   }

   /**
    * The contrast value stored in the preferences, used with the flatbed scanner.
    *
    * @return The contrast value stored in the preferences.
    */
   public long getFlatbedContrast() {
      return prefs.getLong(PREFS_FLATBED_CONTRAST, DEFAULT_FLATBED_CONTRAST);
   }

   public void setFlatbedContrast(long contrast) {
      prefs.put(PREFS_FLATBED_CONTRAST, String.valueOf(contrast));
   }

   public long getDecoderDebugLevel() {
      return prefs.getLong(PREFS_DECODER_DEBUG_LEVEL, DEFAULT_DECODER_DEBUG_LEVEL);
   }

   public void setDecoderDebugLevel(long value) {
      prefs.put(PREFS_DECODER_DEBUG_LEVEL, String.valueOf(value));
   }

   public double getMinEdgeFactor() {
      return prefs.getDouble(PREFS_MIN_EDGE_FACTOR, DEFAULT_MIN_EDGE_FACTOR);
   }

   public void setMinEdgeFactor(double value) {
      prefs.put(PREFS_MIN_EDGE_FACTOR, String.valueOf(value));
   }

   public double getMaxEdgeFactor() {
      return prefs.getDouble(PREFS_MAX_EDGE_FACTOR, DEFAULT_MAX_EDGE_FACTOR);
   }

   public void setMaxEdgeFactor(double value) {
      prefs.put(PREFS_MAX_EDGE_FACTOR, String.valueOf(value));
   }

   public double getScanGapFactor() {
      return prefs.getDouble(PREFS_SCAN_GAP_FACTOR, DEFAULT_SCAN_GAP_FACTOR);
   }

   public void setScanGapFactor(double value) {
      prefs.put(PREFS_SCAN_GAP_FACTOR, String.valueOf(value));
   }

   public long getSquareDeviation() {
      return prefs.getLong(PREFS_SQUARE_DEVIATION, DEFAULT_SQUARE_DEV);
   }

   public void setSquareDeviation(long value) {
      prefs.put(PREFS_SQUARE_DEVIATION, String.valueOf(value));
   }

   public long getEdgeThreshold() {
      return prefs.getLong(PREFS_EDGE_THRESHOLD, DEFAULT_EDGE_THRESH);
   }

   public void setEdgeThreshold(long value) {
      prefs.put(PREFS_EDGE_THRESHOLD, String.valueOf(value));
   }

   public long getDecoderCorrections() {
      return prefs.getLong(PREFS_CORRECTIONS, DEFAULT_CORRECTIONS);
   }

   public void setDecoderCorrections(long value) {
      prefs.put(PREFS_CORRECTIONS, String.valueOf(value));
   }

   //--

   private String geKeyForWellRectangle(PlateType plateType, String subKey) {
      StringBuffer buf = new StringBuffer();
      buf.append(plateType.name());
      buf.append("_");
      buf.append(subKey);
      return buf.toString();
   }

   private static class PlateDecoderPreferencesHolder {
      private static final PlateDecoderPreferences INSTANCE = new PlateDecoderPreferences();
   }

}
