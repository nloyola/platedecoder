package org.biobank.platedecoder.ui;

import org.biobank.platedecoder.model.PlateModel;
import org.biobank.platedecoder.model.PlateTypes;

import javafx.beans.value.ChangeListener;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;

public class PlateTypeChooser extends HBox {

    private final PlateModel model = PlateModel.getInstance();

    public PlateTypeChooser() {
        super(8);

        Text plateTypeText = new Text();
        plateTypeText.setText("Plate type:");

        ChoiceBox<PlateTypes> plateTypeChoice = createPlateChoiceBox();

        getChildren().addAll(plateTypeText, plateTypeChoice);

        model.setPlateTypeSelectionModel(plateTypeChoice.getSelectionModel());
        plateTypeChoice.getSelectionModel().selectFirst();
    }

    private ChoiceBox<PlateTypes> createPlateChoiceBox() {
        ChoiceBox<PlateTypes> result = new ChoiceBox<PlateTypes>();
        result.setItems(model.plateTypes);
        //result.setStyle("-fx-font: 12px \"Verdana\"");
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
