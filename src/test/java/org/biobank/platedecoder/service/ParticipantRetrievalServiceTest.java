package org.biobank.platedecoder.service;

import static org.junit.Assert.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.biobank.platedecoder.model.Participant;
import org.biobank.platedecoder.service.ParticipantRetrievalService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.saxsys.javafx.test.JfxRunner;

import javafx.collections.ObservableList;
import javafx.concurrent.Worker;

/**
 * See http://blog.buildpath.de/javafx-testrunner/ for the mechanism used to run these tests.
 */
@RunWith(JfxRunner.class)
public class ParticipantRetrievalServiceTest {

    //@SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(ParticipantRetrievalServiceTest.class);

    @Test
    public void canGetParticipants() throws Exception {
        ParticipantRetrievalService service =
            new ParticipantRetrievalService("http://localhost:8080/", "CBSR", "1098mrn");
        CompletableFuture<ServiceTestResults<ObservableList<Participant>>> future =
            new CompletableFuture<>();
        service.stateProperty().addListener(
            (observable, oldValue, newValue) -> {
                if (newValue == Worker.State.SUCCEEDED) {
                    future.complete(
                        new ServiceTestResults<ObservableList<Participant>>(service.getMessage(),
                                                                     service.getState(),
                                                                     service.getValue()));
                }
            });

        // We are not in the UI Thread so we have to start the service in runLater()
        service.start();

        ServiceTestResults<ObservableList<Participant>> result = future.get(5000, TimeUnit.MILLISECONDS);

        assertEquals(Worker.State.SUCCEEDED, result.state);
        LOG.debug("service value: {}", result.serviceResult);
    }

    @SuppressWarnings("unused")
    private class ServiceTestResults<T> {
        String message;
        Worker.State state;
        T serviceResult;

        ServiceTestResults(String message, Worker.State state, T serviceResult) {
            this.message = message;
            this.state = state;
            this.serviceResult = serviceResult;
        }
    }

}

