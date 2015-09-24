package org.biobank.platedecoder.model;

import java.io.FileWriter;
import java.util.Collection;

import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.constraint.UniqueHashCode;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

public class PlateWellCsvWriter {

    public static void write(String filename, final Collection<PlateWell> plateWells)
        throws Exception {
        final CellProcessor[] processors = new CellProcessor[] {
            new UniqueHashCode(),  // label
            new Optional(),        // inventoryId
        };
        CsvBeanWriter beanWriter = null;

        try {
            beanWriter = new CsvBeanWriter(new FileWriter(filename),
                                           CsvPreference.STANDARD_PREFERENCE);
            final String [] header = new String [] { "label", "inventoryId" };
            beanWriter.writeHeader(header);
            for (PlateWell well : plateWells) {
                beanWriter.write(well, header, processors);
            }
        } finally {
            if (beanWriter != null) {
                beanWriter.close();
            }
        }

    }

}
