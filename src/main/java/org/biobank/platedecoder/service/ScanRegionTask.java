package org.biobank.platedecoder.service;

import org.biobank.platedecoder.dmscanlib.ScanLib;
import org.biobank.platedecoder.dmscanlib.ScanLibResult;
import org.biobank.platedecoder.model.PlateDecoderDefaults;
import org.biobank.platedecoder.model.PlateModel;
import org.biobank.platedecoder.ui.PlateDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.concurrent.Task;

public class ScanRegionTask extends Task<ScanLibResult> {

    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(ScanRegionTask.class);

    protected PlateModel model = PlateModel.getInstance();

    @Override
    protected ScanLibResult call() throws Exception {
        if (PlateDecoder.IS_LINUX) {
            return scanFlatbedLinux();
        }
        return scanFlatbedWindows();
    }

    private ScanLibResult scanFlatbedWindows() {
        return ScanLib.getInstance().scanFlatbed(0L,
                                                 PlateDecoderDefaults.FLATBED_IMAGE_DPI,
                                                 0,
                                                 0,
                                                 PlateDecoderDefaults.FLATBED_IMAGE_NAME);
    }

    private ScanLibResult scanFlatbedLinux() throws InterruptedException {
        Thread.sleep(500);
        if (!PlateDecoder.fileExists(PlateDecoderDefaults.FLATBED_IMAGE_NAME)) {
            throw new IllegalStateException(
               "file not present: " + PlateDecoderDefaults.FLATBED_IMAGE_NAME);
        }
        return new ScanLibResult(ScanLib.ResultCode.SC_SUCCESS, 0, "");
    }

}
