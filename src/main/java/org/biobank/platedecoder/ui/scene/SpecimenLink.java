package org.biobank.platedecoder.ui.scene;

import org.biobank.platedecoder.model.Plate;
import org.biobank.platedecoder.model.PlateDecoderPreferences;
import org.biobank.platedecoder.model.PlateModel;
import org.biobank.platedecoder.ui.PlateRegion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class SpecimenLink extends AbstractSceneRoot {

    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(SpecimenLink.class);

    private PlateRegion plateRegion;

    private SplitPane sp;

    public SpecimenLink() {
        super("Link specimens to participants");
    }

    @Override
    public void onDisplay() {
        // do nothing
    }

    @Override
    protected Node creatContents() {
        sp = new SplitPane();
        final VBox leftPane = new VBox(5);
        plateRegion = new PlateRegion(model.getPlateType());

        leftPane.getChildren().add(createParticipantControl());

        sp.getItems().addAll(leftPane, plateRegion);
        sp.setDividerPositions(0.25f, 0.75f);
        setupDivider();

        SplitPane.setResizableWithParent(leftPane, Boolean.FALSE);

        return sp;
    }

    /**
     * Remembers the position of the divider if the user moved it.
     */
    private void setupDivider() {
        sp.setDividerPosition(
            0, PlateDecoderPreferences.getInstance().getSpecimenLinkDividerPosition());

        sp.getDividers().get(0).positionProperty()
            .addListener((observable, oldValue, newValue) -> {
                PlateDecoderPreferences.getInstance()
                    .setSpecimenLinkDividerPosition(newValue.doubleValue());
            });
    }

    private Node createParticipantControl() {
        Label participantLabel = new Label("Participant: ");
        TextField participantTextField = new TextField();

        GridPane grid = new GridPane();
        grid.setVgap(5);
        grid.setHgap(5);
        grid.setPadding(new Insets(5));

        grid.add(participantLabel, 0, 0);
        grid.add(participantTextField, 1, 0);

        return grid;
    }

    public static void setTestData() {
        Plate plate = PlateModel.getInstance().getPlate();
        plate.setWellInventoryId("A1", "NUAU454147");
        plate.setWellInventoryId("A2",  "NUAU454378");
        plate.setWellInventoryId("A3",  "NUAT644406");
        plate.setWellInventoryId("A4",  "NUAU461253");
        plate.setWellInventoryId("A5",  "NUAT643708");
        plate.setWellInventoryId("A6",  "NUAT639006");
        plate.setWellInventoryId("A7",  "NUAU488922");
        plate.setWellInventoryId("A8",  "NUAT639316");
        plate.setWellInventoryId("A9",  "NUAT642338");
        plate.setWellInventoryId("A10", "NUAT641861");
        plate.setWellInventoryId("A11", "NUAT641834");
        plate.setWellInventoryId("A12", "NUAU483565");
        plate.setWellInventoryId("B1",  "NUAT639547");
        plate.setWellInventoryId("B2",  "NUAT639608");
        plate.setWellInventoryId("B3",  "NUAT637141");
        plate.setWellInventoryId("B4",  "NUAT643726");
        plate.setWellInventoryId("B5",  "NUAU461101");
        plate.setWellInventoryId("B6",  "NUAT639617");
        plate.setWellInventoryId("B7",  "NUAT642268");
        plate.setWellInventoryId("B8",  "NUAU460634");
        plate.setWellInventoryId("B9",  "NUAU461280");
        plate.setWellInventoryId("B10", "NUAT642152");
        plate.setWellInventoryId("B11", "NUAT639538");
        plate.setWellInventoryId("B12", "NUAT641995");
        plate.setWellInventoryId("C1",  "NUAT641737");
        plate.setWellInventoryId("C2",  "NUAT642444");
        plate.setWellInventoryId("C3",  "NUAU461059");
        plate.setWellInventoryId("C4",  "NUAT639510");
        plate.setWellInventoryId("C5",  "NUAU484254");
        plate.setWellInventoryId("C6",  "NUAT641621");
        plate.setWellInventoryId("C7",  "NUAU488384");
        plate.setWellInventoryId("C8",  "NUAT642365");
        plate.setWellInventoryId("C9",  "NUAT641755");
        plate.setWellInventoryId("C10", "NUAT641746");
        plate.setWellInventoryId("C11", "NUAU460661");
        plate.setWellInventoryId("C12", "NUAT641667");
        plate.setWellInventoryId("D1",  "NUAU461217");
        plate.setWellInventoryId("D2",  "NUAU460607");
        plate.setWellInventoryId("D3",  "NUAU483857");
        plate.setWellInventoryId("D4",  "NUAT642329");
        plate.setWellInventoryId("D5",  "NUAT641959");
        plate.setWellInventoryId("D6",  "NUAU489019");
        plate.setWellInventoryId("D7",  "NUAT636957");
        plate.setWellInventoryId("D8",  "NUAT641968");
        plate.setWellInventoryId("D9",  "NUAT641676");
        plate.setWellInventoryId("D10", "NUAT690131");
        plate.setWellInventoryId("D11", "NUAT690168");
        plate.setWellInventoryId("D12", "NUAT689649");
        plate.setWellInventoryId("E1",  "NUAT689667");
        plate.setWellInventoryId("E2",  "NUAT690344");
        plate.setWellInventoryId("E3",  "NUAT689889");
        plate.setWellInventoryId("E4",  "NUAT690469");
        plate.setWellInventoryId("E5",  "NUAT689995");
        plate.setWellInventoryId("E6",  "NUAT689861");
        plate.setWellInventoryId("E7",  "NUAT690043");
        plate.setWellInventoryId("E8",  "NUAT690229");
        plate.setWellInventoryId("E9",  "NUAT689922");
        plate.setWellInventoryId("E10", "NUAT690238");
        plate.setWellInventoryId("E11", "NUAT690016");
        plate.setWellInventoryId("E12", "NUAT688826");
        plate.setWellInventoryId("F1",  "NUAU455289");
        plate.setWellInventoryId("F2",  "NUAU455058");
        plate.setWellInventoryId("F3",  "NUAU455021");
        plate.setWellInventoryId("F4",  "NUAU455243");
        plate.setWellInventoryId("F5",  "NUAU454873");
        plate.setWellInventoryId("F6",  "NUAU455049");
        plate.setWellInventoryId("F7",  "NUAU455252");
        plate.setWellInventoryId("F8",  "NUAU454925");
        plate.setWellInventoryId("F9",  "NUAU487640");
        plate.setWellInventoryId("F10", "NUAU487978");
        plate.setWellInventoryId("G1",  "NUAT689764");
        plate.setWellInventoryId("G2",  "NUAT689807");
        plate.setWellInventoryId("G3",  "NUAT689977");
        plate.setWellInventoryId("G4",  "NUAT690007");
        plate.setWellInventoryId("G5",  "NUAT689463");
        plate.setWellInventoryId("G6",  "NUAT689870");
        plate.setWellInventoryId("G7",  "NUAT690104");
    }

}

