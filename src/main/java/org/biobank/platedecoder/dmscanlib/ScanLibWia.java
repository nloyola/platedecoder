package org.biobank.platedecoder.dmscanlib;

import javafx.scene.shape.Rectangle;

public class ScanLibWia {

    /**
     * A utility function that returns the correct flatbed scanner bounding box for an MS Windows
     * WIA device.
     *
     * <p>The WIA bounding box subtracts the left margin from the width, and the top margin from the
     * height. <em>Not sure why this is.</em>
     *
     * @param scanBbox  The bounding box to be adjusted.
     *
     * @return  The adjusted bounding box.
     */
    public static Rectangle getWiaBoundingBox(final Rectangle scanBbox) {
        return new Rectangle(
            scanBbox.getX(),
            scanBbox.getY(),
            scanBbox.getWidth() - scanBbox.getX(),
            scanBbox.getHeight() - scanBbox.getY());
    }

}
