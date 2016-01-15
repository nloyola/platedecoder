package org.biobank.platedecoder.ui.scene.decodersettings;

import java.util.HashSet;
import java.util.Set;

import org.biobank.platedecoder.ui.scene.ConfigScene;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;

// BUG: When no changes have been made and the "Back" button is pressed, the "Unsaved changes"
// dialog is displayed.

public class DecoderSettings extends ConfigScene {

   @SuppressWarnings("unused")
   private static final Logger LOG = LoggerFactory.getLogger(DecoderSettings.class);

   private Set<TextFieldData> errors = new HashSet<>();

   private boolean modelValuesAssigned;

   public DecoderSettings() {
      super("2D barcode decoder settings");
   }

   @Override
   protected Region createContents() {
      GridPane grid = new GridPane();
      grid.setPadding(new Insets(20, 5, 5, 5));
      grid.setVgap(10);
      grid.setHgap(10);
      grid.setAlignment(Pos.TOP_CENTER);

      int row = 0;
      for (TextFieldData tfInfo : TextFieldData.values()) {
         Label label = new Label(tfInfo.label);
         Tooltip t = new Tooltip(tfInfo.description);
         Tooltip.install(label, t);

         grid.add(label, 0, row);
         grid.add(tfInfo.textField, 1, row);
         grid.add(tfInfo.getErrorImageView(), 2, row);
         GridPane.setHalignment(label, HPos.RIGHT);
         ++row;
      }

      ColumnConstraints col1 = new ColumnConstraints();
      col1.setPercentWidth(20);
      ColumnConstraints col2 = new ColumnConstraints();
      col2.setPercentWidth(55);
      ColumnConstraints col3 = new ColumnConstraints();
      col3.setPercentWidth(5);
      grid.getColumnConstraints().addAll(col1, col2, col3);

      BorderPane borderPane = new BorderPane();
      borderPane.setCenter(grid);
      borderPane.setBottom(createApplyAndRestoreButtons());

      modelValuesAssigned = false;
      setConfigChanged(false);
      setTitleAreaMessage("");
      assignModelValues();

      addListeners();
      return borderPane;
   }

   @Override
   protected void applyAction() {
      super.applyAction();
      model.setDecoderDebugLevel(TextFieldData.DECODER_DEBUG_LEVEL.getValue());
      model.setMinEdgeFactor(TextFieldData.EDGE_MIN.getValue() / 100.0);
      model.setMaxEdgeFactor(TextFieldData.EDGE_MAX.getValue() / 100.0);
      model.setScanGapFactor(TextFieldData.SCAN_GAP.getValue() / 100.0);
      model.setSquareDeviation(TextFieldData.EDGE_THRESHOLD.getValue());
      model.setEdgeThreshold(TextFieldData.SQUARE_DEVIATION.getValue());
      model.setDecoderCorrections(TextFieldData.CORRECTIONS.getValue());
   }

    @Override
    protected void restoreDefaultsAction() {
      assignModelValues();
   }

   private void setInvalidValue(TextFieldData textFieldInfo, boolean add) {
      //LOG.debug("setInvalidValue: textFieldInfo: {}, add: {}", textFieldInfo, add);
      textFieldInfo.setErrorImageVisible(add);

      if (add) {
         errors.add(textFieldInfo);
      } else {
         errors.remove(textFieldInfo);
      }

      boolean haveErrors = !errors.isEmpty();

      if (haveErrors) {
         TextFieldData firstErr = errors.iterator().next();
         setTitleAreaErrorMessage(firstErr.errorMessage);
      } else {
         setTitleAreaMessage("");
      }

      disableApplyButton(haveErrors);
      disableNextButton(haveErrors);
   }

   private void assignModelValues() {
      TextFieldData.DECODER_DEBUG_LEVEL.setValue(model.getDecoderDebugLevel());
      TextFieldData.EDGE_MIN.setValue((long) (model.getMinEdgeFactor() * 100));
      TextFieldData.EDGE_MAX.setValue((long) (model.getMaxEdgeFactor() * 100));
      TextFieldData.SCAN_GAP.setValue((long) (model.getScanGapFactor() * 100));
      TextFieldData.EDGE_THRESHOLD.setValue(model.getEdgeThreshold());
      TextFieldData.SQUARE_DEVIATION.setValue(model.getSquareDeviation());
      TextFieldData.CORRECTIONS.setValue(model.getDecoderCorrections ());
      modelValuesAssigned = true;
   }

   private void addListeners() {
      for (TextFieldData tfInfo : TextFieldData.values()) {
         tfInfo.textField.addListener((observable, oldValue, newValue) -> {
               try {
                  int value = Integer.parseInt(newValue);
                  boolean valid = tfInfo.isValid(value);
                  setInvalidValue(tfInfo, !valid);
                  if (modelValuesAssigned) {
                     setConfigChanged(true);
                  }
               } catch (NumberFormatException e) {
                  setInvalidValue(tfInfo, true);
               };
            });
      }
   }

}
