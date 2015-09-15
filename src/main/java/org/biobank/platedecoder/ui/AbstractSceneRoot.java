package org.biobank.platedecoder.ui;

import org.biobank.platedecoder.model.PlateModel;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public abstract class AbstractSceneRoot extends BorderPane {

    protected final PlateModel model = PlateModel.getInstance();

    private final String title;

    private Button backBtn;

    public AbstractSceneRoot(String title, boolean hasBackButton) {
        this.title = title;
        setTop(createTitle());
        setCenter(createContentsArea());
        if (hasBackButton) {
            setBottom(createBottomArea());
        }
    }

    public AbstractSceneRoot(String title) {
        this(title, true);
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

    protected abstract void onDisplay();

    protected abstract Node creatContents();

    private Node createBottomArea() {
        final AnchorPane pane = new AnchorPane();
        pane.setPadding(new Insets(5, 5, 5, 5));

        backBtn = new Button("Back");
        backBtn.setDisable(true);

        AnchorPane.setTopAnchor(backBtn, 0.0);
        AnchorPane.setRightAnchor(backBtn, 0.0);
        pane.getChildren().add(backBtn);

        final ScrollPane scrollPane = new ScrollPane(pane);
        scrollPane.setMaxHeight(Double.MAX_VALUE);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        return scrollPane;
    }

    public void onBackAction(EventHandler<ActionEvent> backActionHandler) {
        backBtn.setDisable(false);
        backBtn.setOnAction(backActionHandler);
    }

}
