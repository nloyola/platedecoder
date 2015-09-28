package org.biobank.platedecoder.ui.scene;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.biobank.platedecoder.model.PlateWell;
import org.biobank.platedecoder.model.PlateWellCsvWriter;
import org.biobank.platedecoder.model.SbsPosition;
import org.biobank.platedecoder.ui.PlateDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

public class DecodedTubes extends AbstractSceneRoot {

    //@SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(DecodedTubes.class);

    private TableView<PlateWell> table;

    private TableColumn<PlateWell, String> labelColumn;

    private List<PlateWell> sortedList;

    private Optional<EventHandler<ActionEvent>> specimenLinkHandlerMaybe = Optional.empty();

    public DecodedTubes() {
        super("Decoded tubes");
        LOG.debug("DecodedTubes");
        sortedList = new ArrayList<>();
    }

    @Override
    public void onDisplay() {
        sortedList = new ArrayList<>(model.getPlate().getWells());
        Collections.sort(sortedList);
        ObservableList<PlateWell> plateWells = FXCollections.observableArrayList(sortedList);
        table.setItems(plateWells);
        table.getSortOrder().add(labelColumn);
        LOG.debug("plate wells: {}", sortedList.size());
    }

    @Override
    protected Node creatContents() {
        table = new TableView<>();

        labelColumn = new TableColumn<>("Label");
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
        inventoryIdColumn.prefWidthProperty().bind(table.widthProperty().multiply(0.85));

        table.getColumns().add(labelColumn);
        table.getColumns().add(inventoryIdColumn);

        HBox hbox = new HBox(10);
        hbox.setPadding(new Insets(20, 15, 15, 15));
        hbox.getChildren().addAll(createButtons());

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(5, 5, 5, 5));
        vbox.getChildren().addAll(table, hbox);

        onDisplay();

        return vbox;
    }

    private Button [] createButtons() {
        Button copyToClipboardBtn = new Button("Copy to clipboard");
        copyToClipboardBtn.setOnAction(this::copyToClipboardAction);

        Button copyToClipboardNoLabelsBtn = new Button("Copy to clipboard (no labels)");
        copyToClipboardNoLabelsBtn.setOnAction(this::copyToClipboardNoLabelsAction);

        Button exportToCsvBtn = new Button("Export to CSV");
        exportToCsvBtn.setOnAction(this::exportToCsvAction);

        Button specimenLinkBtn = new Button("Specimen link");
        specimenLinkBtn.setOnAction(this::specimenLinkAction);

        Button specimenAssignBtn = new Button("Specimen assign");
        specimenAssignBtn.setOnAction(this::specimenAssignAction);

        return new Button [] {
            copyToClipboardBtn,
            copyToClipboardNoLabelsBtn,
            exportToCsvBtn,
            specimenLinkBtn,
            specimenAssignBtn
        };
    }

    @SuppressWarnings("unused")
    private void copyToClipboardAction(ActionEvent e) {
        String text = sortedList.stream()
            .map(w -> w.getLabel() + "," + w.getInventoryId())
            .collect(Collectors.joining("\n"));

        ClipboardContent cb = new ClipboardContent();
        cb.putString(text);
        Clipboard.getSystemClipboard().setContent(cb);
    }

    @SuppressWarnings("unused")
    private void copyToClipboardNoLabelsAction(ActionEvent e) {
        String text = sortedList.stream()
            .map(PlateWell::getInventoryId)
            .collect(Collectors.joining(","));

        ClipboardContent cb = new ClipboardContent();
        cb.putString(text);
        Clipboard.getSystemClipboard().setContent(cb);
    }

    @SuppressWarnings("unused")
    private void exportToCsvAction(ActionEvent e) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export to CSV");
        File file = fileChooser.showSaveDialog(this.getScene().getWindow());
        if (file != null) {
            try {
                PlateWellCsvWriter.write(file.toString(), sortedList);
            } catch (Exception ex) {
                LOG.error(ex.getMessage());
                PlateDecoder.errorDialog("Could not save file: " + ex.getMessage(),
                                         "File save error",
                                         null);
            }
        }
    }

    public void onSpecimenLinkAction(EventHandler<ActionEvent> handler) {
        specimenLinkHandlerMaybe = Optional.of(handler);
    }

    private void specimenLinkAction(ActionEvent event) {
        specimenLinkHandlerMaybe.ifPresent(handler -> handler.handle(event));
    }

    @SuppressWarnings("unused")
    private void specimenAssignAction(ActionEvent e) {
        PlateDecoder.infoDialog("To be completed", "Under construction");
    }
}

