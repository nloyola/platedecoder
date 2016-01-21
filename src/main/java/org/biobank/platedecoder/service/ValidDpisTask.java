package org.biobank.platedecoder.service;

import org.biobank.dmscanlib.ScanLib;
import org.biobank.dmscanlib.ValidDpisResult;

import javafx.concurrent.Task;

public class ValidDpisTask extends Task<ValidDpisResult> {

   private final String deviceName;

   public ValidDpisTask(String deviceName) {
      this.deviceName = deviceName;
   }

   @Override
   protected ValidDpisResult call() throws Exception {
      return ScanLib.getInstance().getValidDpis(deviceName);
   }

}
