package org.biobank.platedecoder.ui.fsm;

import org.biobank.platedecoder.ui.SceneChanger;

/**
 * A factory class for creating the state machines that handles the scenes and switching between
 * them.
 */
public class SceneFsmFactory {

   /**
    * Returns a state machine that handles switching between scenes.
    *
    * @param sceneChanger the interface that changes the scene the application is displaying.
    *
    * @return The scene state machine.
    */
   public static SceneFsm createSceneFsm(SceneChanger sceneChanger) {
      return new SceneFsm(sceneChanger);
   }

}
