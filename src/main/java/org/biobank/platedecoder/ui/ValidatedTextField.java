package org.biobank.platedecoder.ui;

import javafx.beans.value.ChangeListener;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * A text field that includes a validator.
 */
public class ValidatedTextField extends TextField {

   private static final Image invalidValueImage =
      new Image(ValidatedTextField.class.getResourceAsStream("invalid.png"));

   private final ImageView errorImageView;

   /**
    * A text field that includes a validator.
    *
    * <p>If the validator fails then an image is displayed after the text field to show that the
    * validation failed.
    *
    */
   public ValidatedTextField() {
      errorImageView = new ImageView(invalidValueImage);
      errorImageView.setPreserveRatio(true);
      errorImageView.setSmooth(true);
      errorImageView.setVisible(false);
   }

   /**
    * Makes the error image visible or invisible.
    *
    * @param visible When TRUE the image is visible.
    */
   public void setErrorImageVisible(boolean visible) {
      errorImageView.setVisible(visible);
   }

   /**
    * Returns the image view.
    *
    * <p>This is usually called to add the image to the UI.
    *
    * @return The image view that contains the error icon.
    */
   public ImageView getErrorImageView() {
      return errorImageView;
   }

   /**
    * Adds a listener that is called when the value in the text field changes.
    *
    * @param listener The listener to be notified when the value changes.
    */
   public void addListener(ChangeListener<String> listener) {
      textProperty().addListener(listener);
   }
}
