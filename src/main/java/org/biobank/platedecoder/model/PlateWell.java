package org.biobank.platedecoder.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class PlateWell {

    private final Plate parent;

    private String inventoryId;

    private final String label;

    private final int row;

    private final int col;

    private BooleanProperty selectedProperty = new SimpleBooleanProperty(false);

    public PlateWell(Plate parent, int row, int col, String label) {
        this.parent = parent;
        this.inventoryId = null;
        this.label = label;
        this.row = row;
        this.col = col;
        this.inventoryId = "";
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public String getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(String inventoryId) {
        this.inventoryId = inventoryId;
    }

    public String getLabel() {
        return label;
    }

    public boolean isSelected() {
        return selectedProperty.get();
    }

    public BooleanProperty getSelectedProperty() {
        return selectedProperty;
    }

    public void setSelected(boolean selected) {
        this.selectedProperty.setValue(selected);
    }

    public void userSelected(boolean selectedRegionEnd, boolean addToSelection) {
        parent.wellSelected(this, selectedRegionEnd, addToSelection);
    }

    @Override
    public String toString()  {
        StringBuffer sb = new StringBuffer();
        sb.append(label).append(": ").append(inventoryId);
        return sb.toString();
    }
}
