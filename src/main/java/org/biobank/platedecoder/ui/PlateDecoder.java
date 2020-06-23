package org.biobank.platedecoder.ui;

import static org.biobank.platedecoder.model.PlateDecoderDefaults.FLATBED_IMAGE_NAME;
import static org.biobank.platedecoder.model.PlateDecoderDefaults.FLATBED_PLATE_IMAGE_NAME;

import java.io.File;
import java.util.Map;
import java.util.Set;

import org.biobank.dmscanlib.DeviceNamesResult;
import org.biobank.dmscanlib.ScanLib;
import org.biobank.dmscanlib.ScanLibResult;
import org.biobank.platedecoder.model.ImageSourceFileSystem;
import org.biobank.platedecoder.model.PlateDecoderPreferences;
import org.biobank.platedecoder.ui.fsm.SceneFsmFactory;
import org.biobank.platedecoder.ui.scene.DecodeImageScene;
import org.biobank.platedecoder.ui.scene.DecodedTubes;
import org.biobank.platedecoder.ui.scene.ScanRegionScene;
import org.biobank.platedecoder.ui.scene.SceneRoot;
import org.biobank.platedecoder.ui.scene.SpecimenLink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * This class manages how all the scenes that make up the application are connected.
 *
 * <p>It is the main class for this JavaFX application.
 */
public class PlateDecoder extends Application implements SceneChanger {

  // @SuppressWarnings("unused")
  private static final Logger LOG = LoggerFactory.getLogger(PlateDecoder.class);

  public static final boolean IS_LINUX = System.getProperty("os.name").startsWith("Linux");

  public static final boolean IS_DEBUG_MODE = (System.getProperty("debug") != null);

  private Stage stage;

  private double sceneWidth;

  private double sceneHeight;

  public static void main(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage stage) throws Exception {

    this.stage = stage;
    stage.setTitle("Plate decoder");

    Point2D dimensions = PlateDecoderPreferences.getInstance().getAppWindowSize();

    sceneWidth = dimensions.getX();
    sceneHeight = dimensions.getY();

    setStartScene();
    stage.show();

    // for (Map.Entry<String, String> param : getParameters().getNamed().entrySet()) {
    //   LOG.info("param: {} -> {}", param.getKey(), param.getValue());
    // }

    // for (String param : getParameters().getUnnamed()) {
    //   LOG.info("unnamed param: {} -> {}", param);
    // }

    if (!getParameters().getUnnamed().contains("--skip-sane-init")) {
      this.saneInit();
    }
  }

  @Override
  public <T extends SceneRoot> void changeScene(T sceneRoot) {
    LOG.info("changeScene: {}", sceneRoot);

    Scene scene = stage.getScene();
    if (scene != null) {
      // theprevious scene's root has to be cleared so we dont get an exception when user
      // enters the scene again
      scene.setRoot(new Region());
    }
    scene = new Scene(sceneRoot, sceneWidth, sceneHeight);
    scene.widthProperty().addListener((observable, oldValue, newValue) -> {
        sceneWidth = newValue.doubleValue();
      });
    scene.heightProperty().addListener((observable, oldValue, newValue) -> {
        sceneHeight = newValue.doubleValue();
      });
    stage.setScene(scene);

    // need to call this whenever the scene is changed
    stage.setOnCloseRequest(this::handleCloseRequest);
    LOG.debug("changed scene: {}", sceneRoot.getClass().getSimpleName());
  }

  @Override
  public void closeApplicationRequest() {
    saveWindowSize();
    stage.close();
  }

  /**
   * Start scene can be set when DEBUG mode is on.
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

  // DEBGUG code
  private void setSceneTestSpecimenLink() {
    SpecimenLink.setTestData();
    SpecimenLink specimenLink = new SpecimenLink();
    changeScene(specimenLink);
    specimenLink.enableFinishAction(() -> closeApplicationRequest());
  }

  // DEBGUG code
  private void setSceneTestDecode() {
    DecodeImageScene decodeImage = new DecodeImageScene();
    DecodedTubes decodedTubes = new DecodedTubes();
    SpecimenLink specimenLink = new SpecimenLink();

    decodeImage.enableNextAction(() -> changeScene(decodedTubes));
    decodedTubes.enableBackAction(() -> changeScene(decodeImage));
    decodedTubes.enableFinishAction(() -> closeApplicationRequest());
    decodedTubes.onSpecimenLinkAction(() -> changeScene(specimenLink));

    changeScene(decodeImage);
    decodeImage.setImageSource(
      new ImageSourceFileSystem(
        "file:///home/nelson/Desktop/testImages/8x12/FrozenPalletImages/HP_L1985A/scanned4.bmp"));
  }

  // DEBUG code
  private void setSceneScanningRegion() {
    ScanRegionScene scanRegion = new ScanRegionScene();
    changeScene(scanRegion);
  }

  private void setScene() {
    SceneFsmFactory.createSceneFsm(this);
  }

  private void handleCloseRequest(@SuppressWarnings("unused") WindowEvent event) {
    saveWindowSize();
  }

  private void saveWindowSize() {
    Scene scene = stage.getScene();
    if (scene != null) {
      PlateDecoderPreferences.getInstance().setAppWindowSize(scene.getWidth(), scene.getHeight());
    }
  }

  /**
   * The file name used to store the image of the entire flatbed scanning region..
   *
   * @return the file name used to store the image of the entire flatbed scanning region.
   */
  public static String flatbedImageFilenameToUrl() {
    return userDirFilenameToUrl(FLATBED_IMAGE_NAME);
  }

  /**
   * The file name used to store the image of the plate.
   *
   * @return the file name used to store the image of the plate.
   */
  public static String flatbedPlateImageFilenameToUrl() {
    return userDirFilenameToUrl(FLATBED_PLATE_IMAGE_NAME);
  }

  /**
   * Checks if a filename exists in the file system.
   *
   * @param filename The filename to check.
   *
   * @return true if filename exists and is not a directory.
   */
  public static boolean fileExists(String filename) {
    File f = new File(filename);
    return (f.exists() && !f.isDirectory());
  }

  private static String userDirFilenameToUrl(String filename) {
    StringBuffer buf = new StringBuffer();

    if (IS_LINUX) {
      buf.append("file://");
    } else {
      buf.append("file:/");
    }

    buf.append(System.getProperty("user.dir"));
    buf.append(File.separator);
    buf.append(filename);
    return buf.toString();
  }

  private void saneInit() {
    LOG.info("startup");
    ScanLib scanLib = ScanLib.getInstance();
    LOG.info("got scanLib instance");

    if (!ScanLib.runningMsWindows()) {
      DeviceNamesResult result = scanLib.getDeviceNames();

      if (result.getResultCode() != ScanLibResult.Result.SUCCESS) {
        throw new IllegalStateException("could not get scanning library device names");
      }
      Set<String> deviceNames = result.getDeviceNames();
      LOG.info("got scanLib device names: {}", deviceNames);

      // if (deviceNames.isEmpty()) {
      //    throw new IllegalStateException("scanning library reports ZERO devices");
      // }
      // PlateModel.getInstance().setDeviceName(deviceNames.iterator().next());
    }
  }

}
