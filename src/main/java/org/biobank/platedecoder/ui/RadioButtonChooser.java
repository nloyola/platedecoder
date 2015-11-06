package org.biobank.platedecoder.ui;

import org.biobank.platedecoder.model.PlateModel;
import org.controlsfx.tools.Borders;

import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;

/**
 * A widget that allows the user to select a value from a group of radio buttons.
 */
public abstract class RadioButtonChooser<T> extends VBox {

   protected final PlateModel model = PlateModel.getInstance();

   private final ToggleGroup toggleGroup = new ToggleGroup();

   private final VBox toggleGroupContainer = new VBox(5);

   /**
    * Creates a widget that presents several options in a radio button group wrapped in a border.
    *
    * <p>The user can select one of these options.
    *
    * @param title The label to display in the border that surrounds the radio buttons.
    *
    * @param property The property that should be updated when the user selects a radio button.
    */
   @SuppressWarnings("unchecked")
   public RadioButtonChooser(String title, ObjectProperty<T> property) {
      super();

      toggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (toggleGroup.getSelectedToggle() != null) {
               property.setValue((T) newValue.getUserData());
            }
         });

      property.addListener((observable, oldValue, newValue) -> {
            for (Node node : toggleGroupContainer.getChildren()) {
               if (node instanceof RadioButton) {
                  RadioButton button = (RadioButton) node;
                  button.setSelected(button.getUserData() == newValue);
               }
            }
         });

      Node border = Borders.wrap(toggleGroupContainer).etchedBorder().title(title).build().build();
      getChildren().add(border);
   }

   /**
    * Subclasses should call this method to add radio buttons to the widget.
    *
    * <p>When the button is selected by the user, the property given in the call to the constructor
    * is assigned the new value.
    *
    * @param label The string to be displayed with this button.
    *
    * @param userDataObj The object to associated with this button.
    *
    * @param isSelected The initial selection state for this button.
    */
   protected void addButton(String label, T userDataObj, boolean isSelected) {
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
