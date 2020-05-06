package org.biobank.platedecoder.ui.scene;

import static org.biobank.platedecoder.ui.JavaFxHelper.createButton;
import static org.biobank.platedecoder.ui.JavaFxHelper.errorDialog;
import static org.biobank.platedecoder.ui.JavaFxHelper.infoDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.biobank.platedecoder.model.PlateWell;
import org.biobank.platedecoder.model.PlateWellCsvWriter;
import org.biobank.platedecoder.model.SbsPosition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

/**
 * Displays the results of the decoded 2D barcodes in an image.
 *
 * <p>
 * This scene displays a table view with each row displaying the position label
 * for a tube and the string decoded from the corresponding 2D barcode. If
 * nothing was decoded for a position in the plate, then a blank cell is
 * displayed for that label.
 *
 * <p>
 * Additional buttons are displayed below the table view that allow the user to:
 * <ul>
 * <li>Copy the table's information to the clipboard.</li>
 * <li>Copy the values decoded from the 2D barcodes as a comma separated
 * string.</li>
 * <li>Export the infrmation to a CSV file.</li>
 * <li>Allow for the information to be linked to patients.</li>
 * <li>Allow for the information to be assigned to positions.</li>
 * </ul>
 */
public class DecodedTubes extends SceneRoot {

  @SuppressWarnings("unused")
  private static final Logger LOG = LoggerFactory.getLogger(DecodedTubes.class);

  private TableView<PlateWell> table;

  private TableColumn<PlateWell, String> labelColumn;

  private Optional<Runnable> specimenLinkRunnableMaybe = Optional.empty();

  /**
   * Creates the scene.
   *
   */
  public DecodedTubes() {
    super("Decoded tubes");
  }

  @Override
  protected Region createContents() {
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
    inventoryIdColumn.setCellValueFactory(cellData -> cellData.getValue().getInventoryIdProperty());
    inventoryIdColumn.prefWidthProperty().bind(table.widthProperty().multiply(0.85));

    table.getColumns().add(labelColumn);
    table.getColumns().add(inventoryIdColumn);

    HBox hbox = new HBox(10);
    hbox.setPadding(new Insets(20, 15, 15, 15));
    hbox.getChildren().addAll(createButtons());

    VBox vbox = new VBox(10);
    vbox.setPadding(new Insets(5, 5, 5, 5));
    vbox.getChildren().addAll(table, hbox);

    List<PlateWell> sortedList = new ArrayList<>(model.getPlate().getWellsSorted());
    ObservableList<PlateWell> plateWells = FXCollections.observableArrayList(sortedList);

    LOG.info("sortedLlist on table size: {}", sortedList.size());
    LOG.info("plateWells on table size: {}", plateWells.size());

    table.setItems(plateWells);
    table.getSortOrder().add(labelColumn);

    return vbox;
  }

  /**
   * Allows for an action handler to be called when the user presses the specimen
   * link button.
   *
   * @param runnable The runnable to invoked when the user presses the button.
   */
  public void onSpecimenLinkAction(Runnable runnable) {
    specimenLinkRunnableMaybe = Optional.of(runnable);
  }

  private Button[] createButtons() {
    Button copyToClipboardBtn = createButton("Copy to clipboard", this::copyToClipboardAction);
    Button copyToClipboardNoLabelsBtn = createButton("Copy to clipboard (no labels)",
        this::copyToClipboardNoLabelsAction);
    Button exportToCsvBtn = createButton("Export to CSV", this::exportToCsvAction);
    Button specimenLinkBtn = createButton("Specimen link", this::specimenLinkAction);
    Button specimenAssignBtn = createButton("Specimen assign", this::specimenAssignAction);

    return new Button[] { copyToClipboardBtn, copyToClipboardNoLabelsBtn, exportToCsvBtn, specimenLinkBtn,
        specimenAssignBtn };
  }

  @SuppressWarnings("unused")
  private void copyToClipboardAction(ActionEvent e) {
    List<PlateWell> sortedList = new ArrayList<>(model.getPlate().getWellsSorted());

    String text = sortedList.stream().map(w -> w.getLabel() + "," + w.getInventoryId())
        .collect(Collectors.joining("\n"));

    ClipboardContent cb = new ClipboardContent();
    cb.putString(text);
    Clipboard.getSystemClipboard().setContent(cb);
  }

  @SuppressWarnings("unused")
  private void copyToClipboardNoLabelsAction(ActionEvent e) {
    List<PlateWell> sortedList = new ArrayList<>(model.getPlate().getWellsSorted());

    String text = sortedList.stream().map(PlateWell::getInventoryId).collect(Collectors.joining(","));

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
        List<PlateWell> sortedList = new ArrayList<>(model.getPlate().getWellsSorted());
        PlateWellCsvWriter.write(file.toString(), sortedList);
      } catch (Exception ex) {
        errorDialog("Could not save file: " + ex.getMessage(), "File save error", null);
      }
    }
  }

  private void specimenLinkAction(@SuppressWarnings("unused") ActionEvent event) {
    specimenLinkRunnableMaybe.ifPresent(runnable -> runnable.run());
  }

  @SuppressWarnings("unused")
  private void specimenAssignAction(ActionEvent e) {
    infoDialog("To be completed", "Under construction");
  }
}
