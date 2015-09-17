package org.biobank.platedecoder.model;

import java.util.Arrays;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlateModel {

    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(PlateModel.class);

    private Plate plate;

    public ObservableList<PlateType> plateTypes =
        FXCollections.observableArrayList(Arrays.asList(PlateType.values()));

    private ObjectProperty<PlateType> plateTypeProperty =
        new SimpleObjectProperty<PlateType>(PlateType.PT_96_WELLS);

    private ObjectProperty<PlateOrientation> plateOrientationProperty =
        new SimpleObjectProperty<PlateOrientation>(PlateOrientation.LANDSCAPE);

    private ObjectProperty<BarcodePosition> barcodePositionProperty =
        new SimpleObjectProperty<BarcodePosition>(BarcodePosition.BOTTOM);

    private PlateModel() {
        plateTypeProperty.addListener((observable, oldValue, newValue) -> {
                createNewPlate();
            });
        plateOrientationProperty.addListener((observable, oldValue, newValue) -> {
                createNewPlate();
            });
        barcodePositionProperty.addListener((observable, oldValue, newValue) -> {
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
