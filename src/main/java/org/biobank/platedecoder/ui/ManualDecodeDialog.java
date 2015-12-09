package org.biobank.platedecoder.ui;

import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 *
 */
public class ManualDecodeDialog extends Dialog<String> {

    //@SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(ManualDecodeDialog.class);

    public ManualDecodeDialog(final String label,
                              final Set<String> deniedInventoryIds) {
        StringBuffer buf = new StringBuffer();
        buf.append("Manual decode for well ");
        buf.append(label);

        setTitle("Manual Decode");
        setHeaderText(buf.toString());

        TextField inventoryId = new TextField();
        Text errorMessage = new Text();
        errorMessage.setFill(Color.RED);

        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(5);
        grid.add(new Label("Enter the inventory ID:"), 0, 0);
        grid.add(inventoryId, 1, 0);
        grid.add(errorMessage, 0, 1, 2, 1);
        grid.setPadding(new Insets(5));
        grid.setMaxWidth(Double.MAX_VALUE);

        GridPane.setVgrow(errorMessage, Priority.ALWAYS);
        GridPane.setHgrow(errorMessage, Priority.ALWAYS);

        getDialogPane().setContent(grid);

        ButtonType buttonTypeOk = new ButtonType("OK", ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().setAll(buttonTypeOk, buttonTypeCancel);

        inventoryId.textProperty().addListener((observable, oldValue, newValue) -> {
                Optional<String> exists = deniedInventoryIds.stream()
                    .filter(id -> id.equals(newValue))
                    .findFirst();

                Node buttonOk = getDialogPane().lookupButton(buttonTypeOk);
                buttonOk.setDisable(exists.isPresent());

                if (exists.isPresent()) {
                    StringBuffer errMsgBuf = new StringBuffer();
                    errMsgBuf.append("Inventory ID \"");
                    errMsgBuf.append(newValue);
                    errMsgBuf.append("\" cannot be entered here.\n");
                    errMsgBuf.append("It was decoded for a different well on this plate.");
                    errorMessage.setText(errMsgBuf.toString());

                    // hack to resize the dialog box to display the errorMessage label
                    Platform.runLater(() -> {
                            getDialogPane().requestLayout();
                            Stage stage = (Stage) getDialogPane().getScene().getWindow();
                            stage.sizeToScene();
                        });
                } else {
                    errorMessage.setText("");
                }
            });

        // handle "enter key" event
        inventoryId.setOnAction((event) -> {
                LOG.debug("TextField Action");
                Button okButton = (Button) getDialogPane().lookupButton(buttonTypeOk);
                okButton.fire();
            });

        setResultConverter(button -> {
                if (button == buttonTypeOk) {
                    return inventoryId.getText();
                }
                return null;
            });

    }

}
