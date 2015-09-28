package org.biobank.platedecoder.ui.scene;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import org.biobank.platedecoder.dmscanlib.CellRectangle;
import org.biobank.platedecoder.dmscanlib.DecodeOptions;
import org.biobank.platedecoder.dmscanlib.DecodeResult;
import org.biobank.platedecoder.dmscanlib.DecodedWell;
import org.biobank.platedecoder.dmscanlib.ScanLib;
import org.biobank.platedecoder.dmscanlib.ScanLibResult;
import org.biobank.platedecoder.model.BarcodePosition;
import org.biobank.platedecoder.model.Plate;
import org.biobank.platedecoder.model.PlateDecoderPreferences;
import org.biobank.platedecoder.model.PlateOrientation;
import org.biobank.platedecoder.ui.PlateTypeChooser;
import org.biobank.platedecoder.ui.wellgrid.WellGrid;
import org.controlsfx.dialog.ProgressDialog;
import org.controlsfx.tools.Borders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

// TODO: add instructions for user for how to use the "Decode" button
public class ImageAndGrid extends AbstractSceneRoot {

    //@SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(ImageAndGrid.class);

    private Optional<Image> imageMaybe;

    private URL imageUrl;

    private ImageView imageView;

    private String imageFilename;

    private Group imageGroup;

    private ScrollPane imagePane;

    private BorderPane borderPane;

    private WellGrid wellGrid;

    private Label filenameLabel;

    private Button continueBtn;

    private Optional<Set<DecodedWell>> decodedWellsMaybe = Optional.empty();

    private Optional<EventHandler<ActionEvent>> continueHandlerMaybe = Optional.empty();

    public ImageAndGrid() {
        super("Align grid with barcodes");

        model.getPlateTypeProperty().addListener((observable, oldValue, newValue) -> {
                LOG.debug("plate type changed: {}", newValue);
                createWellGrid();
            });

        model.getPlateOrientationProperty().addListener((observable, oldValue, newValue) -> {
                createWellGrid();
            });

        model.getBarcodePositionProperty().addListener((observable, oldValue, newValue) -> {
                createWellGrid();
            });
    }

    @Override
    public void onDisplay() {
        // since this method is also called when the user presses the "Back" button, it clears
        // previous decode information if present
        decodedWellsMaybe.ifPresent(decodedWells -> {
                decodedWellsMaybe = Optional.empty();
                model.createNewPlate();
            });

        wellGrid.clearWellCellInventoryId();
        wellGrid.update();
        updateDecodedWellCount(Collections.emptySet());
        continueBtn.setDisable(true);
    }

    /**
     * Creates a new well grid with the dimensions of the previous one.
     */
    private void createWellGrid() {
        Image image = imageView.getImage();
        if (image != null) {
            decodedWellsMaybe = Optional.empty();

            wellGrid = new WellGrid(imageGroup,
                                    imageView,
                                    model.getPlateType(),
                                    wellGrid.getX(),
                                    wellGrid.getY(),
                                    wellGrid.getWidth(),
                                    wellGrid.getHeight(),
                                    imageView.getLayoutBounds().getWidth() / image.getWidth());

            LOG.debug("new well grid created: decoded Wells: {}", decodedWellsMaybe);
            addWellGrid();
        }
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
        PlateTypeChooser plateTypeChooser = new PlateTypeChooser();

        GridPane grid = new GridPane();
        grid.add(plateTypeChooser, 0, 0);
        grid.add(createOrientationControls(), 0, 1);
        grid.add(createBarcodePisitionsControls(), 0, 2);
        grid.add(createDecodeButton(), 0, 3);
        grid.add(createContinueButton(), 0, 4);

        return grid;
    }

