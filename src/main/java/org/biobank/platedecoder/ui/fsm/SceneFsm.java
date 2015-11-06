package org.biobank.platedecoder.ui.fsm;

import java.util.Optional;

import org.biobank.platedecoder.model.ImageSourceFileSystem;
import org.biobank.platedecoder.model.ImageSourceFlatbedScanner;
import org.biobank.platedecoder.model.PlateDecoderPreferences;
import org.biobank.platedecoder.service.fsm.Fsm;
import org.biobank.platedecoder.service.fsm.TransitionRunnable;
import org.biobank.platedecoder.ui.SceneChanger;
import org.biobank.platedecoder.ui.scene.DecodeImageScene;
import org.biobank.platedecoder.ui.scene.DecodedTubes;
import org.biobank.platedecoder.ui.scene.FileChoose;
import org.biobank.platedecoder.ui.scene.FlatbedScannerSettings;
import org.biobank.platedecoder.ui.scene.InitialScene;
import org.biobank.platedecoder.ui.scene.ScanPlateScene;
import org.biobank.platedecoder.ui.scene.ScanRegionScene;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.scene.shape.Rectangle;

/*
 * A state machine to handle transitions between the scenes that make up this application.
 *
 * Returns to initial scene when on FINISH_EVENT.
 */
class SceneFsm extends Fsm<StateId, ChoicepointId, Event> {

   @SuppressWarnings("unused")
   private static final Logger LOG = LoggerFactory.getLogger(SceneFsm.class);

   private final InitialScene initialScene    = new InitialScene();
   private final FileChoose fileChoose        = new FileChoose();
   private final ScanRegionScene scanRegion   = new ScanRegionScene();
   private final ScanPlateScene scanPlate     = new ScanPlateScene();
   private final DecodeImageScene decodeImage = new DecodeImageScene();
   private final DecodedTubes decodedTubes    = new DecodedTubes();
   private final FlatbedScannerSettings scannerSettings = new FlatbedScannerSettings();

   private final SceneChanger sceneChanger;

   private boolean flatbedScannerUsed = false;
   private boolean decodeWithPreviousSettings = false;

   public SceneFsm(SceneChanger sceneChanger) {
      this.sceneChanger = sceneChanger;

      addState(StateId.INITIAL_SCENE);
      addState(StateId.USE_FILESYSTEM_IMAGE);
      addState(StateId.USE_FLATBED_IMAGE);
      addState(StateId.SCAN_REGION, StateId.USE_FLATBED_IMAGE);
      addState(StateId.SCAN_PLATE, StateId.USE_FLATBED_IMAGE);
      addState(StateId.DECODE_IMAGE);
      addState(StateId.DECODED_IMAGE_TUBES);
      addState(StateId.FLATBED_SETTINGS_SCANNER);
      addState(StateId.FLATBED_SETTINGS_REGION);

      addChoicepoint(
         ChoicepointId.FLATBED_IMAGE_SELECTED,
         () -> { return flatbedScannerUsed; });
      addChoicepoint(
         ChoicepointId.FLATBED_IMAGE_SELECTED_WITH_PREV_SETTINGS,
         () -> { return decodeWithPreviousSettings; });

      createInitialSceneTransitions();
      createFileChooserSceneTransitions();
      createUseFlatbedImageTransitions();
      createDecodeImageSceneTransitions();
      createDecodedTubesSceneTransitions();
      createFlatbedScannerSettingsTransitions();

      validate();

      sceneChanger.changeScene(initialScene);
   }

