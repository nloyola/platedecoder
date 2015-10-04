package org.biobank.platedecoder.model;

public interface PlateWellHandler {

    public void wellSelected(PlateWell well,
                             boolean   selectedRegionEnd,
                             boolean   addToSelection);

}
