package org.biobank.platedecoder.ui.scene;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;

public class ImageSource extends AbstractSceneRoot {

    private RadioButton filesystemBtn;

    private RadioButton flatbedBtn;

    public ImageSource() {
        super("Select the image source");
    }

    @Override
    protected Node creatContents() {
        final GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setPadding(new Insets(20, 5, 5, 5));

        final ToggleGroup toggleGroup = new ToggleGroup();

        filesystemBtn = new RadioButton("Filesystem");
        filesystemBtn.setToggleGroup(toggleGroup);
        grid.add(filesystemBtn, 0, 0);

        flatbedBtn = new RadioButton("Flatbed scanner");
        flatbedBtn.setToggleGroup(toggleGroup);
        grid.add(flatbedBtn, 0, 1);
        return grid;
    }

    public void onFlatbedSelectedAction(EventHandler<ActionEvent> flatbedSelectedHandler) {
        filesystemBtn.setOnAction(flatbedSelectedHandler);
    }

    @Override
    public void onDisplay() {
        unselectAll();
    }

    public void unselectAll() {
        filesystemBtn.setSelected(false);
        flatbedBtn.setSelected(false);
    }
};
