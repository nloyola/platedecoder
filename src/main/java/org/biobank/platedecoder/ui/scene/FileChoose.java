package org.biobank.platedecoder.ui.scene;

import java.io.File;
import java.util.Optional;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class FileChoose extends AbstractSceneRoot {

    private TextField filenameField;
    private Optional<File> selectedFile = Optional.empty();
    private Button decodeBtn;

    public FileChoose() {
        super("Select an image file");
    }

    @Override
    protected void init() {}

    @Override
    protected Node creatContents() {
        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setPadding(new Insets(20, 5, 5, 5));

        Label label = new Label("File name:");
        grid.add(label, 0, 0);

        filenameField = new TextField("");
        filenameField.setId("image_filename");
        filenameField.setEditable(false);
        GridPane.setHgrow(filenameField, Priority.ALWAYS);
        grid.add(filenameField, 1, 0);

        final Button browseFileBtn = new Button("Browse");
        browseFileBtn.setOnAction(this::browseFileBtnAction);
        browseFileBtn.setMaxWidth(Double.MAX_VALUE);
        browseFileBtn.requestFocus();

        decodeBtn = new Button("Decode this image");
        decodeBtn.setDisable(true);
        decodeBtn.setMaxWidth(Double.MAX_VALUE);
        grid.add(decodeBtn, 1, 2);

        final HBox box = new HBox(8);
        box.setPadding(new Insets(5, 5, 5, 5));
        box.setPadding(new Insets(0, 20, 10, 20));
        box.getChildren().addAll(browseFileBtn, decodeBtn);

        grid.add(box, 1, 1);
        return grid;
    }

    @SuppressWarnings("unused")
    private void browseFileBtnAction(ActionEvent e) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open image file");
        fileChooser.getExtensionFilters().addAll(
            new ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif", "*.bmp"));
        File file = fileChooser.showOpenDialog(this.getScene().getWindow());
        if (file != null) {
            selectedFile = Optional.of(file);
            filenameField.setText(file.getName());
            decodeBtn.setDisable(false);
        }
    }

    @Override
    public void onDisplay() {
        unselectAll();
    }

    public void unselectAll() {
        filenameField.setText("");
        decodeBtn.setDisable(true);
    }

    public void onDecodeAction(EventHandler<ActionEvent> selectedHandler) {
        decodeBtn.setOnAction(selectedHandler);
    }

    public String getSelectedFileURI() {
        File file = selectedFile.orElseThrow(IllegalStateException::new);
        return file.toURI().toString();
    }

}
