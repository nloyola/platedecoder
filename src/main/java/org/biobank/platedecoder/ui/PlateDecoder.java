package org.biobank.platedecoder.ui;

import org.biobank.platedecoder.model.PlateDecoderPreferences;
import org.biobank.platedecoder.ui.scene.AbstractSceneRoot;
import org.biobank.platedecoder.ui.scene.DecodedTubes;
import org.biobank.platedecoder.ui.scene.FileChoose;
import org.biobank.platedecoder.ui.scene.ImageAndGrid;
import org.biobank.platedecoder.ui.scene.ImageSource;
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

    // private final Color color = Color.color(0.66, 0.67, 0.69);

    private static final boolean IS_MS_WINDOWS = System.getProperty("os.name").startsWith("Windows");

    private static final boolean IS_LINUX = System.getProperty("os.name").startsWith("Linux");

    private static final boolean IS_ARCH_64_BIT = System.getProperty("os.arch").equals("amd64");

    private Stage stage;

    private double sceneWidth;

    private double sceneHeight;

    public static void main(String[] args) {
        if (IS_MS_WINDOWS) {
            System.loadLibrary("OpenThreadsWin32");
            System.loadLibrary("opencv_core248");
            System.loadLibrary("opencv_highgui248");
            System.loadLibrary("opencv_imgproc248");
            System.loadLibrary("dmscanlib");
        } else if (IS_LINUX && IS_ARCH_64_BIT) {
            System.loadLibrary("dmscanlib64");
        }

        launch(args);
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        stage.setTitle("Plate decoder");

        Point2D dimensions = PlateDecoderPreferences.getInstance().getAppWindowSize();

        sceneWidth  = dimensions.getX();
        sceneHeight = dimensions.getY();

        setScene();
        //setSceneTestDecode();
        //setSceneTestSpecimenLink();
        sceneOnClose();
        stage.show();
    }

    @SuppressWarnings("unused")
    private void setSceneTestSpecimenLink() {
        SpecimenLink.setTestData();

        SpecimenLink specimenLink = new SpecimenLink();
        changeScene(specimenLink);

        specimenLink.enableFinishAction(e -> {
                Platform.exit();
            });
    }

    @SuppressWarnings("unused")
    private void setSceneTestDecode() {
        ImageAndGrid imageAndGrid = new ImageAndGrid();
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

        changeScene(imageAndGrid);
        imageAndGrid.setImageFileURI("file:///home/nelson/Desktop/scanned_ice4_cropped.bmp");
        //imageAndGrid.setImageFileURI("file:///home/nelson/Dropbox/CBSR/scanlib/testImages/12x12/stanford_12x12_1.jpg");
    }

    @SuppressWarnings("unused")
    private void setScene() {
        ImageSource imageSourceSelection = new ImageSource();
        FileChoose fileChoose = new FileChoose();
        ImageAndGrid imageAndGrid = new ImageAndGrid();
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


    // private BorderPane createImageSourceControl() {
    // final ToggleGroup toggleGroup = new ToggleGroup();

// RadioButtonflatbedRegion = new RadioButton("Flatbed Region 1");
    // flatbedRegion.setToggleGroup(toggleGroup);

// RadioButtonfilesystem = new RadioButton("Filesystem");
    // filesystem.setToggleGroup(toggleGroup);

// VBox vbox =new VBox(8, flatbedRegion, filesystem);
    // vbox.getStyleClass().add("bordered-titled-border");

    // toggleGroup.selectToggle(toggleGroup.getToggles().get(0));
    // toggleGroup.selectedToggleProperty().addListener((ov, oldValue, newValue) -> {
// RadioButtonrb = ((RadioButton) toggleGroup.getSelectedToggle());
    // if (rb != null) {
    // System.out.println(rb.getText() + " selected");
// }
// });

    // BorderPane result = new BorderPane();
    // Text topText = new Text("Image source");
    // topText.setFont(Font.font("Arial", FontWeight.BOLD, 12));
    // result.setTop(topText);
    // result.setCenter(vbox);
    // return result;
// }

}
