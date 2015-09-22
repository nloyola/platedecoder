package org.biobank.platedecoder.ui;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.prefs.Preferences;

import org.biobank.platedecoder.model.PlateModel;
import org.biobank.platedecoder.model.PlateType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlateDecoder extends Application {

    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(PlateDecoder.class);

    // private final Color color = Color.color(0.66, 0.67, 0.69);

    private Preferences prefs = Preferences.userNodeForPackage(PlateDecoder.class);

    private static final String PREFS_APP_WINDOW_WIDTH = "PREFS_APP_WINDOW_WIDTH";

    private static final String PREFS_APP_WINDOW_HEIGHT = "PREFS_APP_WINDOW_HEIGHT";

    private static final boolean IS_MS_WINDOWS = System.getProperty("os.name").startsWith("Windows");

    private static final boolean IS_LINUX = System.getProperty("os.name").startsWith("Linux");

    private static final boolean IS_ARCH_64_BIT = System.getProperty("os.arch").equals("amd64");

    private Stage stage;

    private ScrollPane plateRegion;

    private final PlateModel model = PlateModel.getInstance();

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

        sceneWidth  = Double.parseDouble(prefs.get(PREFS_APP_WINDOW_WIDTH,  "1000"));
        sceneHeight = Double.parseDouble(prefs.get(PREFS_APP_WINDOW_HEIGHT, "500"));

        setScene();
        //setSceneTestDecode();

        stage.setOnCloseRequest(e -> {
                Scene scene = stage.getScene();
                if (scene != null) {
                    prefs.put(PREFS_APP_WINDOW_WIDTH,  String.valueOf(scene.getWidth()));
                    prefs.put(PREFS_APP_WINDOW_HEIGHT, String.valueOf(scene.getHeight()));
                }
            });

        stage.show();
    }


    @SuppressWarnings("unused")
    private void setSceneTestDecode() {
        ImageAndGrid imageAndGrid = new ImageAndGrid();

        DecodedTubes decodedTubes = new DecodedTubes();

        imageAndGrid.onContinueAction(e -> {
                changeScene(decodedTubes);
            });

        decodedTubes.onBackAction(e -> {
                changeScene(imageAndGrid);
            });

        changeScene(imageAndGrid);
        //imageAndGrid.setImageFileURI("file:///home/nelson/Desktop/scanned_ice4_cropped.bmp");
        imageAndGrid.setImageFileURI("file:///home/nelson/Dropbox/CBSR/scanlib/testImages/12x12/stanford_12x12_1.jpg");
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

        fileChoose.onBackAction(e -> {
                changeScene(imageSourceSelection);
            });

        fileChoose.onDecodeAction(e -> {
                imageAndGrid.setImageFileURI(fileChoose.getSelectedFileURI());
                changeScene(imageAndGrid);
            });

        imageAndGrid.onBackAction(e -> {
                changeScene(fileChoose);
            });

        imageAndGrid.onContinueAction(e -> {
                changeScene(decodedTubes);
            });

        decodedTubes.onBackAction(e -> {
                changeScene(imageAndGrid);
            });

        changeScene(imageSourceSelection);
    }

    private <T extends AbstractSceneRoot> void changeScene(T sceneRoot) {
        Scene scene = stage.getScene();
        if (scene != null) {
            // the previous scene's root has to be cleared so we dont get an exception when user
            // enters the scene again
            scene.setRoot(new Region());
        }
        sceneRoot.onDisplay();
        stage.setScene(new Scene(sceneRoot, sceneWidth, sceneHeight));
    }

    @SuppressWarnings("unused")
    private Scene plateTypeScene() {
        SplitPane sp = new SplitPane();
        final VBox leftPane = new VBox(8);
        plateRegion = new ScrollPane();

        leftPane.getChildren().add(createPlateTypeControl());

        sp.getItems().addAll(leftPane, plateRegion);
        sp.setDividerPositions(0.20f, 0.80f);

        SplitPane.setResizableWithParent(leftPane, Boolean.FALSE);
        return new Scene(sp);
    }

    private Node createPlateTypeControl() {
        Text plateTypeText = new Text();
        plateTypeText.setText("Plate type:");
        plateTypeText.setFont(Font.font("Verdana", 12));

        ChoiceBox<PlateType> plateTypeChoice = createPlateChoiceBox();

        HBox hbox = new HBox(8);
        hbox.getChildren().addAll(plateTypeText, plateTypeChoice);

        return hbox;
    }

    private ChoiceBox<PlateType> createPlateChoiceBox() {
        ChoiceBox<PlateType> result = new ChoiceBox<PlateType>();
        result.setItems(model.plateTypes);
        result.setStyle("-fx-font: 12px \"Verdana\"");
        return result;
    }

    // private BorderPane createImageSourceControl() {
    // final ToggleGroup toggleGroup = new ToggleGroup();

    // RadioButton flatbedRegion = new RadioButton("Flatbed Region 1");
    // flatbedRegion.setToggleGroup(toggleGroup);

    // RadioButton filesystem = new RadioButton("Filesystem");
    // filesystem.setToggleGroup(toggleGroup);

    // VBox vbox = new VBox(8, flatbedRegion, filesystem);
    // vbox.getStyleClass().add("bordered-titled-border");

    // toggleGroup.selectToggle(toggleGroup.getToggles().get(0));
    // toggleGroup.selectedToggleProperty().addListener((ov, oldValue, newValue) -> {
    // RadioButton rb = ((RadioButton) toggleGroup.getSelectedToggle());
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
