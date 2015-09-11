package org.biobank.platedecoder.ui;

import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: add instructions for user for how to use the "Decode" button
public class ImageAndGrid extends AbstractSceneRoot {

    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(ImageAndGrid.class);

    private PlateTypeChooser plateTypeChooser;

    private Optional<Image> imageMaybe;

    private ImageView imageView;

    private Group imageGroup;

    private ScrollPane imagePane;

    private BorderPane borderPane;

    private WellGrid wellGrid;

    private Label filenameLabel;

    public ImageAndGrid() {
        super("Align grid with barcodes");
    }

    @Override
    protected Node creatContents() {
        Pane controls = createControlsPane();
        Pane imagePane = createImagePane();

        borderPane = new BorderPane();
        borderPane.setPadding(new Insets(5, 5, 5, 5));
        borderPane.setLeft(controls);
        borderPane.setCenter(imagePane);
        return borderPane;
    }

    private Pane createControlsPane() {
        plateTypeChooser = new PlateTypeChooser();
        plateTypeChooser.addListenerToPlateTypeSelectionModel((observable, oldValue, newValue) -> {
                wellGrid.plateTypeSelectionChanged(newValue);
                addWellGrid();
            });

        Button decodeBtn = new Button("Decode");
        decodeBtn.setOnAction(e -> decodeImage());

        final AnchorPane anchorPane = new AnchorPane();
        AnchorPane.setTopAnchor(decodeBtn, 0.0);
        AnchorPane.setRightAnchor(decodeBtn, 0.0);
        anchorPane.getChildren().add(decodeBtn);

        GridPane grid = new GridPane();
        grid.add(plateTypeChooser, 0, 0);
        grid.add(anchorPane, 0, 1);

        GridPane.setMargin(anchorPane, new Insets(5));

        return grid;
    }

    private Pane createImagePane() {
        imageView = new ImageView();
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);

        imageGroup = new Group();
        imageGroup.getChildren().add(imageView);
        imagePane = new ScrollPane(imageGroup);

        filenameLabel = new Label("Filename:");

        GridPane grid = new GridPane();
        grid.add(imagePane, 0, 0);
        grid.add(filenameLabel, 0, 1);


        // subtract a few pixels so that scroll bars are not displayed
        imageView.fitWidthProperty().bind(grid.widthProperty().subtract(5));
        imageView.fitHeightProperty().bind(grid.heightProperty()
                                           .subtract(filenameLabel.heightProperty()).subtract(5));

        imageView.fitWidthProperty().addListener((observable, oldValue, newValue) -> {
                // the actual dimensions are in imageView.getLayoutBounds().getWidth()
                Image image = imageView.getImage();
                if (image != null) {
                    double newScale = imageView.getLayoutBounds().getWidth() / image.getWidth();
                    wellGrid.setScale(newScale);
                }
            });

        imageView.fitHeightProperty().addListener((observable, oldValue, newValue) -> {
                // the actual dimensions are in imageView.getLayoutBounds().getHeight()
                Image image = imageView.getImage();
                if (image != null) {
                    double newScale = imageView.getLayoutBounds().getHeight() / image.getHeight();
                    wellGrid.setScale(newScale);
                }
            });

        wellGrid = new WellGrid(imageGroup, imageView, plateTypeChooser.getSelection(), 0, 0, 1500, 1300);

        return grid;
    }

    @Override
    protected void onDisplay() {
    }

    public void setImageFileURI(String uri) {
        Image image = new Image(uri);
        imageView.setImage(image);
        imageView.setCache(true);

        filenameLabel.setText("Filename: " + uri);

        imageMaybe = Optional.of(image);
        addWellGrid();
    }

    private void addWellGrid() {
        imageMaybe.ifPresent(image -> {
                imageGroup.getChildren().clear();
                imageGroup.getChildren().add(imageView);
                imageGroup.getChildren().addAll(wellGrid.getWellRectangles());
                imageGroup.getChildren().addAll(wellGrid.getResizeControls());
            });
    }

    private void decodeImage() {
        LOG.debug("image grid dimensions: {}", wellGrid);
    }

}
