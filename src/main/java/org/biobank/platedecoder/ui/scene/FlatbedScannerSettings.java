package org.biobank.platedecoder.ui.scene;

import java.util.Optional;

import org.biobank.platedecoder.dmscanlib.ScanLib;
import org.biobank.platedecoder.dmscanlib.ScanLibResult;
import org.biobank.platedecoder.model.DriverType;
import org.biobank.platedecoder.model.PlateDecoderDefaults;
import org.biobank.platedecoder.model.PlateModel;
import org.biobank.platedecoder.ui.PlateDecoder;
import org.biobank.platedecoder.ui.ScannerDriverTypeChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

/**
 * Displays a windows to the user where he/she can modify the parameters related to the flatbed
 * scanner. These include:
 *
 * <ul>
 *   <li>selecting the TWAIN driver</li>
 *   <li>selecting the TWAIN driver type</li>
 *   <li>The brightness setting used when scanning an image</li>
 *   <li>The contrast setting used when scanning an image</li>
 *   <li></li>
 * </ul>
 *
 */
public class FlatbedScannerSettings extends SceneRoot {

   //@SuppressWarnings("unused")
   private static final Logger LOG = LoggerFactory.getLogger(FlatbedScannerSettings.class);

   private static final String BRIGHTNESS_LABEL_PREFIX = "Brightness";

   private static final String CONTRAST_LABEL_PREFIX = "Contrast";

   private Label brightnessLabel;

   private Label contrastLabel;

   private Button scanRegionBtn;

   private Button restoreDefaultsBtn;

   private boolean userMadeChanges;

   private ObjectProperty<DriverType> driverTypeProperty;

   // The brightness setting for the flatbed scanner
   private LongProperty brightnessProperty;

   // The contrast setting for the flatbed scanner
   private LongProperty contrastProperty;

   public FlatbedScannerSettings() {
      super("Flatbed scanner settings");
   }

   @Override
   protected Region createContents() {
      userMadeChanges = false;
      driverTypeProperty = new SimpleObjectProperty<DriverType>(model.getDriverType());
      driverTypeProperty.addListener((observable, oldValue, newValue) -> {
            userMadeChanges = true;
         });
      brightnessProperty = new SimpleLongProperty(model.getFlatbedBrightness());
      contrastProperty = new SimpleLongProperty(model.getFlatbedContrast());

      Button selectDriverBtn = new Button("Select driver");
      selectDriverBtn.setOnAction(this::selectScannerSourceAction);
      selectDriverBtn.setMaxWidth(Double.MAX_VALUE);

      ScannerDriverTypeChooser driverTypeChooser = new ScannerDriverTypeChooser(driverTypeProperty);
      Slider brightnessSlider = createBrightnessSlider();
      Slider contrastSlider = createContrastSlider();

      scanRegionBtn = new Button("Define scanning region");
      scanRegionBtn.setMaxWidth(Double.MAX_VALUE);

      GridPane grid = new GridPane();
      grid.setPadding(new Insets(20, 5, 5, 5));
      grid.setVgap(10);
      grid.setHgap(10);
      grid.add(selectDriverBtn, 0, 0);
      grid.add(driverTypeChooser, 0, 1);
      grid.add(brightnessLabel, 0, 2);
      grid.add(brightnessSlider, 1, 2);
      grid.add(contrastLabel, 0, 3);
      grid.add(contrastSlider, 1, 3);
      grid.add(scanRegionBtn, 0, 4);
      grid.setAlignment(Pos.TOP_CENTER);

      ColumnConstraints col1 = new ColumnConstraints();
      col1.setPercentWidth(20);
      ColumnConstraints col2 = new ColumnConstraints();
      col2.setPercentWidth(70);
      grid.getColumnConstraints().add(col1);
      grid.getColumnConstraints().add(col2);

      VBox vbox = new VBox(5);
      vbox.getChildren().addAll(grid, createBottomButtons());

      brightnessProperty.bindBidirectional(brightnessSlider.valueProperty());
      brightnessProperty.addListener(this::brightnessSliderListener);

      contrastProperty.bindBidirectional(contrastSlider.valueProperty());
      contrastProperty.addListener(this::contrastSliderListener);

      return vbox;
   }

   @Override
   public void onDisplay() {
      driverTypeProperty.setValue(model.getDriverType());
      brightnessProperty.setValue(model.getFlatbedBrightness());
      contrastProperty.setValue(model.getFlatbedContrast());
   }

   @Override
   protected boolean allowBackButtonAction() {
      return allowNavigationAction();
   }

   @Override
   protected boolean allowNextButtonAction() {
      return allowNavigationAction();
   }

   /**
    * Assigns a handler to be invoked when the user presses the {@code Scan Region} button.
    *
    * @param runnable The runnable to execute when the button is pressed.
    */
   public void onDefineScanRegionAction(Runnable runnable) {
      scanRegionBtn.setOnAction(e -> runnable.run());
   }

