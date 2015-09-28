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

public abstract class AbstractSceneRoot extends BorderPane {

    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(AbstractSceneRoot.class);

    protected PlateModel model;

    private final String title;

    private Button backBtn;

    private Button finishBtn;

    public AbstractSceneRoot(String title) {
        this.title = title;
        init();
        setTop(createTitle());
        setCenter(createContentsArea());
        setBottom(createBottomArea());
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

    private Node createContentsArea() {
        final Node node = creatContents();
        return node;
    }

    protected void init() {
        model = PlateModel.getInstance();
    }

    public abstract void onDisplay();

    protected abstract Node creatContents();

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

    public void enableBackAction(EventHandler<ActionEvent> actionHandler) {
        backBtn.setVisible(true);
        backBtn.setOnAction(actionHandler);
    }

    public void enableFinishAction(EventHandler<ActionEvent> actionHandler) {
        finishBtn.setVisible(true);
        finishBtn.setOnAction(actionHandler);
    }

}
