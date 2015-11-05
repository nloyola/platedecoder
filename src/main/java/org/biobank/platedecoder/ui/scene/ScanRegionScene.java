package org.biobank.platedecoder.ui.scene;

import java.util.Optional;

import org.biobank.platedecoder.dmscanlib.ScanLibResult;
import org.biobank.platedecoder.model.PlateDecoderDefaults;
import org.biobank.platedecoder.model.PlateDecoderPreferences;
import org.biobank.platedecoder.service.ScanRegionTask;
import org.biobank.platedecoder.ui.PlateDecoder;
import org.biobank.platedecoder.ui.ZoomingPane;
import org.biobank.platedecoder.ui.scanregion.ScanRegion;
import org.biobank.platedecoder.ui.scanregion.ScanRegionHandler;
import org.controlsfx.dialog.ProgressDialog;
import org.controlsfx.tools.Borders;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScanRegionScene extends SceneRoot implements ScanRegionHandler {

    //@SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(ScanRegionScene.class);

    private ImageView imageView;

    private Group imageGroup;

    private ScanRegion scanRegion;

    private Button continueButton;

    private Optional<EventHandler<ActionEvent>> continueHandlerMaybe = Optional.empty();

    public ScanRegionScene() {
        super("Define scanning region");
    }

    @Override
    public void onDisplay() {
        createScanRegion();
        continueButton.setDisable(true);
    }

    @Override
    protected Node creatContents() {
        Node controls = createControlsPane();
        Node imagePane = createImagePane();

        BorderPane borderPane = new BorderPane();
        borderPane.setPadding(new Insets(5, 5, 5, 5));
        borderPane.setLeft(controls);
        borderPane.setCenter(imagePane);

        BorderPane.setMargin(controls, new Insets(5));

        return borderPane;
    }

    private Node createControlsPane() {
        Button scanButton = new Button("Scan");
        scanButton.setOnAction(this::scanAction);
        scanButton.setMaxWidth(Double.MAX_VALUE);

        continueButton = new Button("Continue");
        continueButton.setOnAction(this::continueAction);
        continueButton.setMaxWidth(Double.MAX_VALUE);

        HBox hbox = new HBox(5);
        hbox.setPadding(new Insets(0, 20, 10, 20));
        hbox.getChildren().addAll(scanButton, continueButton);

        GridPane grid = new GridPane();
        grid.setVgap(2);
        grid.setHgap(2);
        grid.add(createInstructionsArea(), 0, 0);
        grid.add(hbox, 0, 1);

        return grid;
    }

    private Node createInstructionsArea() {
        StringBuffer buf = new StringBuffer();
        buf.append("Place a plate on your flatbed scanner and then press the scan button.\n\n");
        buf.append("Align the rectangle so that it contains all the tubes on the plate.\n\n");
        buf.append("Use the scroll wheel on the mouse to zoom into / out of the image.\n\n");
        buf.append("Once aligned press the continue button.");

        Text text = new Text();
        text.setText(buf.toString());
        text.setWrappingWidth(200);

        return Borders.wrap(text)
            .etchedBorder().build()
            .build();
    }

    private Node createImagePane() {
        imageView = new ImageView();
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);

        imageGroup = new Group();
        imageGroup.getChildren().add(imageView);
        ZoomingPane imagePane = new ZoomingPane(imageGroup);

        imageView.fitWidthProperty().bind(imagePane.widthProperty());
        imageView.fitHeightProperty().bind(imagePane.heightProperty());

        imageView.fitWidthProperty().addListener((observable, oldValue, newValue) -> {
                // the actual dimensions are in imageView.getLayoutBounds().getWidth()
                Image image = imageView.getImage();
                if (image != null) {
                    double newScale = imageView.getLayoutBounds().getWidth() / image.getWidth();
                    scanRegion.setDisplayScale(newScale);
                }
            });

        imageView.fitHeightProperty().addListener((observable, oldValue, newValue) -> {
                // the actual dimensions are in imageView.getLayoutBounds().getHeight()
                Image image = imageView.getImage();
                if (image != null) {
                    double newScale = imageView.getLayoutBounds().getHeight() / image.getHeight();
                    scanRegion.setDisplayScale(newScale);
                }
            });

        imagePane.getZoomScaleProperty().addListener((observable, oldValue, newValue) -> {
                if (scanRegion != null) {
                    scanRegion.setImageZoomScale(newValue.doubleValue());
                }
            });

        return imagePane;
    }

    private void createScanRegion() {
        Image image = imageView.getImage();

        if (image == null) return;

        Rectangle r;

        if (scanRegion == null) {
            Optional<Rectangle> rectMaybe = PlateDecoderPreferences.getInstance().getScanRegion();
            if (rectMaybe.isPresent()) {
                r = inchesToPixels(rectMaybe.get(), PlateDecoderDefaults.FLATBED_IMAGE_DPI);
            } else {
                r = inchesToPixels(PlateDecoderDefaults.getDefaultScanRegion(),
                                   PlateDecoderDefaults.FLATBED_IMAGE_DPI);
            }
        } else {
            r = scanRegion;
        }

        double scale = imageView.getLayoutBounds().getWidth() / image.getWidth();
        scanRegion = new ScanRegion(this,
                                    imageView,
                                    r.getX(),
                                    r.getY(),
                                    r.getWidth(),
                                    r.getHeight(),
                                    scale);
    }

    private void scanAction(@SuppressWarnings("unused") ActionEvent e) {
        if (!checkFilePresentLinux()) {
            PlateDecoder.errorDialog(
                "Simulating a scan of the entire flatbed will not work. "
                + "To correct this, please copy an image to: "
                + PlateDecoder.flatbedImageFilenameToUrl(),
                "Unable to simulate action",
                "File is missing.");
            return;
        }

        ScanRegionTask worker = new ScanRegionTask();

        ProgressDialog dlg = new ProgressDialog(worker);
        dlg.setTitle("Scanning flatbed");
        dlg.setHeaderText("Scanning flatbed");

        worker.setOnSucceeded(event -> {
                ScanLibResult result = worker.getValue();

                if (result.getResultCode() == ScanLibResult.Result.SUCCESS) {
                    Image image = new Image(PlateDecoder.flatbedImageFilenameToUrl());
                    imageView.setImage(image);
                    imageView.setCache(true);

                    createScanRegion();

                    imageGroup.getChildren().clear();
                    imageGroup.getChildren().add(imageView);
                    imageGroup.getChildren().add(scanRegion.getDisplayRegion());
                    imageGroup.getChildren().addAll(scanRegion.getResizeHandles());

                    continueButton.setDisable(false);
                }
            });

        worker.setOnFailed(event -> {
                LOG.error("The task failed: {}", event);
            });

        Thread th = new Thread(worker);
        th.setDaemon(true);
        th.start();
    }

    private boolean checkFilePresentLinux() {
        if (PlateDecoder.IS_LINUX) {
            return PlateDecoder.fileExists(PlateDecoderDefaults.FLATBED_IMAGE_NAME);
        }
        throw new IllegalStateException("OS is not Linux");
    }

    public void onContinueAction(EventHandler<ActionEvent> continueHandler) {
        continueHandlerMaybe = Optional.of(continueHandler);
    }

    private void continueAction(ActionEvent event) {
        Rectangle r = pixelsToInches(scanRegion, PlateDecoderDefaults.FLATBED_IMAGE_DPI);

        PlateDecoderPreferences.getInstance().setScanRegion(r);
        LOG.debug("continueAction: rect: {}", r);
        continueHandlerMaybe.ifPresent(handler -> handler.handle(event));
    }

    private Rectangle pixelsToInches(Rectangle r, long dotsPerInch) {
        return new Rectangle(r.getX()      / dotsPerInch,
                             r.getY()      / dotsPerInch,
                             r.getWidth()  / dotsPerInch,
                             r.getHeight() / dotsPerInch);
    }

    private Rectangle inchesToPixels(Rectangle r, long dotsPerInch) {
        return new Rectangle(r.getX()      * dotsPerInch,
                             r.getY()      * dotsPerInch,
                             r.getWidth()  * dotsPerInch,
                             r.getHeight() * dotsPerInch);
    }
}
