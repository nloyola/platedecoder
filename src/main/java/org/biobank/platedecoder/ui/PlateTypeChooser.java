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

    //@SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(PlateTypeChooser.class);

    private final PlateModel model = PlateModel.getInstance();

    public PlateTypeChooser() {
        Text text = new Text();
        text.setText("Plate type:");

        ChoiceBox<PlateType> choice = createPlateChoiceBox();

        addRow(0, text, choice);
        setMargin(text, new Insets(5));
        setMargin(choice, new Insets(5));
        choice.setValue(model.getPlateType());

        choice.getSelectionModel().selectedItemProperty()
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
