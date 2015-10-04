package org.biobank.platedecoder.dmscanlib;

import org.junit.Before;

public class RequiresJniLibraryTest {

    @Before
    public void setUp() throws Exception {
        LibraryLoader.load();
    }

}
