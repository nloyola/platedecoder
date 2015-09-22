package org.biobank.platedecoder.ui;

import org.biobank.platedecoder.model.PlateModel;
import org.biobank.platedecoder.model.PlateType;

import javafx.geometry.Insets;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Uses a GridPane so that label and choice box are centered vertically.
 */
public class PlateTypeChooser extends GridPane {

    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(PlateTypeChooser.class);

    private final PlateModel model = PlateModel.getInstance();

    public PlateTypeChooser() {
        Text plateTypeText = new Text();
        plateTypeText.setText("Plate type:");

        ChoiceBox<PlateType> plateTypeChoice = createPlateChoiceBox();

        addRow(0, plateTypeText, plateTypeChoice);
        setMargin(plateTypeText, new Insets(5));
        setMargin(plateTypeChoice, new Insets(5));
        plateTypeChoice.setValue(model.getPlateType());

        plateTypeChoice.getSelectionModel().selectedItemProperty()
            .addListener((observable, oldValue, newValue) -> {
                    LOG.debug("plate type changed: {}", newValue);
                    model.setPlateType(newValue);
                });
    }

    private ChoiceBox<PlateType> createPlateChoiceBox() {
        ChoiceBox<PlateType> choiceBox = new ChoiceBox<PlateType>();
        choiceBox.getItems().setAll(model.plateTypes);
        return choiceBox;
    }

}
