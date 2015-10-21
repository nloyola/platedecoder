package org.biobank.platedecoder.ui.scene;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;

public class ImageSource extends AbstractSceneRoot {

    private RadioButton filesystemButton;

    private RadioButton flatbedScanButton;

    public ImageSource() {
        super("Choose an action");
    }

    @Override
    public void onDisplay() {
        unselectAll();
    }

    @Override
    protected Node creatContents() {
        filesystemButton = new RadioButton("Filesystem");
        flatbedScanButton = new RadioButton("Flatbed scanner");

        final ToggleGroup toggleGroup = new ToggleGroup();
        filesystemButton.setToggleGroup(toggleGroup);
        flatbedScanButton.setToggleGroup(toggleGroup);

        final GridPane grid = new GridPane();
        grid.setPadding(new Insets(20, 5, 5, 5));
        grid.setVgap(10);
        grid.setHgap(10);

        grid.add(filesystemButton, 0, 0);
        grid.add(flatbedScanButton, 0, 1);
        return grid;
    }

    public void onFilesystemAction(EventHandler<ActionEvent> flatbedSelectedHandler) {
        filesystemButton.setOnAction(flatbedSelectedHandler);
    }

    public void onFlatbedScanAction(EventHandler<ActionEvent> flatbedSelectedHandler) {
        flatbedScanButton.setOnAction(flatbedSelectedHandler);
    }

    public void unselectAll() {
        filesystemButton.setSelected(false);
        flatbedScanButton.setSelected(false);
    }
};
