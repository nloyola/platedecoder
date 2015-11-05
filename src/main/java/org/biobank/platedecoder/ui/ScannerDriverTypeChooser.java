package org.biobank.platedecoder.ui;

import org.biobank.platedecoder.model.DriverType;
import org.biobank.platedecoder.model.PlateModel;

public class ScannerDriverTypeChooser extends RadioButtonChooser<DriverType> {

   private final PlateModel model = PlateModel.getInstance();

   public ScannerDriverTypeChooser() {
        super("Driver Type");

        final DriverType lastUsedDriverType = model.getDriverType();

        for (DriverType driverType : DriverType.values()) {
            addButton(driverType.toString(), driverType, (lastUsedDriverType == driverType));
        }
   }

   @Override
   protected void setValue(DriverType driverType) {
      model.setDriverType(driverType);
   }

}
