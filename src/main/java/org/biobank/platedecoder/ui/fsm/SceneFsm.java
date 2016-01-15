package org.biobank.platedecoder.ui.fsm;

import java.util.Optional;

import org.biobank.platedecoder.model.ImageSource;
import org.biobank.platedecoder.model.ImageSourceFileSystem;
import org.biobank.platedecoder.model.ImageSourceFlatbedScanner;
import org.biobank.platedecoder.model.PlateDecoderPreferences;
import org.biobank.platedecoder.model.PlateModel;
import org.biobank.platedecoder.service.fsm.Fsm;
import org.biobank.platedecoder.service.fsm.TransitionRunnable;
import org.biobank.platedecoder.ui.SceneChanger;
import org.biobank.platedecoder.ui.scene.DecodeImageScene;
import org.biobank.platedecoder.ui.scene.DecodedTubes;
import org.biobank.platedecoder.ui.scene.decodersettings.DecoderSettings;
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

   //@SuppressWarnings("unused")
   private static final Logger LOG = LoggerFactory.getLogger(SceneFsm.class);

   protected PlateModel model = PlateModel.getInstance();

   private ImageSource imageSource;

   private final SceneChanger sceneChanger;

   private final TransitionRunnable backToInitialSceneRunnable;

   private boolean flatbedScannerUsed = false;
   private boolean decodeWithPreviousSettings = false;

   public SceneFsm(SceneChanger sceneChanger) {
      this.sceneChanger = sceneChanger;

      backToInitialSceneRunnable = () -> sceneChanger.changeScene(createInitialScene());

      addState(StateId.INITIAL_SCENE);
      addState(StateId.USE_FILESYSTEM_IMAGE);
      addState(StateId.USE_FLATBED_IMAGE);
      addState(StateId.SCAN_REGION, StateId.USE_FLATBED_IMAGE);
      addState(StateId.SCAN_PLATE, StateId.USE_FLATBED_IMAGE);
      addState(StateId.DECODE_IMAGE);
      addState(StateId.DECODED_IMAGE_TUBES);
      addState(StateId.SETTINGS_FLATBED_SCANNER);
      addState(StateId.SETTINGS_SCAN_REGION);
      addState(StateId.SETTINGS_DECODER);

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
      createSettingsScanRegionDefineTransitions();
      createDecoderSettingsTransitions();

      validate();

      sceneChanger.changeScene(createInitialScene());
   }

   private void createInitialSceneTransitions() {
      TransitionRunnable r = () -> {
         flatbedScannerUsed = true;
         decodeWithPreviousSettings = false;
         sceneChanger.changeScene(createScanRegionScene());
      };

      addTransition(Event.SCAN_REGION_DEFINE,
                    StateId.INITIAL_SCENE,
                    StateId.SCAN_REGION,
                    r);
      addTransition(Event.SCAN_REGION_DEFINE,
                    StateId.INITIAL_SCENE,
                    StateId.SCAN_REGION,
                    r);
      addTransition(Event.NEXT_SELECTED,
                    StateId.INITIAL_SCENE,
                    StateId.SCAN_PLATE,
                    () -> {
                       flatbedScannerUsed = true;
                       decodeWithPreviousSettings = false;
                       sceneChanger.changeScene(createScanPlateScene());
                    });
      addTransition(Event.SCAN_AND_DECODE_WITH_PREVIOUS,
                    StateId.INITIAL_SCENE,
                    StateId.DECODE_IMAGE,
                    () -> {
                       decodeWithPreviousSettings = true;
                       sceneChanger.changeScene(createDecodeImageScene());
                    });
      addTransition(Event.FILE_CHOSEN,
                    StateId.INITIAL_SCENE,
                    StateId.USE_FILESYSTEM_IMAGE,
                    () -> {
                       flatbedScannerUsed = false;
                       decodeWithPreviousSettings = false;
                       sceneChanger.changeScene(createFileChooseScene());
                    });
      addTransition(Event.MODIFY_FLATBED_CONFIG,
                    StateId.INITIAL_SCENE,
                    StateId.SETTINGS_FLATBED_SCANNER,
                    () -> sceneChanger.changeScene(createFlatbedScannerSettingsScene()));
      addTransition(Event.MODIFY_DECODER_CONFIG,
                    StateId.INITIAL_SCENE,
                    StateId.SETTINGS_DECODER,
                    () -> {
                       LOG.info("settings decoder state transition");
                       sceneChanger.changeScene(createDecoderSettingsScene());
                    });
      addTransition(Event.FINISH_SELECTED,
                    StateId.INITIAL_SCENE,
                    StateId.INITIAL_SCENE,
                    () -> sceneChanger.closeApplicationRequest());
   }

   private void createFileChooserSceneTransitions() {
      addTransition(Event.NEXT_SELECTED,
                    StateId.USE_FILESYSTEM_IMAGE,
                    StateId.DECODE_IMAGE,
                    () -> sceneChanger.changeScene(createDecodeImageScene()));
      addTransition(Event.BACK_SELECTED,
                    StateId.USE_FILESYSTEM_IMAGE,
                    StateId.INITIAL_SCENE,
                    backToInitialSceneRunnable);
      addTransition(Event.FINISH_SELECTED,
                    StateId.USE_FILESYSTEM_IMAGE,
                    StateId.INITIAL_SCENE,
                    () -> sceneChanger.closeApplicationRequest());

   }

   private void createUseFlatbedImageTransitions() {
      addTransition(Event.NEXT_SELECTED,
                    StateId.SCAN_REGION,
                    StateId.SCAN_PLATE,
                    () -> sceneChanger.changeScene(createScanPlateScene()));

      addTransition(Event.BACK_SELECTED,
                    StateId.USE_FLATBED_IMAGE,
                    StateId.INITIAL_SCENE,
                    backToInitialSceneRunnable);

      addTransition(Event.NEXT_SELECTED,
                    StateId.SCAN_PLATE,
                    StateId.DECODE_IMAGE,
                    () -> {
                       model.createNewPlate();
                       sceneChanger.changeScene(createDecodeImageScene());
                    });

   }

   private void createDecodeImageSceneTransitions() {
      addTransition(Event.NEXT_SELECTED,
                    StateId.DECODE_IMAGE,
                    StateId.DECODED_IMAGE_TUBES,
                    () -> sceneChanger.changeScene(createDecodedTubesScene()));
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
         backToInitialSceneRunnable);
      addTransitionFromChoiceToState(
         ChoicepointId.FLATBED_IMAGE_SELECTED,
         false,
         StateId.USE_FILESYSTEM_IMAGE,
         () -> sceneChanger.changeScene(createFileChooseScene()));
      addTransitionFromChoiceToState(
         ChoicepointId.FLATBED_IMAGE_SELECTED,
         true,
         StateId.SCAN_PLATE,
         () -> sceneChanger.changeScene(createScanPlateScene()));
   }

   private void createDecodedTubesSceneTransitions() {
      addTransition(Event.BACK_SELECTED,
                    StateId.DECODED_IMAGE_TUBES,
                    StateId.DECODE_IMAGE,
                    () -> sceneChanger.changeScene(createDecodeImageScene()));
      addTransition(Event.FINISH_SELECTED,
                    StateId.DECODED_IMAGE_TUBES,
                    StateId.INITIAL_SCENE,
                    () -> sceneChanger.closeApplicationRequest());
   }

   private void createFlatbedScannerSettingsTransitions() {
      addTransition(Event.BACK_SELECTED,
                    StateId.SETTINGS_FLATBED_SCANNER,
                    StateId.INITIAL_SCENE,
                    backToInitialSceneRunnable);
      addTransition(Event.NEXT_SELECTED,
                    StateId.SETTINGS_FLATBED_SCANNER,
                    StateId.INITIAL_SCENE,
                    backToInitialSceneRunnable
                    );
      addTransition(Event.SCAN_REGION_DEFINE,
                    StateId.SETTINGS_FLATBED_SCANNER,
                    StateId.SETTINGS_SCAN_REGION,
                    () -> sceneChanger.changeScene(createScanRegionScene()));

   }

   private void createSettingsScanRegionDefineTransitions() {
      addTransition(Event.NEXT_SELECTED,
                    StateId.SETTINGS_SCAN_REGION,
                    StateId.SETTINGS_FLATBED_SCANNER,
                    () -> sceneChanger.changeScene(createFlatbedScannerSettingsScene()));

      addTransition(Event.BACK_SELECTED,
                    StateId.SETTINGS_SCAN_REGION,
                    StateId.SETTINGS_FLATBED_SCANNER,
                    () -> sceneChanger.changeScene(createFlatbedScannerSettingsScene()));
   }

   private void createDecoderSettingsTransitions() {
      addTransition(Event.BACK_SELECTED,
                    StateId.SETTINGS_DECODER,
                    StateId.INITIAL_SCENE,
                    backToInitialSceneRunnable);
      addTransition(Event.NEXT_SELECTED,
                    StateId.SETTINGS_DECODER,
                    StateId.INITIAL_SCENE,
                    backToInitialSceneRunnable
                    );
   }

   private InitialScene createInitialScene() {
      InitialScene scene = new InitialScene();

      scene.onFilesystemAction(() -> feedEvent(Event.FILE_CHOSEN));

      scene.onFlatbedScanAction(() -> {
            Optional<Rectangle> rectMaybe =
               PlateDecoderPreferences.getInstance().getScanRegion();
            if (rectMaybe.isPresent()) {
               feedEvent(Event.NEXT_SELECTED);
            } else {
               feedEvent(Event.SCAN_REGION_DEFINE);
            }
         });

      scene.onFlatbedScanWithPreviousParamsAction(() -> {
            imageSource = ImageSourceFlatbedScanner.getInstance();
            feedEvent(Event.SCAN_AND_DECODE_WITH_PREVIOUS);
         });

      scene.modifyFlatbedConfigAction(() -> feedEvent(Event.MODIFY_FLATBED_CONFIG));
      scene.modifyDecoderConfigAction(() -> feedEvent(Event.MODIFY_DECODER_CONFIG));
      scene.enableFinishAction(() -> feedEvent(Event.FINISH_SELECTED));

      return scene;
   }

   private FileChoose createFileChooseScene() {
      FileChoose scene = new FileChoose();

      scene.enableBackAction(() -> feedEvent(Event.BACK_SELECTED));

      scene.onDecodeAction(() -> {
            imageSource = new ImageSourceFileSystem(scene.getSelectedFileURI());
            feedEvent(Event.NEXT_SELECTED);
         });

      return scene;
   }

   private ScanRegionScene createScanRegionScene() {
      ScanRegionScene scene = new ScanRegionScene();
      scene.enableBackAction(() -> feedEvent(Event.BACK_SELECTED));
      scene.enableNextAction(() -> feedEvent(Event.NEXT_SELECTED));
      return scene;
   }

   private ScanPlateScene createScanPlateScene() {
      ScanPlateScene scene = new ScanPlateScene();
      scene.enableNextAction(() -> {
            imageSource = ImageSourceFlatbedScanner.getInstance();
            feedEvent(Event.NEXT_SELECTED);
         });
      scene.enableBackAction(() -> feedEvent(Event.BACK_SELECTED));
      return scene;
   }

   private DecodeImageScene createDecodeImageScene() {
      DecodeImageScene scene  = new DecodeImageScene();
      scene.setImageSource(imageSource);
      scene.enableBackAction(() -> feedEvent(Event.BACK_SELECTED));
      scene.enableNextAction(() -> feedEvent(Event.NEXT_SELECTED));
      return scene;
   }

   private DecodedTubes createDecodedTubesScene() {
      DecodedTubes scene = new DecodedTubes();
      scene.enableBackAction(() -> feedEvent(Event.BACK_SELECTED));
      scene.enableFinishAction(() -> feedEvent(Event.FINISH_SELECTED));
      return scene;
   }

   private final FlatbedScannerSettings createFlatbedScannerSettingsScene() {
      FlatbedScannerSettings scene = new FlatbedScannerSettings();
      scene.enableBackAction(() -> feedEvent(Event.BACK_SELECTED));
      scene.enableNextAction(() -> feedEvent(Event.NEXT_SELECTED));
      scene.onDefineScanRegionAction(() -> feedEvent(Event.SCAN_REGION_DEFINE));
      return scene;
   }

   private DecoderSettings createDecoderSettingsScene() {
      DecoderSettings scene = new DecoderSettings();
      scene.enableBackAction(() -> feedEvent(Event.BACK_SELECTED));
      scene.enableNextAction(() -> feedEvent(Event.NEXT_SELECTED));
      return scene;
   }
}
