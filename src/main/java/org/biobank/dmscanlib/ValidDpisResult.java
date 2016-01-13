package org.biobank.dmscanlib;

import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contains the valid DPIs that can be used with the selected scanner.
 *
 * <p>The method {@link #getValidDpis getValidDpis} returns the set of valid DPIs.
 */
public class ValidDpisResult extends ScanLibResult {

   @SuppressWarnings("unused")
   private static final Logger LOG = LoggerFactory.getLogger(ValidDpisResult.class);

   private final Set<Integer> validDpis = new TreeSet<Integer>();

   /**
    * Stores the valid DPIs that can be used with the selected scanner.
    *
    * <p>Meant to be used by scanning library JNI only. Objects of this type are created by the
    * scanning library when the scanning device is queried. See {@link
    * org.biobank.dmscanlib.ScanLib#getValidDpis getValidDpis}.
    *
    * <p>The scanner can be selected by calling {@link
    * org.biobank.dmscanlib.ScanLib.selectSourceasdefault selectSourceasdefault} in MS Windows, or
    * {@link * org.biobank.dmscanlib.ScanLib.selectDevice selectDevice}.
    *
    * @param resultCode  See {@link org.biobank.dmscanlib.ScanLib.ResultCode ResultCode}
    *
    * @param message  The string representation of the resultCode.
    */
   public ValidDpisResult(int resultCode, String message) {
      super(resultCode, message);
   }

   public void addDpi(int dpi) {
      validDpis.add(dpi);
   }

   public Set<Integer> getValidDpis() {
      return validDpis;
   }
}
