package org.biobank.platedecoder.ui;

import org.biobank.platedecoder.ui.scene.SceneRoot;

/**
 * An interface that allows for changing the scene (the window) the application is displaying.
 *
 */
public interface SceneChanger {

   /**
    * Changes the scene the application is displaying.
    *
    * @param <T> A subclass of {@link SceneRoot} that creates and manages the scene.
    *
    * @param sceneRoot The new scene to be displayed.
    */
   public <T extends SceneRoot> void changeScene(T sceneRoot);

   /**
    * Makes a request to close the application.
    */
   public void closeApplicationRequest();

}
