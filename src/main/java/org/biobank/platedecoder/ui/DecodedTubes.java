package org.biobank.platedecoder.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.biobank.platedecoder.model.PlateWell;
import org.biobank.platedecoder.model.SbsPosition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class DecodedTubes extends AbstractSceneRoot {

    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(ImageAndGrid.class);

    private TableView<PlateWell> table;

    public DecodedTubes() {
        super("Decoded tubes");

    }

    @Override
    protected void init() {
        // do nothing
    }

    @Override
    protected void onDisplay() {
        // do nothing
        ObservableList<PlateWell> plateWells =
            FXCollections.observableArrayList(model.getPlate().getWells());
        table.setItems(plateWells);
    }

    @Override
    protected Node creatContents() {
        table = new TableView<>();

        TableColumn<PlateWell, String> labelColumn = new TableColumn<>("Label");
        labelColumn.setCellValueFactory(cellData -> cellData.getValue().getLabelProperty());
        labelColumn.setSortType(TableColumn.SortType.ASCENDING);
        labelColumn.setComparator((String l1, String l2) -> {
                SbsPosition pos1 = new SbsPosition(l1);
                SbsPosition pos2 = new SbsPosition(l2);
                return pos1.compareTo(pos2);
            });
        labelColumn.prefWidthProperty().bind(table.widthProperty().divide(10));

        TableColumn<PlateWell, String> inventoryIdColumn = new TableColumn<>("Inventory ID");
        inventoryIdColumn.setCellValueFactory(cellData ->
                                              cellData.getValue().getInventoryIdProperty());
        inventoryIdColumn.prefWidthProperty().bind(table.widthProperty().divide(4));

        table.getColumns().add(labelColumn);
        table.getColumns().add(inventoryIdColumn);
        table.getSortOrder().add(labelColumn);

        Button exportToCsvBtn = new Button("Export to CSV");
        exportToCsvBtn.setOnAction(e -> exportToCsv());

        Button copyToClipboardBtn = new Button("Copy to clipboard");
        copyToClipboardBtn.setOnAction(e -> copyToClipboard());

        Button copyToClipboardNoLabelsBtn = new Button("Copy to clipboard (no labels)");
        copyToClipboardNoLabelsBtn.setOnAction(e -> copyToClipboardNoLabels());

        HBox hbox = new HBox(10);
        hbox.setPadding(new Insets(20, 15, 15, 15));
        hbox.getChildren().addAll(exportToCsvBtn, copyToClipboardBtn, copyToClipboardNoLabelsBtn);

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(5, 5, 5, 5));
        vbox.getChildren().addAll(table, hbox);

        onDisplay();

        return vbox;
    }

    private void exportToCsv() {
        LOG.debug("exportToCsv");
    }

    private void copyToClipboard() {
        List<PlateWell> sortedList = new ArrayList<>(model.getPlate().getWells());
        Collections.sort(sortedList);

        String text = sortedList.stream()
            .map(w -> w.getLabel() + "," + w.getInventoryId())
            .collect(Collectors.joining("\n"));

        ClipboardContent cb = new ClipboardContent();
        cb.putString(text);
        Clipboard.getSystemClipboard().setContent(cb);
    }

    private void copyToClipboardNoLabels() {
        List<PlateWell> sortedList = new ArrayList<>(model.getPlate().getWells());
        Collections.sort(sortedList);

        String text = sortedList.stream()
            .map(PlateWell::getInventoryId)
            .collect(Collectors.joining(","));

        ClipboardContent cb = new ClipboardContent();
        cb.putString(text);
        Clipboard.getSystemClipboard().setContent(cb);
    }

}

