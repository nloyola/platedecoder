package org.biobank.platedecoder.model;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public enum PlateOrientation {
    LANDSCAPE("Landscape", "Landscape"),
    PORTRAIT("Portrait", "Portrait");

    public static final int size = values().length;

    private final String id;
    private final String displayLabel;

    private static final Map<String, PlateOrientation> ID_MAP;

    static {
        Map<String, PlateOrientation> map = new LinkedHashMap<String, PlateOrientation>();

        for (PlateOrientation orientationEnum : values()) {
            PlateOrientation check = map.get(orientationEnum.getId());
            if (check != null) {
                throw new IllegalStateException("pallet orientation value "
                    + orientationEnum.getId() + " used multiple times");
            }

            map.put(orientationEnum.getId(), orientationEnum);
        }

        ID_MAP = Collections.unmodifiableMap(map);
    }

    private PlateOrientation(String id, String displayLabel) {
        this.id = id;
        this.displayLabel = displayLabel;
    }

    public String getId() {
        return id;
    }

    public String getDisplayLabel() {
        return displayLabel;
    }

    public static Map<String, PlateOrientation> valuesMap() {
        return ID_MAP;
    }

    public static PlateOrientation getFromIdString(String id) {
        PlateOrientation result = valuesMap().get(id);
        if (result == null) {
            throw new IllegalStateException("invalid pallet orientation: " + id);
        }
        return result;
    }

}