   private void createInitialSceneTransitions() {
      TransitionRunnable r = () -> {
         flatbedScannerUsed = true;
         decodeWithPreviousSettings = false;
         sceneChanger.changeScene(scanRegion);
      };

      addTransition(Event.SCAN_REGION_DEFINE,
                    StateId.INITIAL_SCENE,
                    StateId.SCAN_REGION,
                    r);
      addTransition(Event.SCAN_REGION_DEFINE,
                    StateId.INITIAL_SCENE,
                    StateId.SCAN_REGION,
                    r);
      addTransition(Event.CONTINUE_SELECTED,
                    StateId.INITIAL_SCENE,
                    StateId.SCAN_PLATE,
                    () -> {
                       flatbedScannerUsed = true;
                       decodeWithPreviousSettings = false;
                       sceneChanger.changeScene(scanPlate);
                    });
      addTransition(Event.SCAN_AND_DECODE_WITH_PREVIOUS,
                    StateId.INITIAL_SCENE,
                    StateId.DECODE_IMAGE,
                    () -> {
                       decodeWithPreviousSettings = true;
                       sceneChanger.changeScene(decodeImage);
                    });
      addTransition(Event.FILE_CHOSEN,
                    StateId.INITIAL_SCENE,
                    StateId.USE_FILESYSTEM_IMAGE,
                    () -> {
                       flatbedScannerUsed = false;
                       decodeWithPreviousSettings = false;
                       sceneChanger.changeScene(fileChoose);
                    });
      addTransition(Event.MODIFY_CONFIG,
                    StateId.INITIAL_SCENE,
                    StateId.FLATBED_SETTINGS_SCANNER,
                    () -> sceneChanger.changeScene(scannerSettings));

      initialScene.onFilesystemAction(() -> feedEvent(Event.FILE_CHOSEN));

      initialScene.onFlatbedScanAction(() -> {
            Optional<Rectangle> rectMaybe =
               PlateDecoderPreferences.getInstance().getScanRegion();
            if (rectMaybe.isPresent()) {
               feedEvent(Event.CONTINUE_SELECTED);
            } else {
               feedEvent(Event.SCAN_REGION_DEFINE);
            }
         });

      initialScene.onFlatbedScanWithPreviousParamsAction(() -> {
            decodeImage.setImageSource(ImageSourceFlatbedScanner.getInstance());
            feedEvent(Event.SCAN_AND_DECODE_WITH_PREVIOUS);
         });

      initialScene.modifyConfigrationAction(() -> feedEvent(Event.MODIFY_CONFIG));
   }

   private void createFileChooserSceneTransitions() {
      addTransition(Event.CONTINUE_SELECTED,
                    StateId.USE_FILESYSTEM_IMAGE,
                    StateId.DECODE_IMAGE,
                    () -> sceneChanger.changeScene(decodeImage));
      addTransition(Event.BACK_SELECTED,
                    StateId.USE_FILESYSTEM_IMAGE,
                    StateId.INITIAL_SCENE,
                    () -> sceneChanger.changeScene(initialScene));
      addTransition(Event.FINISH_SELECTED,
                    StateId.USE_FILESYSTEM_IMAGE,
                    StateId.INITIAL_SCENE,
                    () -> sceneChanger.closeApplicationRequest());

      fileChoose.enableBackAction(() -> feedEvent(Event.BACK_SELECTED));

      fileChoose.onDecodeAction(() -> {
            ImageSourceFileSystem imageSource =
               new ImageSourceFileSystem(fileChoose.getSelectedFileURI());
            decodeImage.setImageSource(imageSource);
            feedEvent(Event.CONTINUE_SELECTED);
         });

   }

   private void createUseFlatbedImageTransitions() {
      addTransition(Event.CONTINUE_SELECTED,
                    StateId.SCAN_REGION,
                    StateId.SCAN_PLATE,
                    () -> sceneChanger.changeScene(scanPlate));

      addTransition(Event.BACK_SELECTED,
                    StateId.USE_FLATBED_IMAGE,
                    StateId.INITIAL_SCENE,
                    () -> sceneChanger.changeScene(initialScene));

      addTransition(Event.CONTINUE_SELECTED,
                    StateId.SCAN_PLATE,
                    StateId.DECODE_IMAGE,
                    () -> {
                       sceneChanger.changeScene(decodeImage);
                    });

      scanRegion.enableBackAction(() -> feedEvent(Event.BACK_SELECTED));
      scanRegion.enableNextAction(() -> feedEvent(Event.CONTINUE_SELECTED));
      scanPlate.enableNextAction(() -> {
            decodeImage.setImageSource(ImageSourceFlatbedScanner.getInstance());
            feedEvent(Event.CONTINUE_SELECTED);
         });
      scanPlate.enableBackAction(() -> feedEvent(Event.BACK_SELECTED));
   }

