package org.biobank.platedecoder.ui.scene;

import static org.biobank.platedecoder.ui.JavaFxHelper.createButton;
import static org.biobank.platedecoder.ui.JavaFxHelper.createDialog;

import java.util.Optional;

import org.biobank.platedecoder.model.PlateModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

/**
 * A base class for displaying a window in the application.
 *
 * <p>The window has title are at the top of the window, and the remainder of the window is meant for
 * the subclass to fill with UI elements.
 *
 * <p>The application presents several windows to the user where he / she must select an action.
 * Some actions take the user to new windows.
 */
public abstract class SceneRoot extends BorderPane {

   @SuppressWarnings("unused")
   private static final Logger LOG = LoggerFactory.getLogger(SceneRoot.class);

   /**
    * A reference to the application's model where settings are stored.
    */
   protected PlateModel model = PlateModel.getInstance();

   private final String title;

   private Label titleAreaMessage;

   private Button backBtn;

   private Button nextBtn;

   private Button finishBtn;

   private Button applyBtn;

   private boolean configChangesMade;

   private Optional<Runnable> backButtonActionRunnableMaybe = Optional.empty();

   private Optional<Runnable> nextButtonActionRunnableMaybe = Optional.empty();

   /**
    * Creates a scene.
    *
    * <p>The {@code title} is displayed in the title area. The UI components are created by
    * overriding {@link #createContentsArea}.
    *
    * @param title  The title to display at the top of this scene.
    */
   public SceneRoot(String title) {
      this.title = title;
      setTop(createTitle());

      configChangesMade = false;

      Region contentsRegion = createContentsArea();
      setCenter(contentsRegion);

      setBottom(createBottomArea());
   }

   /**
    * Invoked on initialization so that the subclass can create it's UI elements.
    *
    * @return This method should return the root UI element. This element is then added to the scene.
    */
   protected abstract Region createContents();

   /**
    * Called when the scene is displayed.
    *
    * <p>The scen could be displayed for the first time as a result of an action in another scene,
    * or when the user presses the {@code Back} button in another scene.
    *
    * <p>This method is meant to refresh the UI elements.
    */
   public abstract void onDisplay();

   /**
    * Used to enable the {@code Back} button and bind a runnable to execute when the user presses
    * the button.
    *
    * <p>By default this button is disabled. The subclass should call this method if it wants to
    * display the button.
    *
    * @param runnable  The runnable to execute when this button is pressed.
    */
   public void enableBackAction(Runnable runnable) {
      backBtn.setVisible(true);
      backButtonActionRunnableMaybe = Optional.of(runnable);
   }

   /**
    * Used to enable the {@code Next} button and bind runnable to to execute when the user presses
    * the button.
    *
    * <p>By default this button is disabled. The subclass should call this method if it wants to
    * display the button.
    *
    * @param runnable  The runnable to execute when this button is pressed.
    */
   public void enableNextAction(Runnable runnable) {
      nextBtn.setVisible(true);
      nextButtonActionRunnableMaybe = Optional.of(runnable);
   }

   /**
    * Used to enable the {@code Finish} button and bind code to execute when the user presses the
    * button.
    *
    * <p>By default this button is disabled. The subclass should call this method if it wants to
    * display the button. Usually this button will terminate the application.
    *
    * @param runnable  The runnable to execute when this button is pressed.
    */
   public void enableFinishAction(Runnable runnable) {
      finishBtn.setVisible(true);
      finishBtn.setOnAction(e -> runnable.run());
   }

   /**
    * Used to disable or enable the next button.
    *
    * @param disable When TRUE the button is disabled.
    */
   protected void disableNextButton(boolean disable) {
      nextBtn.setDisable(disable);
   }

   /**
    * Sub classes should override this method if they want to prevent the back action from taking
    * place.
    *
    * <p>A good use for this is if the scene wants to remind the user to perform an Action
    * prior to pressing the back button.
    *
    * @return TRUE if the action should be allowed.
    */
   protected boolean allowBackButtonAction() {
      return allowNavigationAction();
   }

