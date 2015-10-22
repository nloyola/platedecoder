package org.biobank.platedecoder.ui;

import static org.biobank.platedecoder.model.PlateDecoderDefaults.*;

import java.io.File;
import java.util.Map;
import java.util.Optional;

import org.biobank.platedecoder.dmscanlib.LibraryLoader;
import org.biobank.platedecoder.model.PlateDecoderPreferences;
import org.biobank.platedecoder.ui.scene.AbstractSceneRoot;
import org.biobank.platedecoder.ui.scene.DecodeImageScene;
import org.biobank.platedecoder.ui.scene.DecodedTubes;
import org.biobank.platedecoder.ui.scene.FileChoose;
import org.biobank.platedecoder.ui.scene.ImageSource;
import org.biobank.platedecoder.ui.scene.ScanPlateScene;
import org.biobank.platedecoder.ui.scene.ScanRegionScene;
import org.biobank.platedecoder.ui.scene.SpecimenLink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class PlateDecoder extends Application implements EventHandler<WindowEvent> {

    //@SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(PlateDecoder.class);

    public static final boolean IS_LINUX = System.getProperty("os.name").startsWith("Linux");

    public static final boolean IS_DEBUG_MODE = (System.getProperty("debug") != null);

    private AbstractSceneRoot onBackFromDecodeImage;

    private Stage stage;

    private double sceneWidth;

    private double sceneHeight;

    public static void main(String[] args) {
        LibraryLoader.load();
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        stage.setTitle("Plate decoder");

        Point2D dimensions = PlateDecoderPreferences.getInstance().getAppWindowSize();

        sceneWidth  = dimensions.getX();
        sceneHeight = dimensions.getY();

        setStartScene();
        stage.show();
    }

    /**
     * Start scene can be set when DEBUB mode is on.
     */
    private void setStartScene() {
        Map<String, String> namedArgs = getParameters().getNamed();
        String startScene = null;

        if (IS_DEBUG_MODE) {
            startScene = namedArgs.get("scene");
        }

        if (startScene == null) {
            setScene();
        } else {
            switch (startScene) {
                case "testdecode":
                    setSceneTestDecode();
                    break;

                case "scanningregion":
                    setSceneScanningRegion();
                    break;

                case "specimenlink":
                    setSceneTestSpecimenLink();
                    break;
            }
        }
    }

    private void setSceneTestSpecimenLink() {
        SpecimenLink.setTestData();

        SpecimenLink specimenLink = new SpecimenLink();
        changeScene(specimenLink);

        specimenLink.enableFinishAction(e -> {
                Platform.exit();
            });
    }

    private void setSceneTestDecode() {
        DecodeImageScene decodeImage = new DecodeImageScene();
        DecodedTubes decodedTubes = new DecodedTubes();
        SpecimenLink specimenLink = new SpecimenLink();

        decodeImage.onContinueAction(e -> {
                changeScene(decodedTubes);
            });

        decodedTubes.enableBackAction(e -> {
                changeScene(decodeImage);
            });

        decodedTubes.enableFinishAction(e -> {
                Platform.exit();
            });

        decodedTubes.onSpecimenLinkAction(e -> {
                changeScene(specimenLink);
            });

        changeScene(decodeImage);
        //imageAndGrid.setImageFileURI("file:///home/nelson/Desktop/scanned_ice4_cropped.bmp");
        decodeImage.setImageFileURI(
            "file:///home/nelson/Desktop/testImages/8x12/FrozenPalletImages/HP_L1985A/scanned4.bmp");
        //imageAndGrid.setImageFileURI(
        //"file:///home/nelson/Dropbox/CBSR/scanlib/testImages/12x12/stanford_12x12_1.jpg");
    }

    private void setSceneScanningRegion() {
        ScanRegionScene scanRegion = new ScanRegionScene();
        changeScene(scanRegion);
    }

    private void setScene() {
        ImageSource imageSourceSelection = new ImageSource();
        FileChoose fileChoose            = new FileChoose();
        ScanRegionScene scanRegion       = new ScanRegionScene();
        ScanPlateScene scanPlate         = new ScanPlateScene();
        DecodeImageScene decodeImage     = new DecodeImageScene();
        DecodedTubes decodedTubes        = new DecodedTubes();

        // TODO: fix back button when flatbed scan is used

        imageSourceSelection.onFilesystemAction(e -> {
                changeScene(fileChoose);
            });

        imageSourceSelection.onFlatbedScanAction(e -> {
                Optional<Rectangle> rectMaybe = PlateDecoderPreferences.getInstance().getScanRegion();
                if (rectMaybe.isPresent()) {
                    changeScene(scanPlate);
                } else {
                    changeScene(scanRegion);
                }
            });

        fileChoose.enableBackAction(e -> {
                changeScene(imageSourceSelection);
            });

        fileChoose.onDecodeAction(e -> {
                onBackFromDecodeImage = fileChoose;
                decodeImage.setImageFileURI(fileChoose.getSelectedFileURI());
                changeScene(decodeImage);
            });

        scanRegion.onContinueAction(e -> {
                changeScene(scanPlate);
            });

        scanRegion.enableBackAction(e -> {
                changeScene(imageSourceSelection);
            });

        scanPlate.onScanCompleteAction(e -> {
                onBackFromDecodeImage = scanPlate;
                decodeImage.setImageFileURI(flatbedPlateImageFilenameToUrl());
                changeScene(decodeImage);
            });

        scanPlate.enableBackAction(e -> {
                changeScene(imageSourceSelection);
            });

        decodeImage.enableBackAction(e -> {
                if (onBackFromDecodeImage == null) {
                    throw new IllegalStateException("onBackFromDecodeImage is null");
                }
                changeScene(onBackFromDecodeImage);
            });

        decodeImage.onContinueAction(e -> {
                changeScene(decodedTubes);
            });

        decodedTubes.enableBackAction(e -> {
                changeScene(decodeImage);
            });

        decodedTubes.enableFinishAction(e -> {
                Platform.exit();
            });

        changeScene(imageSourceSelection);
    }

    private <T extends AbstractSceneRoot> void changeScene(T sceneRoot) {
        Scene scene = stage.getScene();
        if (scene != null) {
            // theprevious scene's root has to be cleared so we dont get an exception when user
            // enters the scene again
            scene.setRoot(new Region());
        }
        sceneRoot.onDisplay();
        stage.setScene(new Scene(sceneRoot, sceneWidth, sceneHeight));

        // need to call this whenever the scene is changed
        stage.setOnCloseRequest(this);
    }

    @Override
    public void handle(WindowEvent we) {
        LOG.debug("sceneOnClose");
        Scene scene = stage.getScene();
        if (scene != null) {
            LOG.debug("sceneOnClose: saving window size");
            PlateDecoderPreferences.getInstance().setAppWindowSize(
                scene.getWidth(), scene.getHeight());
        }
    }

    public static void infoDialog(String infoMessage, String titleBar) {
        // By specifying a null headerMessage String, we cause the dialog to not have a header
        infoDialog(infoMessage, titleBar, null);
    }

    public static void infoDialog(String infoMessage,
                                  String titleBar,
                                  String headerMessage) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(titleBar);
        alert.setHeaderText(headerMessage);
        alert.setContentText(infoMessage);
        alert.showAndWait();
    }

    public static void errorDialog(String infoMessage,
                                   String titleBar,
                                   String headerMessage) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(titleBar);
        alert.setHeaderText(headerMessage);
        alert.setContentText(infoMessage);
        alert.showAndWait();
    }

    public static String flatbedImageFilename() {
        return FLATBED_IMAGE_NAME;
    }

    public static String flatbedPlateImageFilename() {
        return FLATBED_PLATE_IMAGE_NAME;
    }

    private static String userDirFilenameToUrl(String filename) {
        StringBuffer buf = new StringBuffer();
        buf.append("file://");
        buf.append(System.getProperty("user.dir"));
        buf.append(File.separator);
        buf.append(filename);
        return buf.toString();
    }

    /**
     * Returns the file name used to store the image of the entire flatbed scanning region.
     */
    public static String flatbedImageFilenameToUrl() {
        return userDirFilenameToUrl(FLATBED_IMAGE_NAME);
    }

    /**
     * Returns the file name used to store the image of the plate.
     */
    public static String flatbedPlateImageFilenameToUrl() {
        return userDirFilenameToUrl(FLATBED_PLATE_IMAGE_NAME);
    }

}
