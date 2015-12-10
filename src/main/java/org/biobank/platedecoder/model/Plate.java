package org.biobank.platedecoder.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Contains infomation of the tubes present in the plate.
 *
 */
public class Plate implements PlateWellHandler {

   //@SuppressWarnings("unused")
   private static final Logger LOG = LoggerFactory.getLogger(Plate.class);

   private final int rows;

   private final int cols;

   private final Map<String, PlateWell> plateWellMap = new HashMap<>();

   private final Stream<PlateWell> wellsStream;

   private Set<PlateWell> selectedWells = new HashSet<>();

   private PlateWell regionStartWell;

   public Plate(PlateType plateType) {
      rows = plateType.getRows();
      cols = plateType.getCols();

      for (int r = 0; r < rows; ++r) {
         for (int c = 0; c < cols; ++c) {
            SbsPosition position = new SbsPosition(r, c);
            String label = position.getLabel();
            plateWellMap.put(label, new PlateWell(this, r, c, label));
         }
      }

      PlateWell [] plateWellsArray = plateWellMap.values().toArray(new PlateWell [] {});
      wellsStream = Arrays.stream(plateWellsArray);
   }

   public Set<PlateWell> getWells() {
      return new HashSet<>(plateWellMap.values());
   }

   public PlateWell getWell(String label) {
      PlateWell well = plateWellMap.get(label);
      if (well == null) {
         throw new IllegalArgumentException("well with label not found: " + label);
      }
      return well;
   }

   public void setWellInventoryId(String label, String inventoryId) {
      PlateWell well = plateWellMap.get(label);
      if (well == null) {
         throw new IllegalArgumentException("could not find well with label: " + label);
      }
      well.setInventoryId(inventoryId);
   }

   public List<PlateWell> getSelected() {
      return wellsStream.filter(w -> w.isSelected()).collect(Collectors.toList());
   }

   @Override
   public void wellSelected(PlateWell well,
                            boolean   selectedRegionEnd,
                            boolean   addToSelection) {
      LOG.debug("well: {}, selectedRegionEnd: {}, addToSelection: {}",
                new Object [] { well, selectedRegionEnd, addToSelection });

      if (!selectedRegionEnd && !addToSelection) {
         // deselet all but the selected cell
         selectedWells.stream().filter(w -> w != well).forEach(w -> w.setSelected(false));

         selectedWells = new HashSet<>(Arrays.asList(well));
         well.setSelected(true);
         regionStartWell = well;
      } else if (selectedRegionEnd) {
         if (regionStartWell != null) {
            int startRow = regionStartWell.getRow(),
               startCol = regionStartWell.getCol(),
               endRow = well.getRow(),
               endCol = well.getCol();

            int minRow = Math.min(startRow, endRow),
               minCol = Math.min(startCol, endCol),
               maxRow = Math.max(startRow, endRow),
               maxCol = Math.max(startCol, endCol);

            selectedWells.addAll(selectWellRange(minRow, minCol, maxRow, maxCol));
            selectedWells.stream().forEach(w -> w.setSelected(true));
         }
      } else {
         selectedWells.add(well);
         well.setSelected(!well.isSelected());
         regionStartWell = well;
      }
   }

   private Set<PlateWell> selectWellRange(int startRow,
                                          int startCol,
                                          int endRow,
                                          int endCol) {
      Set<PlateWell> result = new HashSet<>();
      for (int row = startRow; row <= endRow; ++row) {
         for (int col = startCol; col <= endCol; ++col) {
            SbsPosition position = new SbsPosition(row, col);
            result.add(plateWellMap.get(position.getLabel()));
         }
      }
      return result;
   }
}
