package org.biobank.platedecoder.ui.fsm;

// the events used by SceneFsm
enum Event {
   INIT,
   FILE_CHOSEN,
   SCAN_REGION_DEFINE,
   SCAN_AND_DECODE_WITH_PREVIOUS,
   MODIFY_FLATBED_CONFIG,
   MODIFY_DECODER_CONFIG,
   NEXT_SELECTED,
   BACK_SELECTED,
   FINISH_SELECTED;
}
