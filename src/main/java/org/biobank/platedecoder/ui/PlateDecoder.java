package org.biobank.platedecoder.ui;

import java.util.Map;

import org.biobank.platedecoder.dmscanlib.LibraryLoader;
import org.biobank.platedecoder.model.PlateDecoderPreferences;
import org.biobank.platedecoder.ui.scene.AbstractSceneRoot;
import org.biobank.platedecoder.ui.scene.DecodedTubes;
import org.biobank.platedecoder.ui.scene.FileChoose;
import org.biobank.platedecoder.ui.scene.DecodeImageScene;
import org.biobank.platedecoder.ui.scene.ImageSource;
import org.biobank.platedecoder.ui.scene.ScanRegionScene;
import org.biobank.platedecoder.ui.scene.SpecimenLink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

public class PlateDecoder extends Application {

    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(PlateDecoder.class);

    public static final boolean IS_DEBUG_MODE = (System.getProperty("debug") != null);

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
        sceneOnClose();
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
        DecodeImageScene imageAndGrid = new DecodeImageScene();
        DecodedTubes decodedTubes = new DecodedTubes();
        SpecimenLink specimenLink = new SpecimenLink();

        imageAndGrid.onContinueAction(e -> {
                changeScene(decodedTubes);
            });

        decodedTubes.enableBackAction(e -> {
                changeScene(imageAndGrid);
            });

        decodedTubes.enableFinishAction(e -> {
                Platform.exit();
            });

        decodedTubes.onSpecimenLinkAction(e -> {
                changeScene(specimenLink);
            });

        // (global-visual-line-mode -1)

        changeScene(imageAndGrid);
        //imageAndGrid.setImageFileURI("file:///home/nelson/Desktop/scanned_ice4_cropped.bmp");
        imageAndGrid.setImageFileURI(
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
        FileChoose fileChoose = new FileChoose();
        DecodeImageScene imageAndGrid = new DecodeImageScene();
        DecodedTubes decodedTubes = new DecodedTubes();

        imageSourceSelection.onFlatbedSelectedAction(e -> {
                changeScene(fileChoose);
            });

        fileChoose.enableBackAction(e -> {
                changeScene(imageSourceSelection);
            });

        fileChoose.onDecodeAction(e -> {
                imageAndGrid.setImageFileURI(fileChoose.getSelectedFileURI());
                changeScene(imageAndGrid);
            });

        imageAndGrid.enableBackAction(e -> {
                changeScene(fileChoose);
            });

        imageAndGrid.onContinueAction(e -> {
                changeScene(decodedTubes);
            });

        decodedTubes.enableBackAction(e -> {
                changeScene(imageAndGrid);
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
    }

    private void sceneOnClose() {
        stage.setOnCloseRequest(e -> {
                Scene scene = stage.getScene();
                if (scene != null) {
                    PlateDecoderPreferences.getInstance().setAppWindowSize(
                        scene.getWidth(), scene.getHeight());
                }
            });
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

}
