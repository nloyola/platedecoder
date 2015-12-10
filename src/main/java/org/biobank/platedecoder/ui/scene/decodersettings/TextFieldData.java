package org.biobank.platedecoder.ui.scene.decodersettings;

import org.biobank.platedecoder.ui.ValidatedTextField;

import javafx.scene.image.ImageView;

enum TextFieldData {
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

   private TextFieldData(long minimum,
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

   ImageView getErrorImageView() {
      return textField.getErrorImageView();
   }

   long getValue() {
      return Long.valueOf(textField.getText());
   }

   void setValue(long value) {
      textField.setText(String.valueOf(value));
   }

   void setErrorImageVisible(boolean visible) {
      textField.setErrorImageVisible(visible);
   }

   boolean isValid(int value) {
      return (value >= minimum) && (value <= maximum);
   }
}
