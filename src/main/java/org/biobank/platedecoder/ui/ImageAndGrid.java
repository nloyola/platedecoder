package org.biobank.platedecoder.ui;

import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImageAndGrid extends AbstractSceneRoot {

    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(ImageAndGrid.class);

    private static final double IMAGE_FIT_WIDTH = 580;

    private PlateTypeChooser plateTypeChooser;

    private Image image;

    private ImageView imageView;

    private Group scrollGroup;

    private ScrollPane scrollPane;

    private BorderPane borderPane;

    private WellGrid wellGrid;

    public ImageAndGrid() {
        super("Align grid with barcodes");
    }

    @Override
    protected Node creatContents() {
        borderPane = new BorderPane();
        borderPane.setPadding(new Insets(20, 5, 5, 5));

        plateTypeChooser = new PlateTypeChooser();
        plateTypeChooser.addListenerToPlateTypeSelectionModel((observable, oldValue, newValue) -> {
                wellGrid.plateTypeSelectionChanged(newValue);
                addWellGrid();
            });

        imageView = new ImageView();
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);

        scrollGroup = new Group();
        scrollGroup.getChildren().add(imageView);
        scrollPane = new ScrollPane(scrollGroup);

        borderPane.setLeft(plateTypeChooser);
        borderPane.setCenter(scrollPane);

        wellGrid = new WellGrid(scrollGroup, imageView, plateTypeChooser.getSelection(), 0, 0, 500, 300);

        return borderPane;
    }

    @Override
    protected void onDisplay() {
    }

    public void setImageFileURI(String uri) {
        image = new Image(uri);
        imageView.setImage(image);
        imageView.setCache(true);
        imageView.setFitWidth(IMAGE_FIT_WIDTH);
        double ratio = IMAGE_FIT_WIDTH / image.getWidth();
        imageView.setFitHeight(image.getHeight() * ratio);

        addWellGrid();
    }

    private void addWellGrid() {
        if (image == null) return;

        scrollGroup.getChildren().clear();
        scrollGroup.getChildren().add(imageView);
        scrollGroup.getChildren().addAll(wellGrid.getWellRectangles());
        scrollGroup.getChildren().addAll(wellGrid.getResizeControls());
    }

}
