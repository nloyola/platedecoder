package org.biobank.platedecoder.model;

import java.util.Arrays;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.SingleSelectionModel;

public class PlateModel {

    private Plate plate;

    private SingleSelectionModel<PlateTypes> plateTypeSelectionModel;

    public ObservableList<PlateTypes> plateTypes =
        FXCollections.observableArrayList(Arrays.asList(PlateTypes.values()));

    private PlateModel() {
    }

    public static PlateModel getInstance() {
        return PlateModelHolder.INSTANCE;
    }

    public Plate getPlate() {
        return plate;
    }

    public void setPlateTypeSelectionModel(SingleSelectionModel<PlateTypes> selectionModel) {
        this.plateTypeSelectionModel = selectionModel;
        this.plateTypeSelectionModel.selectedItemProperty().addListener((Observable o) -> {
                PlateTypes selection = PlateModel.getInstance().getPlateTypeSelection();
                createNewPlate(selection);
            });
    }

    public SingleSelectionModel<PlateTypes> getPlateTypeSelectionModel() {
        return plateTypeSelectionModel;
    }

    public PlateTypes getPlateTypeSelection() {
        return plateTypeSelectionModel.selectedItemProperty().getValue();
    }

    private void createNewPlate(PlateTypes plateType) {
        plate = new Plate(plateType);
    }

    private static class PlateModelHolder {
        private static final PlateModel INSTANCE = new PlateModel();
    }

}