   /**
    * Sub classes should override this method if they want to prevent the next action from taking
    * place.
    *
    * <p>A good use for this is if the scene wants to remind the user to perform an Action
    * prior to pressing the next button.
    *
    * @return TRUE if the action should be allowed.
    */
   protected boolean allowNextButtonAction() {
      return allowNavigationAction();
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
    * The sub class may want finer control when the {@code Next} button is pressed.
    *
    * <p>This button allows for the action to be taken.
    */
   protected void performNextButtonAction() {
      nextButtonActionRunnableMaybe.ifPresent(runnable -> runnable.run());
   }

   /**
    * Used to display a text message under the title area.
    *
    * @param message The message to display.
    */
   protected void setTitleAreaMessage(String message) {
      titleAreaMessage.setText(message);
      titleAreaMessage.setWrapText(true);
      titleAreaMessage.setStyle("-fx-font-size:12; -fx-font-weight:normal;");
   }

   /**
    * Used to display an error message under the title area.
    *
    * <p>It is displayed with a red foreground color.
    *
    * @param message The message to display.
    */
   protected void setTitleAreaErrorMessage(String message) {
      titleAreaMessage.setText(message);
      titleAreaMessage.setStyle("-fx-text-fill: #ff1e26; -fx-font-size:12; -fx-font-weight:bold;");
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
      configChangesMade = changed;
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
    * Used to enable or disable the {@code Apply} button.
    *
    * @param disable When TRUE the button is disabled.
    */
   protected void disableApplyButton(boolean disable) {
      applyBtn.setDisable(disable);
   }

   /**
    * Sub classes that implement an apply action should override this method.
    *
    * <p>This method is usually used to save configuration information.
    */
   protected void applyAction() {
   }

   /**
    * Sub classes that implement a restore action should override this method.
    *
    * <p>This method is usually used to restore configuration information.
    */
   protected void restoreDefaultsAction() {
   }

   protected void nextButtonRequestFocus() {
      nextBtn.requestFocus();
   }

   private Region createContentsArea() {
      final Region node = createContents();
      return node;
   }

   private Node createTitle() {
      final Label titleLabel = new Label(title);
      titleLabel.setStyle("-fx-font-size:18; -fx-font-weight:bold;");

      titleAreaMessage = new Label();

      final VBox titleBox = new VBox(10);
      titleBox.setMaxHeight(Double.MAX_VALUE);
      titleBox.setPadding(new Insets(5, 5, 20, 5));
      titleBox.getChildren().add(titleLabel);
      titleBox.getChildren().add(titleAreaMessage);

      final ScrollPane scrollPane = new ScrollPane(titleBox);
      scrollPane.setMaxHeight(Double.MAX_VALUE);
      scrollPane.setFitToWidth(true);
      scrollPane.setFitToHeight(true);

      return scrollPane;
   }

   private Node createBottomArea() {
      final AnchorPane pane = new AnchorPane();
      pane.setPadding(new Insets(5, 5, 5, 5));

      backBtn = createButton("Back", this::backButtonAction);
      backBtn.managedProperty().bind(backBtn.visibleProperty());
      backBtn.setVisible(false);

      nextBtn = createButton("Next", this::nextButtonAction);
      nextBtn.managedProperty().bind(nextBtn.visibleProperty());
      nextBtn.setVisible(false);

      finishBtn = createButton("Finish");
      finishBtn.managedProperty().bind(finishBtn.visibleProperty());
      finishBtn.setVisible(false);

      HBox hbox = new HBox(5);
      hbox.getChildren().addAll(backBtn, nextBtn, finishBtn);

      AnchorPane.setTopAnchor(hbox, 0.0);
      AnchorPane.setRightAnchor(hbox, 0.0);

      pane.getChildren().add(hbox);

      final ScrollPane scrollPane = new ScrollPane(pane);
      scrollPane.setMaxHeight(Double.MAX_VALUE);
      scrollPane.setFitToWidth(true);
      scrollPane.setFitToHeight(true);
      return scrollPane;
   }

   private void backButtonAction(@SuppressWarnings("unused") ActionEvent event) {
      backButtonActionRunnableMaybe.ifPresent(runnable -> {
            if (allowBackButtonAction()) {
               runnable.run();
            }
         });
   }

   private void nextButtonAction(@SuppressWarnings("unused") ActionEvent event) {
      nextButtonActionRunnableMaybe.ifPresent(runnable -> {
            if (allowNextButtonAction()) {
               runnable.run();
            }
         });
   }

}
