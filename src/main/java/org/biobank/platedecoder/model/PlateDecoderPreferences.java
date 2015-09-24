package org.biobank.platedecoder.model;

import static org.biobank.platedecoder.model.PlateDecoderPreferences.PlateDecoderDefaults.*;

import java.util.prefs.Preferences;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.geometry.Point2D;
import javafx.scene.shape.Rectangle;

/**
 * Preferences in Linux are in "$HOME/.java/.userPrefs/" and then look for the package name.
 *
 * For preferences storage see:
 *
 *   https://blogs.oracle.com/CoreJavaTechTips/entry/the_preferences_api
 *
 *   http://www.davidc.net/programming/java/java-preferences-using-file-backing-store
 *
 *   http://stackoverflow.com/questions/208231/is-there-a-way-to-use-java-util-preferences-under-windows-without-it-using-the-r/208289#208289
 */
public class PlateDecoderPreferences {

    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(PlateDecoderPreferences.class);

    private final Preferences prefs;

    private static final String PREFS_APP_WINDOW_WIDTH = "PREFS_APP_WINDOW_WIDTH";

    private static final String PREFS_APP_WINDOW_HEIGHT = "PREFS_APP_WINDOW_HEIGHT";

    private static final String PREFS_WELL_GRID_X = "PREFS_WELL_GRID_X";

    private static final String PREFS_WELL_GRID_Y = "PREFS_WELL_GRID_Y_";

    private static final String PREFS_WELL_GRID_WIDTH = "PREFS_WELL_GRID_WIDTH";

    private static final String PREFS_WELL_GRID_HEIGHT = "PREFS_WELL_GRID_HEIGHT";

    private static final String PREFS_PLATE_TYPE = "PREFS_PLATE_TYPE";

    private static final String PREFS_PLATE_ORIENTATION = "PREFS_PLATE_ORIENTATION";

    private static final String PREFS_BARCODE_POSITION = "PREFS_BARCODE_POSITION";

    public static PlateDecoderPreferences getInstance() {
        return PlateDecoderPreferencesHolder.INSTANCE;
    }

    private PlateDecoderPreferences() {
        prefs = Preferences.userNodeForPackage(PlateDecoderPreferences.class);
    }

    public Point2D getAppWindowSize() {
        double width  = prefs.getDouble(PREFS_APP_WINDOW_WIDTH, DEFAULT_APP_WINDOW_SIZE[0]);
        double height = prefs.getDouble(PREFS_APP_WINDOW_HEIGHT, DEFAULT_APP_WINDOW_SIZE[1]);
        return new Point2D(width, height);
    }

    public void setAppWindowSize(double width, double height) {
        prefs.putDouble(PREFS_APP_WINDOW_WIDTH,  width);
        prefs.putDouble(PREFS_APP_WINDOW_HEIGHT, height);
    }

    private String geKeyForWellRectangle(PlateType plateType, String subKey) {
        StringBuffer buf = new StringBuffer();
        buf.append(plateType.name());
        buf.append("_");
        buf.append(subKey);
        return buf.toString();
    }

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

    public void setWellRectangle(PlateType plateType, Rectangle grid) {
        prefs.putDouble(geKeyForWellRectangle(plateType, PREFS_WELL_GRID_X), grid.getX());
        prefs.putDouble(geKeyForWellRectangle(plateType, PREFS_WELL_GRID_Y), grid.getY());
        prefs.putDouble(geKeyForWellRectangle(plateType, PREFS_WELL_GRID_WIDTH), grid.getWidth());
        prefs.putDouble(geKeyForWellRectangle(plateType, PREFS_WELL_GRID_HEIGHT), grid.getHeight());
    }

    public PlateType getPlateType() {
        return PlateType.valueOf(prefs.get(PREFS_PLATE_TYPE, DEFAULT_PLATE_TYPE));
    }

    public void setPlateType(PlateType newValue) {
        prefs.put(PREFS_PLATE_TYPE, newValue.name());
    }

    public PlateOrientation getPlateOrietation() {
        return PlateOrientation.valueOf(prefs.get(PREFS_PLATE_ORIENTATION, DEFAULT_PLATE_ORIENTATION));
    }

    public void setPlateOrientation(PlateOrientation newValue) {
        prefs.put(PREFS_PLATE_ORIENTATION, newValue.name());
    }

    public BarcodePosition getBarcodePosition() {
        return BarcodePosition.valueOf(prefs.get(PREFS_BARCODE_POSITION, DEFAULT_BARCODE_POSITION));
    }

    public void setBarcodePosition(BarcodePosition newValue) {
        prefs.put(PREFS_BARCODE_POSITION, newValue.name());
    }

     private static class PlateDecoderPreferencesHolder {
        private static final PlateDecoderPreferences INSTANCE = new PlateDecoderPreferences();
    }

    public static class PlateDecoderDefaults {

        public static final double [] DEFAULT_WELL_GRID = new double [] {
            0, 0, 1500, 1000
        };

        public static final double [] DEFAULT_APP_WINDOW_SIZE = new double [] {
            1000, 500
        };

        public static final String DEFAULT_PLATE_TYPE = PlateType.PT_96_WELLS.name();

        public static final String DEFAULT_PLATE_ORIENTATION = PlateOrientation.LANDSCAPE.name();

        public static final String DEFAULT_BARCODE_POSITION = BarcodePosition.BOTTOM.name();
    }
}
