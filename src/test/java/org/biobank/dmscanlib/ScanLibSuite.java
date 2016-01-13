package org.biobank.dmscanlib;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
      CellRectangleTest.class,
         DecodeResultTest.class,
         DmScanLibTest.class
})
public class ScanLibSuite {}
