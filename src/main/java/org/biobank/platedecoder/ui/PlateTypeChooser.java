package org.biobank.platedecoder.ui;

import org.biobank.platedecoder.model.PlateModel;
import org.biobank.platedecoder.model.PlateTypes;

import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

/**
 * Uses a GridPane so that label and choice box are centered vertically.
 */
public class PlateTypeChooser extends GridPane {

    private final PlateModel model = PlateModel.getInstance();

    public PlateTypeChooser() {
        Text plateTypeText = new Text();
        plateTypeText.setText("Plate type:");

        ChoiceBox<PlateTypes> plateTypeChoice = createPlateChoiceBox();

        addRow(0, plateTypeText, plateTypeChoice);
        setMargin(plateTypeText, new Insets(5));
        setMargin(plateTypeChoice, new Insets(5));

        model.setPlateTypeSelectionModel(plateTypeChoice.getSelectionModel());
        plateTypeChoice.getSelectionModel().selectFirst();
    }

    private ChoiceBox<PlateTypes> createPlateChoiceBox() {
        ChoiceBox<PlateTypes> result = new ChoiceBox<PlateTypes>();
        result.setItems(model.plateTypes);
        return result;
    }

    public void addListenerToPlateTypeSelectionModel(ChangeListener<PlateTypes> l) {
        model.getPlateTypeSelectionModel()
            .selectedItemProperty()
            .addListener(l);
    }

    public PlateTypes getSelection() {
        return model.getPlateTypeSelectionModel().selectedItemProperty().get();
    }
}