    private Node createOrientationControls() {
        final ToggleGroup toggleGroup = new ToggleGroup();

        RadioButton landscape = new RadioButton("Landscape");
        landscape.setToggleGroup(toggleGroup);
        RadioButton portrait = new RadioButton("Portrait");
        portrait.setToggleGroup(toggleGroup);

        final VBox orientationBox = new VBox(5, landscape, portrait);

        landscape.setSelected(
            model.getPlateOrientation().equals(PlateOrientation.LANDSCAPE));
        portrait.setSelected(
            model.getPlateOrientation().equals(PlateOrientation.PORTRAIT));

        toggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
                model.setPlateOrientation(newValue == landscape
                                          ? PlateOrientation.LANDSCAPE : PlateOrientation.PORTRAIT);
            });

        return Borders.wrap(orientationBox)
            .etchedBorder().title("Orientation").build()
            .build();
    }

    private Node createBarcodePisitionsControls() {
        final ToggleGroup toggleGroup = new ToggleGroup();

        RadioButton tubeTops = new RadioButton("Tube tops");
        tubeTops.setToggleGroup(toggleGroup);
        RadioButton tubeBottoms = new RadioButton("Tube bottoms");
        tubeBottoms.setToggleGroup(toggleGroup);

        final VBox orientationBox = new VBox(5, tubeTops, tubeBottoms);

        tubeTops.setSelected(model.getBarcodePosition().equals(BarcodePosition.TOP));
        tubeBottoms.setSelected(model.getBarcodePosition().equals(BarcodePosition.BOTTOM));

        toggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
                model.setBarcodePosition(newValue == tubeTops
                                          ? BarcodePosition.TOP : BarcodePosition.BOTTOM);
            });

        return Borders.wrap(orientationBox)
            .etchedBorder().title("Barcode Positions").build()
            .build();
    }

    private Node createDecodeButton() {
        Button button = new Button("Decode");
        button.setOnAction(this::decodeImageAction);

        final AnchorPane anchorPane = new AnchorPane();
        AnchorPane.setTopAnchor(button, 0.0);
        AnchorPane.setRightAnchor(button, 0.0);
        anchorPane.getChildren().add(button);

        GridPane.setMargin(anchorPane, new Insets(5));

        return anchorPane;
    }

    private Node createContinueButton() {
        continueBtn = new Button("Continue");
        continueBtn.setDisable(true);

        final AnchorPane anchorPane = new AnchorPane();
        AnchorPane.setTopAnchor(continueBtn, 0.0);
        AnchorPane.setRightAnchor(continueBtn, 0.0);
        anchorPane.getChildren().add(continueBtn);

        GridPane.setMargin(anchorPane, new Insets(5));

        continueBtn.setOnAction(this::continueButtonAction);

        return anchorPane;
    }

    private void continueButtonAction(ActionEvent event) {
        if (!decodedWellsMaybe.isPresent()) {
            throw new IllegalStateException("missing decoded wells");
        }

        // copy data to model
        Plate plate = model.getPlate();
        decodedWellsMaybe.ifPresent(decodedWells -> {
                for (DecodedWell well: decodedWells) {
                    plate.setWellInventoryId(well.getLabel(), well.getMessage());
                }

                LOG.debug("copying to model: plate wells: {}", plate.getWells().size());
            });

        // save current dimensions of plate to preferences so they are used next time
        PlateDecoderPreferences.getInstance().setWellRectangle(
            model.getPlateType(), wellGrid);
        continueHandlerMaybe.ifPresent(handler -> handler.handle(event));
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

        Rectangle r = PlateDecoderPreferences.getInstance().getWellRectangle(model.getPlateType());

        wellGrid = new WellGrid(imageGroup,
                                imageView,
                                model.getPlateType(),
                                r.getX(),
                                r.getY(),
                                r.getWidth(),
                                r.getHeight(),
                                1.0);

        return grid;
    }

    public void setImageFileURI(String urlString) {
        Image image = new Image(urlString);
        imageView.setImage(image);
        imageView.setCache(true);

        try {
            imageUrl = new URL(urlString);
            File file = new File(imageUrl.toURI());
            imageFilename = file.toString();
        } catch (MalformedURLException | URISyntaxException ex) {
            LOG.error(ex.getMessage());
            filenameLabel.setText("");
        } finally {
        }

        imageMaybe = Optional.of(image);
        addWellGrid();
    }

    private void addWellGrid() {
        imageMaybe.ifPresent(image -> {
                wellGrid.setScale(imageView.getLayoutBounds().getWidth() / image.getWidth());

                imageGroup.getChildren().clear();
                imageGroup.getChildren().add(imageView);
                imageGroup.getChildren().addAll(wellGrid.getWellCells());
                imageGroup.getChildren().addAll(wellGrid.getWellDecodedIcons());
                imageGroup.getChildren().addAll(wellGrid.getResizeControls());

                updateDecodedWellCount(Collections.emptySet());
            });
    }

    private String getFilenameFromImageUrl(URL url) {
        try {
            File file = new File(url.toURI());
            return file.toString();
        } catch (URISyntaxException ex) {
            throw new IllegalStateException("could not convert iamge URL to a filename");
        }
    }

    @SuppressWarnings("unused")
    private void decodeImageAction(ActionEvent e) {
        Task<DecodeResult> worker = new Task<DecodeResult>() {
                @Override
                protected DecodeResult call() throws Exception {
                    Set<CellRectangle> cells = CellRectangle.getCellsForBoundingBox(
                        wellGrid,
                        model.getPlateOrientation(),
                        model.getPlateType(),
                        model.getBarcodePosition());

                    DecodeResult result = ScanLib.getInstance().decodeImage(
                        0L,
                        getFilenameFromImageUrl(imageUrl),
                        DecodeOptions.getDefaultDecodeOptions(),
                        cells.toArray(new CellRectangle[] {}));
                    return result;
                }
            };

        ProgressDialog dlg = new ProgressDialog(worker);
        dlg.setTitle("Decoding image");
        dlg.setHeaderText("Decoding image");

        worker.setOnSucceeded(event -> {
                DecodeResult result = worker.getValue();

                if (result.getResultCode() == ScanLibResult.Result.SUCCESS) {
                    if (decodedWellsMaybe.isPresent()) {
                        Set<DecodedWell> prevDecodedWells = decodedWellsMaybe.get();
                        Set<DecodedWell> currentDecodedWells = result.getDecodedWells();

                        // compare this new result with the previous one
                        if (DecodeResult.compareDecodeResults(prevDecodedWells,
                                                              currentDecodedWells)) {
                            // merge the two results
                            prevDecodedWells.addAll(currentDecodedWells);
                            updateDecodedWellCount(prevDecodedWells);
                            decodedWellsMaybe = Optional.of(prevDecodedWells);

                        } else {
                            if (decodeMismatchErrorDialog()) {
                                decodedWellsMaybe = Optional.of(currentDecodedWells);
                                updateDecodedWellCount(currentDecodedWells);
                                wellGrid.clearWellCellInventoryId();
                            }
                        }
                    } else {
                        Set<DecodedWell> decodedWells = result.getDecodedWells();
                        decodedWellsMaybe = Optional.of(decodedWells);
                        updateDecodedWellCount(decodedWells);
                    }

                    decodedWellsMaybe.ifPresent(decodedWells -> {
                            for (DecodedWell well: decodedWells) {
                                wellGrid.setWellCellInventoryId(well.getLabel(), well.getMessage());
                            }
                            wellGrid.update();
                        });

                    LOG.debug("image decoded: decoded wells: {}", decodedWellsMaybe);
                    continueBtn.setDisable(false);
                }
            });

        worker.setOnFailed(event -> {
                LOG.error("The task failed.");
            });

        Thread th = new Thread(worker);
        th.setDaemon(true);
        th.start();
    }

    public void onContinueAction(EventHandler<ActionEvent> continueHandler) {
        continueHandlerMaybe = Optional.of(continueHandler);
    }

    private void updateDecodedWellCount(Set<DecodedWell> decodedWells) {
        StringBuffer buf = new StringBuffer();
        buf.append(imageFilename);
        if (!decodedWells.isEmpty()) {
            buf.append(", tubes decoded: ");
            buf.append(decodedWells.size());
        }
        filenameLabel.setText(buf.toString());
    }

    /**
     *
     * See http://code.makery.ch/blog/javafx-dialogs-official/ for dialog examples.
     *
     * @return TRUE if these results should be used instead. FALSE if they should be discarded.
     */
    private boolean decodeMismatchErrorDialog() {
        ButtonType buttonTypeUseResult = new ButtonType("Use this result instead");
        ButtonType buttonTypeDiscardResult = new ButtonType("Discard this result");

        // display error message to user
        Alert dlg = new Alert(AlertType.CONFIRMATION);
        dlg.setTitle("Decode Mismatch");
        dlg.getDialogPane().setHeaderText("The results from this decode do not match the previous results.");
        dlg.getDialogPane().setContentText("What would you like to do?");
        dlg.getButtonTypes().setAll(buttonTypeUseResult, buttonTypeDiscardResult);

        Optional<ButtonType> result = dlg.showAndWait();

        return (result.get() == buttonTypeUseResult);
    }

}

