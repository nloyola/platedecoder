package org.biobank.platedecoder.model;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
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

   private PlateDecoderPreferences preferences = PlateDecoderPreferences.getInstance();

   // The type of plate currently selected. I.e. the number of wells it contains.
   private ObjectProperty<PlateType> plateTypeProperty;

   // The orientation of the plate when it was scanned or a picture taken of it.
   private ObjectProperty<PlateOrientation> plateOrientationProperty;

   // If the barcodes are present on the tube tops or bottoms.
   private ObjectProperty<BarcodePosition> barcodePositionProperty;

   // The DPI used when the flatbed scanner was used to take an image of the plate.
   private ObjectProperty<FlatbedDpi> flatbedDpiProperty;

   // The brightness setting for the flatbed scanner
   private LongProperty flatbedBrightnessProperty;

   // The contrast setting for the flatbed scanner
   private LongProperty flatbedContrastProperty;

   private final LongProperty decoderDebugLevelProperty;
   private final DoubleProperty minEdgeFactorProperty;
   private final DoubleProperty maxEdgeFactorProperty;
   private final DoubleProperty scanGapFactorProperty;
   private final LongProperty squareDeviationProperty;
   private final LongProperty edgeThresholdProperty;
   private final LongProperty correctionsProperty;

   private PlateModel() {
      plateTypeProperty = new SimpleObjectProperty<PlateType>(preferences.getPlateType());
      plateTypeProperty.addListener((observable, oldValue, newValue) -> {
            preferences.setPlateType(newValue);
            createNewPlate();
         });

      barcodePositionProperty =
         new SimpleObjectProperty<BarcodePosition>(preferences.getBarcodePosition());
      barcodePositionProperty.addListener((observable, oldValue, newValue) -> {
            preferences.setBarcodePosition((newValue));
            createNewPlate();
         });

      plateOrientationProperty =
         new SimpleObjectProperty<PlateOrientation>(preferences.getPlateOrietation());
      plateOrientationProperty.addListener((observable, oldValue, newValue) -> {
            preferences.setPlateOrientation(newValue);
            createNewPlate();
         });

      flatbedDpiProperty =
         new SimpleObjectProperty<FlatbedDpi>(preferences.getFlatbedDpi());
      flatbedDpiProperty.addListener((observable, oldValue, newValue) -> {
            preferences.setFlatbedDpi(newValue);
         });

      flatbedBrightnessProperty =
         new SimpleLongProperty(preferences.getFlatbedBrightness());
      flatbedBrightnessProperty.addListener((observable, oldValue, newValue) -> {
            preferences.setFlatbedBrightness(newValue.longValue());
         });

      flatbedContrastProperty =
         new SimpleLongProperty(preferences.getFlatbedContrast());
      flatbedContrastProperty.addListener((observable, oldValue, newValue) -> {
            preferences.setFlatbedContrast(newValue.longValue());
         });

      decoderDebugLevelProperty =
         new SimpleLongProperty(preferences.getDecoderDebugLevel());
      decoderDebugLevelProperty.addListener((observable, oldValue, newValue) -> {
            preferences.setDecoderDebugLevel(newValue.longValue());
         });

      minEdgeFactorProperty =
         new SimpleDoubleProperty(preferences.getMinEdgeFactor());
      minEdgeFactorProperty.addListener((observable, oldValue, newValue) -> {
            preferences.setMinEdgeFactor(newValue.doubleValue());
         });

      maxEdgeFactorProperty =
         new SimpleDoubleProperty(preferences.getMaxEdgeFactor());
      maxEdgeFactorProperty.addListener((observable, oldValue, newValue) -> {
            preferences.setMaxEdgeFactor(newValue.doubleValue());
         });

      scanGapFactorProperty =
         new SimpleDoubleProperty(preferences.getScanGapFactor());
      scanGapFactorProperty.addListener((observable, oldValue, newValue) -> {
            preferences.setScanGapFactor(newValue.doubleValue());
         });

      squareDeviationProperty =
         new SimpleLongProperty(preferences.getSquareDeviation());
      squareDeviationProperty.addListener((observable, oldValue, newValue) -> {
            preferences.setSquareDeviation(newValue.longValue());
         });

      edgeThresholdProperty =
         new SimpleLongProperty(preferences.getEdgeThreshold());
      edgeThresholdProperty.addListener((observable, oldValue, newValue) -> {
            preferences.setEdgeThreshold(newValue.longValue());
         });

      correctionsProperty =
         new SimpleLongProperty(preferences.getDecoderCorrections());
      correctionsProperty.addListener((observable, oldValue, newValue) -> {
            preferences.setDecoderCorrections(newValue.longValue());
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

   public long getDecoderDebugLevel() {
      return decoderDebugLevelProperty.getValue();
   }

   public void setDecoderDebugLevel(long value) {
      decoderDebugLevelProperty.setValue(value);
   }

   public double getMinEdgeFactor() {
      return minEdgeFactorProperty.getValue();
   }

   public void setMinEdgeFactor(double value) {
      minEdgeFactorProperty.setValue(value);
   }

   public double getMaxEdgeFactor() {
      return maxEdgeFactorProperty.getValue();
   }

   public void setMaxEdgeFactor(double value) {
      maxEdgeFactorProperty.setValue(value);
   }

   public double getScanGapFactor() {
      return scanGapFactorProperty.getValue();
   }

   public void setScanGapFactor(double value) {
      scanGapFactorProperty.setValue(value);
   }

   public long getSquareDeviation() {
      return squareDeviationProperty.getValue();
   }

   public void setSquareDeviation(long value) {
      squareDeviationProperty.setValue(value);
   }

   public long getEdgeThreshold() {
      return edgeThresholdProperty.getValue();
   }

   public void setEdgeThreshold(long value) {
      edgeThresholdProperty.setValue(value);
   }

   public long getDecoderCorrections() {
      return correctionsProperty.getValue();
   }

   public void setDecoderCorrections(long value) {
      correctionsProperty.setValue(value);
   }

   // --

   private static class PlateModelHolder {
      private static final PlateModel INSTANCE = new PlateModel();
   }
}
