package org.biobank.platedecoder.ui;

import org.biobank.platedecoder.model.PlateModel;
import org.controlsfx.tools.Borders;

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
    */
   @SuppressWarnings("unchecked")
   public RadioButtonChooser(String title) {
      super();

      toggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (toggleGroup.getSelectedToggle() != null) {
               setValue((T) newValue.getUserData());
            }
         });

      Node border = Borders.wrap(toggleGroupContainer).etchedBorder().title(title).build().build();
      getChildren().add(border);
   }

   /**
    * This method is called when the user selects one of the radio buttons.
    *
    * <p>Subclasses must implement this method to record the user's selection.
    *
    * @param obj The object that represents the selection.
    */
   protected abstract void setValue(T obj);

   /**
    * Subclasses should call this method to add radio buttons to the widget.
    *
    * <p>When the button is selected by the user, {@link #setValue setValue} is invoked with {@code
    * userDataObj}.
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
