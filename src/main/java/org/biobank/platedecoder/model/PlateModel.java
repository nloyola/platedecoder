package org.biobank.platedecoder.model;

import java.util.Arrays;
import java.util.prefs.Preferences;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class PlateModel {

    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(PlateModel.class);

    private Plate plate;

    private Preferences prefs = Preferences.userNodeForPackage(PlateModel.class);

    private static final String PREFS_PLATE_TYPE = "PREFS_PLATE_TYPE";

    private static final String PREFS_PLATE_ORIENTATION = "PREFS_PLATE_ORIENTATION";

    private static final String PREFS_BARCODE_POSITION = "PREFS_BARCODE_POSITION";

    public ObservableList<PlateType> plateTypes =
        FXCollections.observableArrayList(Arrays.asList(PlateType.values()));

    private ObjectProperty<PlateType> plateTypeProperty;

    private ObjectProperty<PlateOrientation> plateOrientationProperty;

    private ObjectProperty<BarcodePosition> barcodePositionProperty;

    private PlateModel() {
        plateTypeProperty = new SimpleObjectProperty<PlateType>(
            PlateType.valueOf(prefs.get(PREFS_PLATE_TYPE, PlateType.PT_96_WELLS.name())));

        plateOrientationProperty = new SimpleObjectProperty<PlateOrientation>(
            PlateOrientation.valueOf(prefs.get(PREFS_PLATE_ORIENTATION,
                                               PlateOrientation.LANDSCAPE.name())));

        barcodePositionProperty = new SimpleObjectProperty<BarcodePosition>(
            BarcodePosition.valueOf(prefs.get(PREFS_BARCODE_POSITION,
                                                      BarcodePosition.BOTTOM.name())));

        plateTypeProperty.addListener((observable, oldValue, newValue) -> {
                prefs.put(PREFS_PLATE_TYPE, newValue.name());
                createNewPlate();
            });
        plateOrientationProperty.addListener((observable, oldValue, newValue) -> {
                prefs.put(PREFS_PLATE_ORIENTATION, newValue.name());
                createNewPlate();
            });
        barcodePositionProperty.addListener((observable, oldValue, newValue) -> {
                prefs.put(PREFS_BARCODE_POSITION, newValue.name());
                createNewPlate();
            });

        createNewPlate();
    }

    public static PlateModel getInstance() {
        return PlateModelHolder.INSTANCE;
    }

    public void setPlateType(PlateType plateType) {
        plateTypeProperty.setValue(plateType);
    }

    public PlateType getPlateType() {
        return plateTypeProperty.getValue();
    }

    public ObjectProperty<PlateType> getPlateTypeProperty() {
        return plateTypeProperty;
    }

    public void setPlateOrientation(PlateOrientation orientation) {
        plateOrientationProperty.setValue(orientation);
    }

    public PlateOrientation getPlateOrientation() {
        return plateOrientationProperty.getValue();
    }

    public ObjectProperty<PlateOrientation> getPlateOrientationProperty() {
        return plateOrientationProperty;
    }

    public void setBarcodePosition(BarcodePosition position) {
        barcodePositionProperty.setValue(position);
    }

    public BarcodePosition getBarcodePosition() {
        return barcodePositionProperty.getValue();
    }

    public ObjectProperty<BarcodePosition> getBarcodePositionProperty() {
        return barcodePositionProperty;
    }

    private void createNewPlate() {
        plate = new Plate(plateTypeProperty.getValue());
    }

    public Plate getPlate() {
        return plate;
    }

    private static class PlateModelHolder {
        private static final PlateModel INSTANCE = new PlateModel();
    }

}
