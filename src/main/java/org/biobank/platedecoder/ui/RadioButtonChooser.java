package org.biobank.platedecoder.ui;

import org.biobank.platedecoder.model.PlateModel;
import org.controlsfx.tools.Borders;

import javafx.scene.Node;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;

/**
 * A widget that allows the user to select a value from a group of radio buttons.
 *
 * The user's  election is saved to PlateModel.
 */
public abstract class RadioButtonChooser extends VBox {

    protected final PlateModel model = PlateModel.getInstance();

    private final ToggleGroup toggleGroup = new ToggleGroup();

    private final VBox toggleGroupContainer = new VBox(5);

    /**
     * @param title The label to display in the border that surrounds the radio buttons.
     */
    public RadioButtonChooser(String title) {
        super();

        toggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
                if (toggleGroup.getSelectedToggle() != null) {
                    setValue(newValue.getUserData());
                }
            });

        Node border = Borders.wrap(toggleGroupContainer).etchedBorder().title(title).build()
            .build();

        getChildren().add(border);
    }

    protected abstract void setValue(Object obj);

    protected void addButton(String label, Object userDataObj, boolean isSelected) {
        RadioButton button = new RadioButton(label);
        button.setUserData(userDataObj);

        if (isSelected) {
            button.setSelected(true);
            button.requestFocus();
        }

        button.setToggleGroup(toggleGroup);
        toggleGroupContainer.getChildren().add(button);
    }

}
