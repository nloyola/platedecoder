package org.biobank.platedecoder.dmscanlib;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contains the results of an attempt to scan and decode an image with the flatbed scanner.
 *
 * The method {@link #getDecodedWells()} returns the set of wells that were successfully decoded. If
 * an image was not scanned successfully then this set is empty.
 *
 * @author Nelson Loyola
 *
 */
public class DecodeResult extends ScanLibResult {

    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(DecodeResult.class);

    private final Set<DecodedWell> wells = new TreeSet<DecodedWell>();

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
        for (DecodedWell well : set) {
            if (!well.getMessage().isEmpty()) {
                result.put(well.getLabel(), well.getMessage());
            }
        }
        return result;
    }

    private static Map<String, String> inventoryIdToLabelMap(Set<DecodedWell> set) {
        Map<String, String> result = new HashMap<>();
        for (DecodedWell well : set) {
            if (!well.getMessage().isEmpty()) {
                result.put(well.getMessage(), well.getLabel());
            }
        }
        return result;
    }

    /**
     * Returns true if the two decode results can be merged.
     *
     * Two results can be merged if the labels of the non blank inventory ids in the former match
     * all the non-blank in the latter. They are also valid if an inventory id is blank in the
     * former and the inventory id is non blank in the latter.
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
