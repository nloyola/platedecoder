package org.biobank.platedecoder.model;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Central point of storage for the data used by the application.
 */
public class PlateModel {

   @SuppressWarnings("unused")
   private static final Logger LOG = LoggerFactory.getLogger(PlateModel.class);

   // Where the plate's information is stored.
   private Plate plate;

   // the list of plate types that are possible
   public ObservableList<PlateType> plateTypes =
      FXCollections.observableArrayList(Arrays.asList(PlateType.values()));


   // The type of plate currently selected. I.e. the number of wells it contains.
   private ObjectProperty<PlateType> plateTypeProperty;

   // The orientation of the plate when it was scanned or a picture taken of it.
   private ObjectProperty<PlateOrientation> plateOrientationProperty;

   // If the barcodes are present on the tube tops or bottoms.
   private ObjectProperty<BarcodePosition> barcodePositionProperty;

   // The TWAIN driver type
   private ObjectProperty<DriverType> driverTypeProperty;

   // The DPI used when the flatbed scanner was used to take an image of the plate.
   private ObjectProperty<FlatbedDpi> flatbedDpiProperty;

   private PlateModel() {
      plateTypeProperty = new SimpleObjectProperty<PlateType>(
         PlateDecoderPreferences.getInstance().getPlateType());

      plateOrientationProperty = new SimpleObjectProperty<PlateOrientation>(
         PlateDecoderPreferences.getInstance().getPlateOrietation());

      barcodePositionProperty = new SimpleObjectProperty<BarcodePosition>(
         PlateDecoderPreferences.getInstance().getBarcodePosition());

      plateTypeProperty.addListener((observable, oldValue, newValue) -> {
            PlateDecoderPreferences.getInstance().setPlateType(newValue);
            createNewPlate();
         });
      plateOrientationProperty.addListener((observable, oldValue, newValue) -> {
            PlateDecoderPreferences.getInstance().setPlateOrientation(newValue);
            createNewPlate();
         });
      barcodePositionProperty.addListener((observable, oldValue, newValue) -> {
            PlateDecoderPreferences.getInstance().setBarcodePosition((newValue));
            createNewPlate();
         });

      flatbedDpiProperty = new SimpleObjectProperty<FlatbedDpi>(
         PlateDecoderPreferences.getInstance().getFlatbedDpi());

      createNewPlate();
   }

   /**
    * This object is a singleton. Use this method to access it.
    *
    * @return the reference to this singleton.
    */
   public static PlateModel getInstance() {
      return PlateModelHolder.INSTANCE;
   }

   /**
    * Used to set the plate's type.
    *
    * @param plateType  The value to assign to the plate's type.
    *
    * @see #getPlateType
    */
   public void setPlateType(PlateType plateType) {
      plateTypeProperty.setValue(plateType);
   }

   /**
    * Used to get the plate's type.
    *
    * @return The plate's type.
    *
    * @see #setPlateType setPlateType
    */
   public PlateType getPlateType() {
      return plateTypeProperty.getValue();
   }

   /**
    * The property that holds the plate's type.
    *
    * @return The property that holds the plate's type.
    *
    * @see #getPlateType
    * @see #setPlateType setPlateType
    */
   public ObjectProperty<PlateType> getPlateTypeProperty() {
      return plateTypeProperty;
   }

   /**
    * Used to set the plate's orientation.
    *
    * @param orientation  The value to assign to the plate's orientation.
    *
    * @see #getPlateOrientation
    */
   public void setPlateOrientation(PlateOrientation orientation) {
      plateOrientationProperty.setValue(orientation);
   }

   /**
    * Used to get the plate's orientation.
    *
    * @return The plate's orientation.
    *
    * @see #setPlateOrientation setPlateOrientation
    */
   public PlateOrientation getPlateOrientation() {
      return plateOrientationProperty.getValue();
   }

   /**
    * The property that holds the plate's orientation.
    *
    * @return The property that holds the plate's orientation.
    *
    * @see #getPlateOrientation
    * @see #setPlateOrientation setPlateOrientation
    */
   public ObjectProperty<PlateOrientation> getPlateOrientationProperty() {
      return plateOrientationProperty;
   }

   /**
    * Used to set the position of the barcodes.
    *
    * @param position  Either tube tops or tube bottoms.
    *
    * @see #getBarcodePosition
    */
   public void setBarcodePosition(BarcodePosition position) {
      barcodePositionProperty.setValue(position);
   }

   /**
    * Used to get the positions of the barcodes.
    *
    * @return The position of the barcodes.
    *
    * @see #setBarcodePosition setBarcodePosition
    */
   public BarcodePosition getBarcodePosition() {
      return barcodePositionProperty.getValue();
   }

   /**
    * The property that holds the position of the barcodes.
    *
    * @return The property that holds the position of the barcodes.
    *
    * @see #getBarcodePosition
    * @see #setBarcodePosition setBarcodePosition
    */
   public ObjectProperty<BarcodePosition> getBarcodePositionProperty() {
      return barcodePositionProperty;
   }

   public void createNewPlate() {
      plate = new Plate(plateTypeProperty.getValue());
   }

   /**
    * The plate contains infomation of the tubes present in the plate.
    *
    * @return the plate object.
    */
   public Plate getPlate() {
      return plate;
   }

   /**
    * The TWAIN driver type used to access the flatbed scanner.
    *
    * @return The TWAIN driver type.
    */
   public DriverType getDriverType() {
      return driverTypeProperty.getValue();
   }

   /**
    * The TWAIN driver type used to access the flatbed scanner.
    *
    * @param driverType The TWAIN driver type.
    */
   public void setDriverType(DriverType driverType) {
      driverTypeProperty.setValue(driverType);
   }

   /**
    * The DPI (dots per inch) that was used to scan an image of the plate.
    *
    * @return the DPI used to scan the image.
    */
   public FlatbedDpi getFlatbedDpi() {
      return flatbedDpiProperty.getValue();
   }

   /**
    * Assign the DPI (dots per inch) that was used to scan the image of the plate.
    *
    * @param dpi The DPI value.
    */
   public void setFlatbedDpi(FlatbedDpi dpi) {
      flatbedDpiProperty.set(dpi);
   }

   private static class PlateModelHolder {
      private static final PlateModel INSTANCE = new PlateModel();
   }
}
