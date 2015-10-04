package org.biobank.platedecoder.model;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.util.Pair;

public class PlateWell implements Comparable<PlateWell> {

    private final PlateWellHandler plateWellHandler;

    private final int row;

    private final int col;

    private final StringProperty label;

    private StringProperty inventoryId;

    private BooleanProperty selectedProperty = new SimpleBooleanProperty(false);

    private BooleanProperty filledProperty = new SimpleBooleanProperty(false);

    public PlateWell(PlateWellHandler plateWellHandler, int row, int col, String label) {
        this.plateWellHandler = plateWellHandler;
        this.inventoryId = null;
        this.label = new SimpleStringProperty(label);
        this.row = row;
        this.col = col;
        this.inventoryId = new SimpleStringProperty("");
        filledProperty.setValue(false);
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public String getLabel() {
        return label.getValue();
    }

    public StringProperty getLabelProperty() {
        return label;
    }

    public String getInventoryId() {
        return inventoryId.getValue();
    }

    public void setInventoryId(String inventoryId) {
        this.inventoryId.setValue(inventoryId);
        filledProperty.setValue(!inventoryId.isEmpty());
    }

    public StringProperty getInventoryIdProperty() {
        return inventoryId;
    }

    public boolean isSelected() {
        return selectedProperty.get();
    }

    public BooleanProperty getSelectedProperty() {
        return selectedProperty;
    }

    public BooleanProperty getFilledProperty() {
        return filledProperty;
    }

    public void setSelected(boolean selected) {
        this.selectedProperty.setValue(selected);
    }

    public void userSelected(boolean selectedRegionEnd, boolean addToSelection) {
        plateWellHandler.wellSelected(this, selectedRegionEnd, addToSelection);
    }

    @Override
    public String toString()  {
        StringBuffer buf = new StringBuffer();
        buf.append(label.getValue()).append(": [ ");
        buf.append(row).append(", ");
        buf.append(col).append(" ]: ");
        buf.append(inventoryId.getValue());
        return buf.toString();
    }

    @Override
    public int compareTo(PlateWell that) {
        Pair<Integer, Integer> thisPos = SbsLabeling.toRowCol(this.getLabel());
        Pair<Integer, Integer> thatPos = SbsLabeling.toRowCol(that.getLabel());
        if (thisPos.getKey().equals(thatPos.getKey())) {
            return thisPos.getValue().compareTo(thatPos.getValue());
        }
        return thisPos.getKey().compareTo(thatPos.getKey());
    }
}
