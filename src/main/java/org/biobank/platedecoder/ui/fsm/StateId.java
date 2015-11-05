package org.biobank.platedecoder.ui.fsm;

// the stateIds for states in SceneFsm
enum StateId {
   INITIAL_SCENE,
   USE_FLATBED_IMAGE,
   USE_FILESYSTEM_IMAGE,
   DECODE_IMAGE,
   DECODED_IMAGE_TUBES,

   // child states for USE_FLATBED_IMAGE
   SCAN_REGION,
   SCAN_PLATE;

}
