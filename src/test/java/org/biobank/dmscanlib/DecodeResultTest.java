package org.biobank.dmscanlib;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.biobank.dmscanlib.DecodeResult;
import org.biobank.dmscanlib.DecodedWell;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DecodeResultTest {

    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(DecodeResultTest.class);

    //@SuppressWarnings("serial")
    private static final List<DecodedWell> decodedWells = Arrays.asList(
        new DecodedWell("A1", "AAA1"),
        new DecodedWell("A2", "AAA2")
        );

    private DecodeResult createDecodeResult(List<DecodedWell> wells) {
        DecodeResult dr = new DecodeResult(0, "OK");
        for (DecodedWell well : wells) {
            dr.addWell(well.getLabel(), well.getMessage());
        }
        return dr;
    }

    @Test
    public void identicalResultsAreValid() {
        DecodeResult dr1 = createDecodeResult(decodedWells);
        DecodeResult dr2 = createDecodeResult(decodedWells);

        assertTrue(DecodeResult.compareDecodeResults(dr1.getDecodedWells(),
                                                     dr2.getDecodedWells()));
    }

    @Test
    public void subsetResultsAreValid() {
        DecodeResult dr1 = createDecodeResult(decodedWells);
        DecodeResult dr2 = createDecodeResult(decodedWells.subList(0, 1));

        assertTrue(DecodeResult.compareDecodeResults(dr1.getDecodedWells(),
                                                     dr2.getDecodedWells()));
    }

    @Test
    public void aggregatedResultsAreValid() {
        DecodeResult dr1 = createDecodeResult(decodedWells.subList(0, 1));
        DecodeResult dr2 = createDecodeResult(decodedWells);

        assertTrue(DecodeResult.compareDecodeResults(dr1.getDecodedWells(),
                                                     dr2.getDecodedWells()));
    }

    @Test
    public void nonMatchingResultsAreInvalid() {
        DecodeResult dr1 = createDecodeResult(decodedWells);
        DecodeResult dr2 = createDecodeResult(
            Arrays.asList(
                new DecodedWell("A1", "AAA3"),
                new DecodedWell("A2", "AAA4")
                ));

        assertFalse(DecodeResult.compareDecodeResults(dr1.getDecodedWells(),
                                                      dr2.getDecodedWells()));
    }

    @Test
    public void singleNonMatchingResultAreInvalid() {
        DecodeResult dr1 = createDecodeResult(decodedWells);
        DecodeResult dr2 = createDecodeResult(
            Arrays.asList(
                new DecodedWell("A1", "AAA1"),
                new DecodedWell("A2", "AAA3")
                ));

        assertFalse(DecodeResult.compareDecodeResults(dr1.getDecodedWells(),
                                                      dr2.getDecodedWells()));
    }

    @Test
    public void scrambledResultAreInvalid() {
        DecodeResult dr1 = createDecodeResult(decodedWells);
        DecodeResult dr2 = createDecodeResult(
            Arrays.asList(
                new DecodedWell("A1", "AAA2"),
                new DecodedWell("A2", "AAA1")
                ));

        assertFalse(DecodeResult.compareDecodeResults(dr1.getDecodedWells(),
                                                      dr2.getDecodedWells()));
    }

    @Test
    public void otherLocationResultAreInvalid() {
        DecodeResult dr1 = createDecodeResult(decodedWells);
        DecodeResult dr2 = createDecodeResult(
            Arrays.asList(
                new DecodedWell("A3", "AAA1"),
                new DecodedWell("A4", "AAA4")
                ));

        assertFalse(DecodeResult.compareDecodeResults(dr1.getDecodedWells(),
                                                      dr2.getDecodedWells()));
    }

    @Test
    public void multipleOtherLocationResultAreInvalid() {
        DecodeResult dr1 = createDecodeResult(decodedWells);
        DecodeResult dr2 = createDecodeResult(
            Arrays.asList(
                new DecodedWell("A3", "AAA1"),
                new DecodedWell("A4", "AAA2")
                ));

        assertFalse(DecodeResult.compareDecodeResults(dr1.getDecodedWells(),
                                                      dr2.getDecodedWells()));
    }

}
