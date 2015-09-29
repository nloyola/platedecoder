package org.biobank.platedecoder.service;

import static org.junit.Assert.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.biobank.platedecoder.model.Site;
import org.biobank.platedecoder.service.SiteRetrievalService;
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
public class SiteRetrievalServiceTest {

    //@SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(SiteRetrievalServiceTest.class);

    @Test
    public void canGetSites() throws Exception {
        SiteRetrievalService service = new SiteRetrievalService("http://localhost:8080/");
        CompletableFuture<ServiceTestResults<ObservableList<Site>>> future =
            new CompletableFuture<>();
        service.stateProperty().addListener(
            (observable, oldValue, newValue) -> {
                if (newValue == Worker.State.SUCCEEDED) {
                    future.complete(
                        new ServiceTestResults<ObservableList<Site>>(service.getMessage(),
                                                                     service.getState(),
                                                                     service.getValue()));
                }
            });

        // We are not in the UI Thread so we have to start the service in runLater()
        service.start();

        ServiceTestResults<ObservableList<Site>> result = future.get(5000, TimeUnit.MILLISECONDS);

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

