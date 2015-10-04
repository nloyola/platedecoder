package org.biobank.platedecoder.dmscanlib;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    DmScanLibCommonTest.class,
    DmScanLibLinuxTest.class,
    DmScanLibWindowsTest.class
})
public class ScanLibSuite {
}
