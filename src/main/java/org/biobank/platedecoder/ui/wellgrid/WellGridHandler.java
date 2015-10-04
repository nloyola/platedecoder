package org.biobank.platedecoder.ui.wellgrid;

import javafx.scene.Cursor;

public interface WellGridHandler {

    public void setCursor(Cursor value);

    public void cellMoved(WellCell cell, double deltaX, double deltaY);

    public void manualDecode(WellCell cell);

}