   private Slider createBrightnessSlider() {
      brightnessLabel = new Label(getBrightnessLabel());
      Slider slider = new Slider(PlateModel.BRIGHTNESS_MINIMUM,
                                 PlateModel.BRIGHTNESS_MAXIMUM,
                                 model.getFlatbedBrightness());
      slider.setShowTickLabels(true);
      slider.setShowTickMarks(true);
      slider.setSnapToTicks(true);
      slider.setMajorTickUnit(PlateModel.BRIGHTNESS_MAXIMUM/2);
      slider.setBlockIncrement(25);
      slider.setMaxWidth(Double.MAX_VALUE);

      return slider;
   }

   private String getBrightnessLabel() {
      StringBuffer buf = new StringBuffer();
      buf.append(BRIGHTNESS_LABEL_PREFIX);
      buf.append(": ");
      buf.append(brightnessProperty.getValue());
      return buf.toString();
   }

   private Slider createContrastSlider() {
      contrastLabel = new Label(getContrastLabel());
      Slider slider = new Slider(PlateModel.CONTRAST_MINIMUM,
                                 PlateModel.CONTRAST_MAXIMUM,
                                 model.getFlatbedContrast());
      slider.setShowTickLabels(true);
      slider.setShowTickMarks(true);
      slider.setSnapToTicks(true);
      slider.setMajorTickUnit(PlateModel.CONTRAST_MAXIMUM/2);
      slider.setBlockIncrement(25);
      slider.setMaxWidth(Double.MAX_VALUE);
      return slider;
   }

   private String getContrastLabel() {
      StringBuffer buf = new StringBuffer();
      buf.append(CONTRAST_LABEL_PREFIX);
      buf.append(": ");
      buf.append(contrastProperty.getValue());
      return buf.toString();
   }

   private Pane createBottomButtons() {
      TilePane pane = new TilePane();
      pane.setHgap(5);
      pane.setPadding(new Insets(50, 15, 15, 15));
      pane.setMinHeight(TilePane.USE_PREF_SIZE);
      pane.setAlignment(Pos.BOTTOM_RIGHT);

      Button applyBtn = new Button("Apply");
      applyBtn.setOnAction(this::applyAction);
      applyBtn.setMaxWidth(Double.MAX_VALUE);
      applyBtn.setMinWidth(Button.USE_PREF_SIZE);

      restoreDefaultsBtn = new Button("Restore defaults");
      restoreDefaultsBtn.setOnAction(this::restoreDefaultsAction);
      restoreDefaultsBtn.setMaxWidth(Double.MAX_VALUE);
      restoreDefaultsBtn.setMinWidth(Button.USE_PREF_SIZE);

      pane.getChildren().addAll(applyBtn, restoreDefaultsBtn);
      return pane;
   }

   @SuppressWarnings("unused")
   private void selectScannerSourceAction(ActionEvent e) {
      ScanLibResult result = ScanLib.getInstance().selectSourceAsDefault();
      LOG.debug("selectScannerSource: {}", result);
   }

   @SuppressWarnings("unused")
   private void brightnessSliderListener(ObservableValue<? extends Number> observable,
                                         Number oldValue,
                                         Number newValue) {
      userMadeChanges = true;
      brightnessLabel.setText(getBrightnessLabel());
   }

   @SuppressWarnings("unused")
   private void contrastSliderListener(ObservableValue<? extends Number> observable,
                                       Number oldValue,
                                       Number newValue) {
      userMadeChanges = true;
      contrastLabel.setText(getContrastLabel());
   }
   private void applyAction() {
      model.setDriverType(driverTypeProperty.getValue());
      model.setFlatbedBrightness(brightnessProperty.getValue());
      model.setFlatbedContrast(contrastProperty.getValue());
      userMadeChanges = false;
   }

   @SuppressWarnings("unused")
   private void applyAction(ActionEvent e) {
      applyAction();
   }

   @SuppressWarnings("unused")
   private void restoreDefaultsAction(ActionEvent e) {
      driverTypeProperty.setValue(DriverType.valueOf(PlateDecoderDefaults.DEFAULT_DRIVER_TYPE));
      brightnessProperty.setValue(PlateDecoderDefaults.DEFAULT_FLATBED_BRIGHTNESS);
      contrastProperty.setValue(PlateDecoderDefaults.DEFAULT_FLATBED_CONTRAST);
   }

   private boolean allowNavigationAction() {
      if (userMadeChanges) {
         Alert alert = PlateDecoder.createDialog(
            AlertType.CONFIRMATION,
            "Unsaved changes",
            "Apply your changes?",
            "You have not applied the changes you made to these settings.");

         Optional<ButtonType> result = alert.showAndWait();
         if (result.isPresent() && result.get() == ButtonType.OK) {
            applyAction();
         }
      }
      return true;
   }
}
