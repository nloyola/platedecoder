package org.biobank.platedecoder.dmscanlib;

import javafx.scene.shape.Rectangle;

public class ScanLibWia {

    /**
     * A utility function that returns the correct flatbed scanner bounding box for a MS Windows
     * WIA device.
     */
    public static Rectangle getWiaBoundingBox(final Rectangle scanBbox) {
        return new Rectangle(
            scanBbox.getX(),
            scanBbox.getY(),
            scanBbox.getWidth() - scanBbox.getX(),
            scanBbox.getHeight() - scanBbox.getY());
    }

}
