package org.biobank.platedecoder.ui;

import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

import org.biobank.platedecoder.model.Plate;
import org.biobank.platedecoder.model.PlateModel;
import org.biobank.platedecoder.model.PlateType;
import org.biobank.platedecoder.model.PlateWell;
import org.biobank.platedecoder.model.SbsPosition;

public class PlateRegion extends Region {

    final PlateType plateType;

    private final StackPane platePane;

    private static final PlateModel model = PlateModel.getInstance();

    public PlateRegion(PlateType plateType) {
        this.plateType = plateType;
        platePane = new StackPane();
        platePane.getChildren().addAll(createBackground(), createPlate());
        getChildren().add(platePane);
    }

    private Node createBackground() {
        Region region = new Region();
        // region.setStyle("-fx-background-color: radial-gradient(radius 100%, white, gray)");
        return region;
    }

    private Node createPlate() {
        int rows = plateType.getRows() + 1;
        int cols = plateType.getCols() + 1;

        Plate plate = model.getPlate();

        GridPane grid = new GridPane();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if ((i == 0) && (j > 0)) {
                    grid.add(new StackPane(new PlateLabel(Integer.toString(j))), j, i);
                }

                if ((i > 0) && (j == 0)) {
                    grid.add(new StackPane(new PlateLabel((char) ('A' + i - 1))), j, i);
                }

                if ((i > 0) && (j > 0)) {
                    SbsPosition position = new SbsPosition(i - 1, j - 1);
                    PlateWell well = plate.getWell(position.getLabel());
                    PlateWellRegion wellRegion = new PlateWellRegion(well, i - 1, j - 1);
                    grid.add(new StackPane(wellRegion), j, i);
                }
            }
        }
        return grid;
    }

}
