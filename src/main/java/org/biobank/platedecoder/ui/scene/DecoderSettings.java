package org.biobank.platedecoder.ui.scene;

import java.util.HashSet;
import java.util.Set;

import org.biobank.platedecoder.ui.ValidatedTextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;

public class DecoderSettings extends SceneRoot {

   @SuppressWarnings("unused")
   private static final Logger LOG = LoggerFactory.getLogger(DecoderSettings.class);

   private static enum TextFieldInfo {
      DECODER_DEBUG_LEVEL(
         0,
         9,
         "Decoder debug level:",
         "A non zero value displays debugging information.",
         "Decoder debug level should be a number between "),
      EDGE_MIN(
         0,
         100,
         "Edge minimum",
         "The length of the smallest expected edge in the image as a percentage of the cell width.",
         "Edge minimum factor should be a value between "),
      EDGE_MAX(
         0,
         100,
         "Edge maximum:",
         "The length of the largest expected edge in the image as a percentage of the cell width.",
         "Edge maximum factor should be a value between "),
      SCAN_GAP(
         0,
         100,
         "Scan gap:",
         "The gap between lines in the scan grid used to examine the image"
         + " as a percentage of the cell width.",
         "Scan gap factor should be a value between "),
      EDGE_THRESHOLD(
         0,
         100,
         "Edge threshold:",
         "The minimum edge threshold as a percentage of maximum. For example, an edge between a pure\n"
         + " white and pure black pixel would have an intensity of 100.",
         "Edge threshold should be a a value between "),
      SQUARE_DEVIATION(
         0,
         45,
         "Square deviation:",
         "The maximum deviation, in degrees, from squareness between adjacent barcode sides.\n"
         + "10 degrees is the recommened value for flat images.",
         "Square deviation should be a value between "),
      CORRECTIONS(
         0,
         20,
         "Corrections:",
         "The number of errors to correct per image.",
         "Corrections should be a number between ");

      final long minimum;

      final long maximum;

      final String label;

      final String errorMessage;

      final String description;

      final ValidatedTextField textField;

      private TextFieldInfo(long minimum,
                            long maximum,
                            String label,
                            String description,
                            String errorMessagePrefix) {
         this.minimum      = minimum;
         this.maximum      = maximum;
         this.label        = label;
         this.description  = description;
         this.textField    = new ValidatedTextField();

         StringBuffer buf = new StringBuffer();
         buf.append(errorMessagePrefix);
         buf.append(minimum);
         buf.append(" and ");
         buf.append(maximum);

         this.errorMessage = buf.toString();
      }

      public ImageView getErrorImageView() {
         return textField.getErrorImageView();
      }

      public long getValue() {
         return Long.valueOf(textField.getText());
      }

      public void setValue(long value) {
         textField.setText(String.valueOf(value));
      }

      public void setErrorImageVisible(boolean visible) {
         textField.setErrorImageVisible(visible);
      }

      public boolean isValid(int value) {
         return (value >= minimum) && (value <= maximum);
      }
   }

   private Set<TextFieldInfo> errors = new HashSet<>();

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
      for (TextFieldInfo tfInfo : TextFieldInfo.values()) {
         Label label = new Label(tfInfo.label);
         Tooltip t = new Tooltip(tfInfo.description);
         Tooltip.install(label, t);

         grid.add(label, 0, row);
         grid.add(tfInfo.textField, 1, row);
         grid.add(tfInfo.getErrorImageView(), 2, row);
         GridPane.setHalignment(label, HPos.RIGHT);
         ++row;
      }

      addListeners();

      ColumnConstraints col1 = new ColumnConstraints();
      col1.setPercentWidth(20);
      ColumnConstraints col2 = new ColumnConstraints();
      col2.setPercentWidth(55);
      ColumnConstraints col3 = new ColumnConstraints();
      col3.setPercentWidth(5);
      grid.getColumnConstraints().add(col1);
      grid.getColumnConstraints().add(col2);
      grid.getColumnConstraints().add(col3);

      BorderPane borderPane = new BorderPane();
      borderPane.setCenter(grid);
      borderPane.setBottom(createApplyAndRestoreButtons());

      return borderPane;
   }

   @Override
   public void onDisplay() {
      setTitleAreaMessage("");

      TextFieldInfo.DECODER_DEBUG_LEVEL.setValue(model.getDecoderDebugLevel());
      TextFieldInfo.EDGE_MIN.setValue((long) (model.getMinEdgeFactor() * 100));
      TextFieldInfo.EDGE_MAX.setValue((long) (model.getMaxEdgeFactor() * 100));
      TextFieldInfo.SCAN_GAP.setValue((long) (model.getScanGapFactor() * 100));
      TextFieldInfo.EDGE_THRESHOLD.setValue(model.getEdgeThreshold());
      TextFieldInfo.SQUARE_DEVIATION.setValue(model.getSquareDeviation());
      TextFieldInfo.CORRECTIONS.setValue(model.getDecoderCorrections ());
   }

   @Override
   protected void applyAction() {
      model.setDecoderDebugLevel(TextFieldInfo.DECODER_DEBUG_LEVEL.getValue());
      model.setMinEdgeFactor(TextFieldInfo.EDGE_MIN.getValue() / 100.0);
      model.setMaxEdgeFactor(TextFieldInfo.EDGE_MAX.getValue() / 100.0);
      model.setScanGapFactor(TextFieldInfo.SCAN_GAP.getValue() / 100.0);
      model.setSquareDeviation(TextFieldInfo.EDGE_THRESHOLD.getValue());
      model.setEdgeThreshold(TextFieldInfo.SQUARE_DEVIATION.getValue());
      model.setDecoderCorrections(TextFieldInfo.CORRECTIONS.getValue());
   }

   private void addListeners() {
      for (TextFieldInfo tfInfo : TextFieldInfo.values()) {
         tfInfo.textField.addListener((observable, oldValue, newValue) -> {
               try {
                  int value = Integer.parseInt(newValue);
                  boolean valid = tfInfo.isValid(value);
                  setInvalidValue(tfInfo, !valid);
                  setConfigChanged(true);
               } catch (NumberFormatException e) {
                  setInvalidValue(tfInfo, true);
               };
            });
      }
   }

   private void setInvalidValue(TextFieldInfo textFieldInfo, boolean add) {
      //LOG.debug("setInvalidValue: textFieldInfo: {}, add: {}", textFieldInfo, add);
      textFieldInfo.setErrorImageVisible(add);

      if (add) {
         errors.add(textFieldInfo);
      } else {
         errors.remove(textFieldInfo);
      }

      boolean haveErrors = !errors.isEmpty();

      if (haveErrors) {
         TextFieldInfo firstErr = errors.iterator().next();
         setTitleAreaErrorMessage(firstErr.errorMessage);
      } else {
         setTitleAreaMessage("");
      }

      disableApplyButton(haveErrors);
      disableNextButton(haveErrors);
   }
}
