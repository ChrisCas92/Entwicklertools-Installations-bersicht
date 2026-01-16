package de.entwicklertools.installationsuebersicht.service;

import de.entwicklertools.installationsuebersicht.model.FormData;
import de.entwicklertools.installationsuebersicht.model.SoftwareEntry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;
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

        String csvContent = Files.readString(csv);
        assertTrue(csvContent.contains("firstName;lastName;windowsId;deviceName;referat"));
        assertTrue(csvContent.contains("Erika;Mustermann;emuster;DEV-02;Ref.93"));

        try (InputStream in = Files.newInputStream(excel);
             XSSFWorkbook workbook = new XSSFWorkbook(in)) {
            assertTrue(workbook.getSheet("Allgemein").getRow(1).getCell(0).getStringCellValue().contains("Erika"));
        }
    }
}
