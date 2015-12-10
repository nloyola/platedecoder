package org.biobank.platedecoder.ui.scene;

import static org.biobank.platedecoder.ui.JavaFxHelper.createButton;
import static org.biobank.platedecoder.ui.JavaFxHelper.createDialog;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;

public abstract class ConfigScene extends SceneRoot {

   //@SuppressWarnings("unused")
   private static final Logger LOG = LoggerFactory.getLogger(ConfigScene.class);

   private Button applyBtn;

   private boolean configChangesMade;

   public ConfigScene(String title) {
      super(title);
   }

   @Override
   public void onDisplay() {
      configChangesMade = false;
   }

   /**
    * Creates an pane with Apply and Restore buttons for scenes that save data to the mode.
    *
    * @see #applyAction applyAction
    * @see #restoreDefaultsAction restoreDefaultsAction
    *
    * @return The pane that holds the buttons.
    */
   protected Pane createApplyAndRestoreButtons() {
      TilePane pane = new TilePane();
      pane.setHgap(5);
      pane.setPadding(new Insets(5, 5, 5, 5));
      pane.setMinHeight(TilePane.USE_PREF_SIZE);
      pane.setAlignment(Pos.BOTTOM_RIGHT);

      applyBtn = createButton("Apply", e -> applyAction());
      Button restoreDefaultsBtn = createButton("Restore defaults", e -> restoreDefaultsAction());

      pane.getChildren().addAll(applyBtn, restoreDefaultsBtn);
      return pane;
   }

   /**
    * Sub classes that implement an apply action should override this method.
    *
    * <p>This method is usually used to save configuration information.
    */
   protected void applyAction() {
      configChangesMade = false;
   }

   /**
    * Sub classes that implement a restore action should override this method.
    *
    * <p>This method is usually used to restore configuration information.
    */
   protected void restoreDefaultsAction() {
   }

   /**
    * Sub classes should call this to flag that config changes have been made.
    *
    * <p>When config changes are made, a dialog is shown to the user when he / she tries to navigate
    * away from the window (by pressing the {@code Back} or {@code Next} buttons).
    *
    * @param changed Set to TRUE to flag that changes have been made to the configuration.
    */
   protected void setConfigChanged(boolean changed) {
      LOG.debug("setConfigChanged: value: {}", changed);
      configChangesMade = changed;
   }

/**
    * Sub classes should override this method if they want to prevent the apply or next actions from
    * taking place.
    *
    * <p>A good use for this is if the scene wants to remind the user to perform an Action
    * prior to pressing the next button.
    *
    * @return TRUE if the action should be allowed.
    */
   protected boolean allowNavigationAction() {
      LOG.debug("allowNavigationAction: configChangesMade: {}", configChangesMade);
      if (configChangesMade) {
         Alert alert = createDialog(
            AlertType.CONFIRMATION,
            "Unsaved changes",
            "Apply your changes?",
            "You have not applied the changes you made to these settings.");

         Optional<ButtonType> result = alert.showAndWait();
         if (result.isPresent() && result.get() == ButtonType.OK) {
            applyAction();
         }
      }
      return true;
   }

   /**
    * Used to enable or disable the {@code Apply} button.
    *
    * @param disable When TRUE the button is disabled.
    */
   protected void disableApplyButton(boolean disable) {
      applyBtn.setDisable(disable);
   }

   @Override
   protected boolean allowBackButtonAction() {
      return allowNavigationAction();
   }

   @Override
   protected boolean allowNextButtonAction() {
      return allowNavigationAction();
   }
}
