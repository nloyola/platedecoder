package org.biobank.platedecoder.model;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Central point of storage for the data used by the application.
 */
public class PlateModel {

   @SuppressWarnings("unused")
   private static final Logger LOG = LoggerFactory.getLogger(PlateModel.class);

   /**
    * The minimum brightness value that can be used when scanning an image with the flatbed scanner.
    *
    * <p>This value is {@value #BRIGHTNESS_MINIMUM}.
    */
   public static final long BRIGHTNESS_MINIMUM = -1000;

   /**
    * The maximum brightness value that can be used when scanning an image with the flatbed scanner.
    *
    * <p>This value is {@value #BRIGHTNESS_MAXIMUM}.
    */
   public static final long BRIGHTNESS_MAXIMUM = 1000;

   /**
    * The minimum contrast value that can be used when scanning an image with the flatbed scanner.
    *
    * <p>This value is {@value #CONTRAST_MINIMUM}.
    */
   public static final long CONTRAST_MINIMUM = -1000;

   /**
    * The maximum contrast value that can be used when scanning an image with the flatbed scanner.
    *
    *
    * <p>This value is {@value #CONTRAST_MAXIMUM}.
    */
   public static final long CONTRAST_MAXIMUM = 1000;

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

   // The brightness setting for the flatbed scanner
   private LongProperty flatbedBrightnessProperty;

   // The contrast setting for the flatbed scanner
   private LongProperty flatbedContrastProperty;

   private PlateModel() {
      plateTypeProperty = new SimpleObjectProperty<PlateType>(
         PlateDecoderPreferences.getInstance().getPlateType());
      plateTypeProperty.addListener((observable, oldValue, newValue) -> {
            PlateDecoderPreferences.getInstance().setPlateType(newValue);
            createNewPlate();
         });

      barcodePositionProperty = new SimpleObjectProperty<BarcodePosition>(
         PlateDecoderPreferences.getInstance().getBarcodePosition());
      barcodePositionProperty.addListener((observable, oldValue, newValue) -> {
            PlateDecoderPreferences.getInstance().setBarcodePosition((newValue));
            createNewPlate();
         });

      plateOrientationProperty = new SimpleObjectProperty<PlateOrientation>(
         PlateDecoderPreferences.getInstance().getPlateOrietation());
      plateOrientationProperty.addListener((observable, oldValue, newValue) -> {
            PlateDecoderPreferences.getInstance().setPlateOrientation(newValue);
            createNewPlate();
         });

      driverTypeProperty = new SimpleObjectProperty<DriverType>(
         PlateDecoderPreferences.getInstance().getDriverType());
      driverTypeProperty.addListener((observable, oldValue, newValue) -> {
            PlateDecoderPreferences.getInstance().setFlatbedDriverType(newValue);
         });

      flatbedDpiProperty = new SimpleObjectProperty<FlatbedDpi>(
         PlateDecoderPreferences.getInstance().getFlatbedDpi());
      flatbedDpiProperty.addListener((observable, oldValue, newValue) -> {
            PlateDecoderPreferences.getInstance().setFlatbedDpi(newValue);
         });

      flatbedBrightnessProperty = new SimpleLongProperty(
         PlateDecoderPreferences.getInstance().getFlatbedBrightness());
      flatbedBrightnessProperty.addListener((observable, oldValue, newValue) -> {
            PlateDecoderPreferences.getInstance().setFlatbedBrightness(newValue.longValue());
         });

      flatbedContrastProperty = new SimpleLongProperty(
         PlateDecoderPreferences.getInstance().getFlatbedContrast());
      flatbedContrastProperty.addListener((observable, oldValue, newValue) -> {
            PlateDecoderPreferences.getInstance().setFlatbedContrast(newValue.longValue());
         });

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

   /**
    * The property that holds the last used flatbed DPI.
    *
    * @return The property that holds the last used flatbed DPI.
    *
    * @see #getFlatbedDpi
    * @see #getFlatbedDpi getFlatbedDpi
    */
   public ObjectProperty<FlatbedDpi> getFlatbedDpiProperty() {
      return flatbedDpiProperty;
   }

   /**
    * The brightness that should be used to scan an image of the plate.
    *
    * @return the brightness used to scan the image.
    */
   public long getFlatbedBrightness() {
      return flatbedBrightnessProperty.getValue();
   }

   /**
    * Assign the brightness  that should be used to scan the image of the plate.
    *
    * <p>This value has a range of {@link #BRIGHTNESS_MINIMUM} to {@link #BRIGHTNESS_MAXIMUM}.
    *
    * @param brightness The BRIGHTNESS value.
    */
   public void setFlatbedBrightness(long brightness) {
      if ((brightness < BRIGHTNESS_MINIMUM) || (brightness > BRIGHTNESS_MAXIMUM)) {
         throw new IllegalArgumentException("invalid value for brightness: " + brightness);
      }
      flatbedBrightnessProperty.set(brightness);
   }

   /**
    * The contrast that should be used to scan an image of the plate.
    *
    * @return the contrast used to scan the image.
    */
   public long getFlatbedContrast() {
      return flatbedContrastProperty.getValue();
   }

   /**
    * Assign the contrast that should be used to scan the image of the plate.
    *
    * <p>This value has a range of {@link #CONTRAST_MINIMUM} to {@link #CONTRAST_MAXIMUM}.
    *
    * @param contrast The CONTRAST value.
    */
   public void setFlatbedContrast(long contrast) {
      if ((contrast < CONTRAST_MINIMUM) || (contrast > CONTRAST_MAXIMUM)) {
         throw new IllegalArgumentException("invalid value for contrast: " + contrast);
      }
      flatbedContrastProperty.set(contrast);
   }

   // --

   private static class PlateModelHolder {
      private static final PlateModel INSTANCE = new PlateModel();
   }
}
