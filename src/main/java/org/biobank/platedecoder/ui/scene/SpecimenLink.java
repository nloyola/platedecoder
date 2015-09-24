package org.biobank.platedecoder.ui.scene;

import org.biobank.platedecoder.ui.PlateRegion;
import org.biobank.platedecoder.ui.PlateTypeChooser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class SpecimenLink extends AbstractSceneRoot {

    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(SpecimenLink.class);

    private PlateRegion plateRegion;

    public SpecimenLink() {
        super("Link specimens to patients");
    }

    @Override
    public void onDisplay() {
        // do nothing
    }

    @Override
    protected void init() {
        // do nothing
    }

    @Override
    protected Node creatContents() {
        SplitPane sp = new SplitPane();
        final VBox leftPane = new VBox(5);
        plateRegion = new PlateRegion(model.getPlateType());

        leftPane.getChildren().add(createPlateTypeControl());

        sp.getItems().addAll(leftPane, plateRegion);
        sp.setDividerPositions(0.20f, 0.80f);

        SplitPane.setResizableWithParent(leftPane, Boolean.FALSE);
        return sp;
    }

    private Node createPlateTypeControl() {
        PlateTypeChooser plateTypeChooser = new PlateTypeChooser();

        GridPane grid = new GridPane();
        grid.add(plateTypeChooser, 0, 0);

        return grid;
    }

}

