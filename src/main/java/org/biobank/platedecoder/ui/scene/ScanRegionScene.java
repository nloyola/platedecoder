package org.biobank.platedecoder.ui.scene;

import java.io.File;

import org.biobank.platedecoder.dmscanlib.ScanLib;
import org.biobank.platedecoder.dmscanlib.ScanLibResult;
import org.biobank.platedecoder.model.PlateDecoderPreferences;
import org.biobank.platedecoder.ui.ZoomingPane;
import org.biobank.platedecoder.ui.scanregion.ScanRegion;
import org.biobank.platedecoder.ui.scanregion.ScanRegionHandler;
import org.controlsfx.dialog.ProgressDialog;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScanRegionScene extends AbstractSceneRoot implements ScanRegionHandler {

    //@SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(ScanRegionScene.class);

    private static final boolean IS_LINUX = System.getProperty("os.name").startsWith("Linux");

    private static final String FLATBED_IMAGE_NAME = "flatbed.png";

    private static final long FLATBED_IMAGE_DPI = 300;

    private ImageView imageView;

    private Group imageGroup;

    private ScanRegion scanRegion;

    public ScanRegionScene() {
        super("Define scanning region");
    }

    @Override
    public void onDisplay() {
        createScanRegion();
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
        Button button = new Button("Scan");
        button.setOnAction(this::scanFlatbed);
        button.setMaxWidth(Double.MAX_VALUE);

        GridPane grid = new GridPane();
        grid.setVgap(2);
        grid.setHgap(2);
        grid.add(button, 0, 0);

        return grid;
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
            r = PlateDecoderPreferences.getInstance().getScanRegion();
        } else {
            r = scanRegion.getDisplayRegion();
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

    private void scanFlatbed(@SuppressWarnings("unused") ActionEvent e) {
        LOG.debug("scanFlatbed");
        Task<ScanLibResult> worker = new Task<ScanLibResult>() {
                @Override
                protected ScanLibResult call() throws Exception {
                    if (IS_LINUX) {
                        return scanFlatbedLinux();
                    }
                    return scanFlatbedWindows();}
            };

        ProgressDialog dlg = new ProgressDialog(worker);
        dlg.setTitle("Scanning flatbed");
        dlg.setHeaderText("Scanning flatbed");

        worker.setOnSucceeded(event -> {
                ScanLibResult result = worker.getValue();

                if (result.getResultCode() == ScanLibResult.Result.SUCCESS) {
                    StringBuffer buf = new StringBuffer();
                    buf.append("file://");
                    buf.append(System.getProperty("user.dir"));
                    buf.append(File.separator);
                    buf.append(FLATBED_IMAGE_NAME);

                    Image image = new Image(buf.toString());
                    imageView.setImage(image);
                    imageView.setCache(true);

                    createScanRegion();

                    imageGroup.getChildren().clear();
                    imageGroup.getChildren().add(imageView);
                    imageGroup.getChildren().add(scanRegion.getDisplayRegion());
                    imageGroup.getChildren().addAll(scanRegion.getResizeHandles());
                }
            });

        worker.setOnFailed(event -> {
                LOG.error("The task failed: {}", event);
            });

        Thread th = new Thread(worker);
        th.setDaemon(true);
        th.start();
    }

    private ScanLibResult scanFlatbedWindows() {
        return ScanLib.getInstance().scanFlatbed(0L,
                                                 FLATBED_IMAGE_DPI,
                                                 0,
                                                 0,
                                                 FLATBED_IMAGE_NAME);
    }

    private ScanLibResult scanFlatbedLinux() throws InterruptedException {
        Thread.sleep(500);
        return new ScanLibResult(ScanLib.SC_SUCCESS, 0, "");
    }
}
