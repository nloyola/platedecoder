package org.biobank.platedecoder.model;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class PlateModel {

    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(PlateModel.class);

    private Plate plate;

    public ObservableList<PlateType> plateTypes =
        FXCollections.observableArrayList(Arrays.asList(PlateType.values()));

    private ObjectProperty<PlateType> plateTypeProperty;

    private ObjectProperty<PlateOrientation> plateOrientationProperty;

    private ObjectProperty<BarcodePosition> barcodePositionProperty;

    private PlateModel() {
        plateTypeProperty = new SimpleObjectProperty<PlateType>(
            PlateDecoderPreferences.getInstance().getPlateType());

        plateOrientationProperty = new SimpleObjectProperty<PlateOrientation>(
            PlateDecoderPreferences.getInstance().getPlateOrietation());

        barcodePositionProperty = new SimpleObjectProperty<BarcodePosition>(
            PlateDecoderPreferences.getInstance().getBarcodePosition());

        plateTypeProperty.addListener((observable, oldValue, newValue) -> {
                PlateDecoderPreferences.getInstance().setPlateType(newValue);
                createNewPlate();
            });
        plateOrientationProperty.addListener((observable, oldValue, newValue) -> {
                PlateDecoderPreferences.getInstance().setPlateOrientation(newValue);
                createNewPlate();
            });
        barcodePositionProperty.addListener((observable, oldValue, newValue) -> {
                PlateDecoderPreferences.getInstance().setBarcodePosition((newValue));
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

    public void createNewPlate() {
        plate = new Plate(plateTypeProperty.getValue());
    }

    public Plate getPlate() {
        return plate;
    }

    private static class PlateModelHolder {
        private static final PlateModel INSTANCE = new PlateModel();
    }

}
