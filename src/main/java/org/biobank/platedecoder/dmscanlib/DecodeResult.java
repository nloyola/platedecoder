package org.biobank.platedecoder.dmscanlib;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contains the results of an attempt to decode regions of a plate image.
 *
 * <p>The method {@link #getDecodedWells getDecodedWells} returns the set of wells that were
 * successfully decoded.
 *
 * @author Nelson Loyola
 *
 */
public class DecodeResult extends ScanLibResult {

   @SuppressWarnings("unused")
   private static final Logger LOG = LoggerFactory.getLogger(DecodeResult.class);

   private final Set<DecodedWell> wells = new TreeSet<DecodedWell>();

   /**
    * Stores the results when an image of a plate is decoded.
    *
    * <p>Meant for the scanning library JNI. Object of this type are created by the scanning library
    * when it decodes an image.
    *
    * @param resultCode  See {@link org.biobank.platedecoder.dmscanlib.ScanLib.ResultCode ResultCode}
    *
    * @param value  Same as resultCode. This value is deprecated.
    *
    * @param message  The string representation of the resultCode.
    *
    */
   public DecodeResult(int resultCode, int value, String message) {
      super(resultCode, value, message);
   }

   public void addWell(String label, String message) {
      wells.add(new DecodedWell(label, message));
   }

   /**
    * Gets the set of wells that were successfully decoded.
    *
    * @return the set of wells that were successfully decoded.
    */
   public Set<DecodedWell> getDecodedWells() {
      return wells;
   }

   private static Map<String, String> labelToInventoryIdMap(Set<DecodedWell> set) {
      Map<String, String> result = new HashMap<>();
      set.forEach(well -> {
            if (!well.getMessage().isEmpty()) {
               result.put(well.getLabel(), well.getMessage());
            }
         });
      return result;
   }

   private static Map<String, String> inventoryIdToLabelMap(Set<DecodedWell> set) {
      Map<String, String> result = new HashMap<>();
      set.forEach(well -> {
            if (!well.getMessage().isEmpty()) {
               result.put(well.getMessage(), well.getLabel());
            }
         });
      return result;
   }

   /**
    * Returns true if the two decode results can be merged.
    *
    * Two results can be merged if the labels of the non blank inventory ids in the former match
    * all the non-blank in the latter. They are also valid if an inventory id is blank in the
    * former and the inventory id is non blank in the latter.
    *
    * @param former  The former of the decode results.
    *
    * @param latter  The latter of the decode results.
    *
    * @return {@code true} if the results can be merged.
    */
   public static boolean compareDecodeResults(Set<DecodedWell> former, Set<DecodedWell> latter) {
      // ensure each label contains the same inventory id
      Map<String, String> formerLabelToInventoryId = labelToInventoryIdMap(former);
      Map<String, String> latterLabelToInventoryId = labelToInventoryIdMap(latter);

      for (Entry<String, String> entry : formerLabelToInventoryId.entrySet()) {
         String formerLabel = entry.getKey();
         String formerInventoryId = entry.getValue();
         String latterInventoryId = latterLabelToInventoryId.get(formerLabel);

         if ((latterInventoryId != null) && !formerInventoryId.equals(latterInventoryId)) {
            return false;
         }
      }

      Map<String, String> formerInventoryIdToLabel = inventoryIdToLabelMap(former);
      Map<String, String> latterInventoryIdToLabel = inventoryIdToLabelMap(latter);

      for (Entry<String, String> entry : formerInventoryIdToLabel.entrySet()) {
         String formerInventoryId = entry.getKey();
         String formerLabel = entry.getValue();
         String latterLabel = latterInventoryIdToLabel.get(formerInventoryId);

         if ((latterLabel != null) && !latterLabel.equals(formerLabel)) {
            return false;
         }
      }

      return true;
   }

}
