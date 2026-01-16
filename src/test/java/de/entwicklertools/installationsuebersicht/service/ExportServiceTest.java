package de.entwicklertools.installationsuebersicht.service;

import de.entwicklertools.installationsuebersicht.model.FormData;
import de.entwicklertools.installationsuebersicht.model.SoftwareEntry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ExportServiceTest {

    @TempDir
    Path tempDir;

    @Test
    void exportsCsvExcelAndPdf() throws Exception {
        ExportService service = new ExportService();
        FormData data = new FormData();
        data.setFirstName("Erika");
        data.setLastName("Mustermann");
        data.setWindowsId("emuster");
        data.setDeviceName("DEV-02");
        data.setReferat("Ref.93");
        SoftwareEntry entry = new SoftwareEntry("Git", "Git SCM");
        entry.setInstalled("Nein");
        entry.setInstalledVersion("");
        entry.setRequired("Benötigt");
        entry.setLicenseRequired("Nein");
        data.getSoftwareEntries().add(entry);

        Path csv = tempDir.resolve("export.csv");
        Path excel = tempDir.resolve("export.xlsx");
        Path pdf = tempDir.resolve("export.pdf");

        service.exportCsv(data, csv);
        service.exportExcel(data, excel);
        service.exportPdf(data, pdf);

        assertTrue(Files.size(csv) > 0);
        assertTrue(Files.size(excel) > 0);
        assertTrue(Files.size(pdf) > 0);
    }
}
