package org.biobank.dmscanlib;

import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contains the names of the flatbed scanners available on this computer.
 *
 * <p>The method {@link #getDeviceNames getDeviceNames} returns the set of device names.
 *
 */
public class DeviceNamesResult extends ScanLibResult {

   @SuppressWarnings("unused")
   private static final Logger LOG = LoggerFactory.getLogger(DeviceNamesResult.class);

   private final Set<String> deviceNames = new TreeSet<String>();

   /**
    * Stores the names of the flatbed scanners available on this computer.
    *
    * <p>Meant to be used by scanning library JNI only. . Objects of this type are created by the
    * scanning library when the device names are queried. See {@link
    * org.biobank.dmscanlib.ScanLib#getDeviceNames getDeviceNames}.
    *
    * @param resultCode  See {@link org.biobank.dmscanlib.ScanLib.ResultCode ResultCode}
    *
    * @param message  The string representation of the resultCode.
    */
   public DeviceNamesResult(int resultCode, String message) {
      super(resultCode, message);
   }

   public void addDeviceName(String deviceName) {
      deviceNames.add(deviceName);
   }

   public Set<String> getDeviceNames() {
      return deviceNames;
   }
}
