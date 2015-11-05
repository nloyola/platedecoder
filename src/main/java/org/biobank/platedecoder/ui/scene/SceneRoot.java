package org.biobank.platedecoder.ui.scene;

import org.biobank.platedecoder.model.PlateModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
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

    private Button backBtn;

    private Button finishBtn;

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
        setCenter(createContentsArea());
        setBottom(createBottomArea());
    }

    private Node createContentsArea() {
        final Node node = creatContents();
        return node;
    }

    /**
     * Invoked on initialization so that the subclass can create it's UI elements.
     *
     * @return This method should return the root UI element. This element is then added to the scene.
     */
    protected abstract Node creatContents();

    /**
     * Called when the scene is diplayed.
     *
     * <p>The scen could be displayed for the first time as a result of an action in another scene,
     * or when the user presses the {@code Back} button in another scene.
     *
     * <p>This method is meant to refresh the UI elements.
     */
    public abstract void onDisplay();

    /**
     * Used to enable the {@code Finish} button and bind code to execute when the user presses the
     * button.
     *
     * <p>By default this button is disabled. The subclass should call this method if it wants to
     * display the button.
     *
     * @param actionHandler  The block of code to execute when this button is pressed.
     */
    public void enableBackAction(EventHandler<ActionEvent> actionHandler) {
        backBtn.setVisible(true);
        backBtn.setOnAction(actionHandler);
    }

    /**
     * Used to enable the {@code Finish} button and bind code to execute when the user presses the
     * button.
     *
     * <p>By default this button is disabled. The subclass should call this method if it wants to
     * display the button. Usually this button will terminate the application.
     *
     * @param actionHandler  The block of code to execute when this button is pressed.
     */
    public void enableFinishAction(EventHandler<ActionEvent> actionHandler) {
        finishBtn.setVisible(true);
        finishBtn.setOnAction(actionHandler);
    }

    private Node createTitle() {
        final VBox titleBox = new VBox();
        titleBox.setMaxHeight(Double.MAX_VALUE);
        titleBox.setPadding(new Insets(5, 5, 20, 5));

        final Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size:18; -fx-font-weight:bold;");
        titleBox.getChildren().add(titleLabel);

        final ScrollPane scrollPane = new ScrollPane(titleBox);
        scrollPane.setMaxHeight(Double.MAX_VALUE);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        return scrollPane;
    }

    private Node createBottomArea() {
        final AnchorPane pane = new AnchorPane();
        pane.setPadding(new Insets(5, 5, 5, 5));

        backBtn = new Button("Back");
        backBtn.managedProperty().bind(backBtn.visibleProperty());
        backBtn.setVisible(false);

        finishBtn = new Button("Finish");
        finishBtn.managedProperty().bind(finishBtn.visibleProperty());
        finishBtn.setVisible(false);

        HBox hbox = new HBox(5);
        hbox.getChildren().addAll(backBtn, finishBtn);

        AnchorPane.setTopAnchor(hbox, 0.0);
        AnchorPane.setRightAnchor(hbox, 0.0);

        pane.getChildren().add(hbox);

        final ScrollPane scrollPane = new ScrollPane(pane);
        scrollPane.setMaxHeight(Double.MAX_VALUE);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        return scrollPane;
    }

}
