package org.biobank.platedecoder.ui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;

public class JavaFxHelper {

   /**
    * Creates a button with the given label and action handler.
    *
    * @param label The string to display in the button.
    *
    * @param handler The handler to be invoked when the user presses the button.
    *
    * @return The button that is created.
    */
   public static Button createButton(String label, EventHandler<ActionEvent> handler) {
      Button button = new Button(label);
      if (handler != null) {
         button.setOnAction(handler);
      }
      button.setMaxWidth(Double.MAX_VALUE);
      button.setMinWidth(Button.USE_PREF_SIZE);
      button.defaultButtonProperty().bind(button.focusedProperty());
      return button;
   }

   /**
    * Creates a button with the given label.
    *
    * @param label The string to display in the button.
    *
    * @return The button that is created.
    */
   public static Button createButton(String label) {
      return createButton(label, null);
   }

   /**
    * Displays an alert dialog.
    *
    * @param alertType The type of alert dialog to display.
    *
    * @param titleBar the string to display in the dialog box's title bar.
    *
    * @param headerMessage the string to display in the title are of the dialog box. When this value
    *                      is null, the dialog will not have a title area.
    *
    * @param infoMessage the string to display in the body of the dialog box.
    *
    * @return the dialog box.
    */
   public static Alert createDialog(Alert.AlertType alertType,
                                    String titleBar,
                                    String headerMessage,
                                    String infoMessage) {
      Alert alert = new Alert(alertType);
      alert.setTitle(titleBar);
      alert.setHeaderText(headerMessage);
      alert.setContentText(infoMessage);

      if (PlateDecoder.IS_LINUX) {
         // FIXME: Remove after release 8u40
         alert.setResizable(true);
         alert.getDialogPane().setPrefSize(480, 320);
      }

      return alert;
   }

   /**
    * Displays an information dialog.
    *
    * @param titleBar the string to display in the dialog box's title bar.
    *
    * @param headerMessage the string to display in the title are of the dialog box. When this value
    *                      is null, the dialog will not have a title area.
    *
    * @param infoMessage the string to display in the body of the dialog box.
    *
    */
   public static void infoDialog(String titleBar,
                                 String headerMessage,
                                 String infoMessage) {
      Alert alert = createDialog(AlertType.INFORMATION, titleBar, headerMessage, infoMessage);
      alert.showAndWait();
   }

   /**
    * Displays an information dialog without a title are.
    *
    * @param titleBar the string to display in the dialog box's title bar.
    *
    * @param infoMessage the string to display in the body of the dialog box.
    *
    */
   public static void infoDialog(String infoMessage, String titleBar) {
      // By specifying a null headerMessage String, we cause the dialog to not have a header
      Alert alert = createDialog(AlertType.INFORMATION, titleBar, null, infoMessage);
      alert.showAndWait();
   }

   /**
    * Displays an error dialog.
    *
    * @param titleBar the string to display in the dialog box's title bar.
    *
    * @param headerMessage the string to display in the title are of the dialog box. When this value
    *                      is null, the dialog will not have a title area.
    *
    * @param infoMessage the string to display in the body of the dialog box.
    *
    */
   public static void errorDialog(String infoMessage,
                                  String titleBar,
                                  String headerMessage) {
      Alert alert = createDialog(AlertType.ERROR, titleBar, headerMessage, infoMessage);
      alert.showAndWait();
   }

}
