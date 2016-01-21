package org.biobank.platedecoder.service;

import org.biobank.dmscanlib.DeviceNamesResult;
import org.biobank.dmscanlib.ScanLib;

import javafx.concurrent.Task;

public class DeviceNamesTask extends Task<DeviceNamesResult> {

   public DeviceNamesTask() {
   }

   @Override
   protected DeviceNamesResult call() throws Exception {
      return ScanLib.getInstance().getDeviceNames();
   }

}
