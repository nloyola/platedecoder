package org.biobank.platedecoder.ui;

import org.biobank.platedecoder.model.DriverType;
import org.biobank.platedecoder.model.PlateModel;

import javafx.beans.property.ObjectProperty;

public class ScannerDriverTypeChooser extends RadioButtonChooser<DriverType> {

   private final PlateModel model = PlateModel.getInstance();

   public ScannerDriverTypeChooser(ObjectProperty<DriverType> property) {
      super("Driver Type", property);

      final DriverType lastUsedDriverType = model.getDriverType();

      for (DriverType driverType : DriverType.values()) {
         if (driverType != DriverType.NONE) {
            addButton(driverType.toString(), driverType, (lastUsedDriverType == driverType));
         }
      }
   }

}