   private void createDecodeImageSceneTransitions() {
      addTransition(Event.CONTINUE_SELECTED,
                    StateId.DECODE_IMAGE,
                    StateId.DECODED_IMAGE_TUBES,
                    () -> sceneChanger.changeScene(decodedTubes));
      addTransitionToChoice(
         Event.BACK_SELECTED,
         StateId.DECODE_IMAGE,
         ChoicepointId.FLATBED_IMAGE_SELECTED_WITH_PREV_SETTINGS);

      addTransitionFromChoiceToChoice(
         ChoicepointId.FLATBED_IMAGE_SELECTED_WITH_PREV_SETTINGS,
         false,
         ChoicepointId.FLATBED_IMAGE_SELECTED);

      addTransitionFromChoiceToState(
         ChoicepointId.FLATBED_IMAGE_SELECTED_WITH_PREV_SETTINGS,
         true,
         StateId.INITIAL_SCENE,
         () -> sceneChanger.changeScene(initialScene));
      addTransitionFromChoiceToState(
         ChoicepointId.FLATBED_IMAGE_SELECTED,
         false,
         StateId.USE_FILESYSTEM_IMAGE,
         () -> sceneChanger.changeScene(fileChoose));
      addTransitionFromChoiceToState(
         ChoicepointId.FLATBED_IMAGE_SELECTED,
         true,
         StateId.SCAN_PLATE,
         () -> sceneChanger.changeScene(scanPlate));

      decodeImage.enableBackAction(() -> feedEvent(Event.BACK_SELECTED));
      decodeImage.enableNextAction(() -> feedEvent(Event.CONTINUE_SELECTED));
   }

   private void createDecodedTubesSceneTransitions() {
      addTransition(Event.BACK_SELECTED,
                    StateId.DECODED_IMAGE_TUBES,
                    StateId.DECODE_IMAGE,
                    () -> sceneChanger.changeScene(decodeImage));
      addTransition(Event.FINISH_SELECTED,
                    StateId.DECODED_IMAGE_TUBES,
                    StateId.INITIAL_SCENE,
                    () -> sceneChanger.closeApplicationRequest());

      decodedTubes.enableBackAction(() -> feedEvent(Event.BACK_SELECTED));
      decodedTubes.enableFinishAction(() -> feedEvent(Event.FINISH_SELECTED));
   }

   private void createFlatbedScannerSettingsTransitions() {
      TransitionRunnable backToInitialSceneRunnable =
         () -> sceneChanger.changeScene(initialScene);

      addTransition(Event.FINISH_SELECTED,
                    StateId.FLATBED_SETTINGS_SCANNER,
                    StateId.INITIAL_SCENE,
                    backToInitialSceneRunnable
                    );
      addTransition(Event.BACK_SELECTED,
                    StateId.FLATBED_SETTINGS_SCANNER,
                    StateId.INITIAL_SCENE,
                    backToInitialSceneRunnable);
      addTransition(Event.SCAN_REGION_DEFINE,
                    StateId.FLATBED_SETTINGS_SCANNER,
                    StateId.FLATBED_SETTINGS_REGION,
                    () -> sceneChanger.changeScene(scanRegion));
      addTransition(Event.SCAN_REGION_DEFINE,
                    StateId.FLATBED_SETTINGS_SCANNER,
                    StateId.FLATBED_SETTINGS_REGION,
                    () -> sceneChanger.changeScene(scanRegion));
      addTransition(Event.CONTINUE_SELECTED,
                    StateId.FLATBED_SETTINGS_REGION,
                    StateId.FLATBED_SETTINGS_SCANNER,
                    () -> sceneChanger.changeScene(scannerSettings));

      scannerSettings.enableBackAction(() -> feedEvent(Event.BACK_SELECTED));
      scannerSettings.enableNextAction(() -> feedEvent(Event.FINISH_SELECTED));
      scannerSettings.onDefineScanRegionAction(() -> feedEvent(Event.SCAN_REGION_DEFINE));
   }
}
