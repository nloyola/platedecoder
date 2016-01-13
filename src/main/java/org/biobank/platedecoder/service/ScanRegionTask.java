package org.biobank.platedecoder.service;

import org.biobank.dmscanlib.ScanLib;
import org.biobank.dmscanlib.ScanLibResult;
import org.biobank.platedecoder.model.FlatbedDpi;
import org.biobank.platedecoder.model.PlateDecoderDefaults;
import org.biobank.platedecoder.model.PlateModel;
import org.biobank.platedecoder.ui.PlateDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.concurrent.Task;

public class ScanRegionTask extends Task<ScanLibResult> {

   //@SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(ScanRegionTask.class);

    protected PlateModel model = PlateModel.getInstance();

    @Override
    protected ScanLibResult call() throws Exception {
        ScanLibResult result = new ScanLibResult(ScanLib.ResultCode.SC_FAIL, "exception");
        long dpi = FlatbedDpi.valueOf(PlateDecoderDefaults.DEFAULT_FLATBED_DPI).getValue();
        try {
            result = ScanLib.getInstance().scanFlatbed(
               1L,
               PlateDecoder.getDeviceName(),
               dpi,
               0,
               0,
               PlateDecoderDefaults.FLATBED_IMAGE_NAME);
        } catch (Exception ex) {
            LOG.error(ex.getMessage());
        }
        return result;
    }

}
