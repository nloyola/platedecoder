package org.biobank.platedecoder.ui.fsm;

import java.util.Optional;

import org.biobank.platedecoder.model.PlateDecoderPreferences;
import org.biobank.platedecoder.service.fsm.Fsm;
import org.biobank.platedecoder.ui.PlateDecoder;
import org.biobank.platedecoder.ui.SceneChanger;
import org.biobank.platedecoder.ui.scene.DecodeImageScene;
import org.biobank.platedecoder.ui.scene.DecodedTubes;
import org.biobank.platedecoder.ui.scene.FileChoose;
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

      addChoicepoint(
         ChoicepointId.FLATBED_IMAGE_SELECTED,
         () -> {
            return flatbedScannerUsed;
         });
      addChoicepoint(
         ChoicepointId.FLATBED_IMAGE_SELECTED_WITH_PREV_SETTINGS,
         () -> {
            return decodeWithPreviousSettings;
         });

      createInitialSceneTransitions();
      createFileChooserSceneTransitions();
      createUseFlatbedImageTransitions();
      createDecodeImageSceneTransitions();
      createDecodedTubesSceneTransitions();

      validate();

      sceneChanger.changeScene(initialScene);
   }

   private void createInitialSceneTransitions() {
      addTransition(Event.SCAN_REGION_DEFINE,
                    StateId.INITIAL_SCENE,
                    StateId.SCAN_REGION,
                    () -> {
                       flatbedScannerUsed = true;
                       sceneChanger.changeScene(scanRegion);
                    });
      addTransition(Event.SCAN_REGION_DEFINE,
                    StateId.INITIAL_SCENE,
                    StateId.SCAN_REGION,
                    () -> {
                       flatbedScannerUsed = true;
                       decodeWithPreviousSettings = false;
                       sceneChanger.changeScene(scanRegion);
                    });
      addTransition(Event.SCAN_AND_DECODE,
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

      initialScene.onFilesystemAction(e -> {
            feedEvent(Event.FILE_CHOSEN);
         });

      initialScene.onFlatbedScanAction(e -> {
            Optional<Rectangle> rectMaybe =
               PlateDecoderPreferences.getInstance().getScanRegion();
            if (rectMaybe.isPresent()) {
               feedEvent(Event.SCAN_AND_DECODE);
            } else {
               feedEvent(Event.SCAN_REGION_DEFINE);
            }
         });

      initialScene.onFlatbedScanWithPreviousParamsAction(e -> {
            decodeImage.setImageFileURI(PlateDecoder.flatbedPlateImageFilenameToUrl());
            feedEvent(Event.SCAN_AND_DECODE_WITH_PREVIOUS);
         });

      initialScene.modifyConfigrationAction(e -> {
            feedEvent(Event.MODIFY_CONFIG);
         });
   }

   private void createFileChooserSceneTransitions() {
      addTransition(Event.IMAGE_SELECTED,
                    StateId.USE_FILESYSTEM_IMAGE,
                    StateId.DECODE_IMAGE,
                    () -> {
                       sceneChanger.changeScene(decodeImage);
                    });
      addTransition(Event.BACK_SELECTED,
                    StateId.USE_FILESYSTEM_IMAGE,
                    StateId.INITIAL_SCENE,
                    () -> {
                       sceneChanger.changeScene(initialScene);
                    });
      addTransition(Event.FINISH_SELECTED,
                    StateId.USE_FILESYSTEM_IMAGE,
                    StateId.INITIAL_SCENE,
                    () -> {
                       sceneChanger.closeApplicationRequest();
                    });

      fileChoose.enableBackAction(e -> {
            feedEvent(Event.BACK_SELECTED);
         });

      fileChoose.onDecodeAction(e -> {
            decodeImage.setImageFileURI(fileChoose.getSelectedFileURI());
            feedEvent(Event.IMAGE_SELECTED);
         });

   }

   private void createUseFlatbedImageTransitions() {
      addTransition(Event.SCAN_AND_DECODE,
                    StateId.SCAN_REGION,
                    StateId.SCAN_PLATE,
                    () -> {
                       sceneChanger.changeScene(scanPlate);
                    });

      addTransition(Event.BACK_SELECTED,
                    StateId.USE_FLATBED_IMAGE,
                    StateId.INITIAL_SCENE,
                    () -> {
                       sceneChanger.changeScene(initialScene);
                    });

      addTransition(Event.IMAGE_SCANNED,
                    StateId.SCAN_PLATE,
                    StateId.DECODE_IMAGE,
                    () -> {
                       sceneChanger.changeScene(decodeImage);
                    });

      scanRegion.onContinueAction(e -> {
            feedEvent(Event.SCAN_AND_DECODE);
         });
      scanRegion.enableBackAction(e -> {
            feedEvent(Event.BACK_SELECTED);
         });
      scanPlate.onScanCompleteAction(e -> {
            decodeImage.setImageFileURI(PlateDecoder.flatbedPlateImageFilenameToUrl());
            feedEvent(Event.IMAGE_SCANNED);
         });
      scanPlate.enableBackAction(e -> {
            feedEvent(Event.BACK_SELECTED);
         });
   }

   private void createDecodeImageSceneTransitions() {
      addTransition(Event.TUBES_DECODED,
                    StateId.DECODE_IMAGE,
                    StateId.DECODED_IMAGE_TUBES,
                    () -> {
                       sceneChanger.changeScene(decodedTubes);
                    });
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
         () -> {
            sceneChanger.changeScene(initialScene);
         });
      addTransitionFromChoiceToState(
         ChoicepointId.FLATBED_IMAGE_SELECTED,
         false,
         StateId.USE_FILESYSTEM_IMAGE,
         () -> {
            sceneChanger.changeScene(fileChoose);
         });
      addTransitionFromChoiceToState(
         ChoicepointId.FLATBED_IMAGE_SELECTED,
         true,
         StateId.SCAN_PLATE,
         () -> {
            sceneChanger.changeScene(scanPlate);
         });

      decodeImage.onContinueAction(e -> {
            feedEvent(Event.TUBES_DECODED);
         });

      decodeImage.enableBackAction(e -> {
            feedEvent(Event.BACK_SELECTED);
         });

   }

   private void createDecodedTubesSceneTransitions() {
      addTransition(Event.BACK_SELECTED,
                    StateId.DECODED_IMAGE_TUBES,
                    StateId.DECODE_IMAGE,
                    () -> {
                       sceneChanger.changeScene(decodeImage);
                    });
      addTransition(Event.FINISH_SELECTED,
                    StateId.DECODED_IMAGE_TUBES,
                    StateId.INITIAL_SCENE,
                    () -> {
                       sceneChanger.closeApplicationRequest();
                    });

      decodedTubes.enableBackAction(e -> {
            feedEvent(Event.BACK_SELECTED);
         });

      decodedTubes.enableFinishAction(e -> {
            feedEvent(Event.FINISH_SELECTED);
         });
   }
}
