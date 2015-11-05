package org.biobank.platedecoder.model;

/**
 * The type of TWAIN driver being used to access the flatbed scanner.
 *
 * <p>When the driver type is a WIA driver, defining the region to scan is different. Therefore, the
 * application needs to keep track of this.
 *
 */
public enum DriverType {
   /** Setting has not been selected by the user yet. */
   NONE,

   /** The TWAIN driver is of type WIA. */
   WIA,

   /** The TWAIN is not a WIA type. */
   TWAIN;
}
