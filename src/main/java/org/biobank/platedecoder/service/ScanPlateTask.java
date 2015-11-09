package org.biobank.platedecoder.service;

import org.biobank.platedecoder.dmscanlib.ScanLibResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScanPlateTask extends ScanAndDecodeImageTask {

   @SuppressWarnings("unused")
   private static final Logger LOG = LoggerFactory.getLogger(ScanPlateTask.class);

   public ScanPlateTask(long dpi) {
      super(null, dpi, null, null, null, 0L, null, null);
   }

   @Override
   protected ScanLibResult call() throws Exception {
      return scanPlate();
   }
}
