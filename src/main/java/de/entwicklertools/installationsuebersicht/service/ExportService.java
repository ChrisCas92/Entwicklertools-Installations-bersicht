package de.entwicklertools.installationsuebersicht.service;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import de.entwicklertools.installationsuebersicht.model.FormData;
import de.entwicklertools.installationsuebersicht.model.SoftwareEntry;
import de.entwicklertools.installationsuebersicht.util.CsvUtil;

public class ExportService {
    private static final Logger LOGGER = LogManager.getLogger(ExportService.class);

    public void exportCsv(FormData data, Path target) {
        List<String> header = List.of(
                "vorname", "nachname", "windowsId", "gerätename", "referat",
                "name", "betriebssystem", "installierteVersion", "benötigt",
                "lizenzErforderlich", "bemerkung");

        try (OutputStream out = Files.newOutputStream(target)) {
            out.write((CsvUtil.join(header) + System.lineSeparator()).getBytes(StandardCharsets.UTF_8));

            for (SoftwareEntry entry : data.getSoftwareEntries()) {
                out.write((CsvUtil.join(List.of(
                        nullToEmpty(data.getFirstName()),
                        nullToEmpty(data.getLastName()),
                        nullToEmpty(data.getWindowsId()),
                        nullToEmpty(data.getDeviceName()),
                        nullToEmpty(data.getReferat()),

                        nullToEmpty(entry.getName()),
                        nullToEmpty(entry.getsystem()),
                        nullToEmpty(entry.getComment()),
                        nullToEmpty(entry.getInstalledVersion()),
                        nullToEmpty(entry.getRequired()),
                        nullToEmpty(entry.getLicenseRequired()))) + System.lineSeparator())
                        .getBytes(StandardCharsets.UTF_8));
            }
        } catch (IOException ex) {
            LOGGER.error("CSV export fehlgeschlagen", ex);
        }
    }

    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }

    public void exportExcel(FormData data, Path target) {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {

            // Meta sheet
            XSSFSheet meta = workbook.createSheet("Meta");
            int r = 0;
            meta.createRow(r).createCell(0).setCellValue("Vorname");
            meta.getRow(r++).createCell(1).setCellValue(nullToEmpty(data.getFirstName()));
            meta.createRow(r).createCell(0).setCellValue("Nachname");
            meta.getRow(r++).createCell(1).setCellValue(nullToEmpty(data.getLastName()));
            meta.createRow(r).createCell(0).setCellValue("Windows ID");
            meta.getRow(r++).createCell(1).setCellValue(nullToEmpty(data.getWindowsId()));
            meta.createRow(r).createCell(0).setCellValue("Gerätename");
            meta.getRow(r++).createCell(1).setCellValue(nullToEmpty(data.getDeviceName()));
            meta.createRow(r).createCell(0).setCellValue("Referat");
            meta.getRow(r++).createCell(1).setCellValue(nullToEmpty(data.getReferat()));
            meta.autoSizeColumn(0);
            meta.autoSizeColumn(1);

            // Software sheet
            XSSFSheet sheet = workbook.createSheet("Software");
            int rowIndex = 0;
            Row header = sheet.createRow(rowIndex++);
            header.createCell(0).setCellValue("Name");
            header.createCell(1).setCellValue("Betriebssystem");
            header.createCell(2).setCellValue("Installationsstatus");
            header.createCell(3).setCellValue("Installierte Version");
            header.createCell(4).setCellValue("Benötigt");
            header.createCell(5).setCellValue("Lizenz erforderlich");
            for (SoftwareEntry entry : data.getSoftwareEntries()) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(nullToEmpty(entry.getName()));
                row.createCell(1).setCellValue(nullToEmpty(entry.getsystem()));
                row.createCell(2).setCellValue(nullToEmpty(entry.getComment()));
                row.createCell(3).setCellValue(nullToEmpty(entry.getInstalledVersion()));
                row.createCell(4).setCellValue(nullToEmpty(entry.getRequired()));
                row.createCell(5).setCellValue(nullToEmpty(entry.getLicenseRequired()));
            }

            for (int i = 0; i < 6; i++)
                sheet.autoSizeColumn(i);

            try (OutputStream out = Files.newOutputStream(target)) {
                workbook.write(out);
            }
        } catch (IOException ex) {
            LOGGER.error("Excel export fehlgeschlagen", ex);
        }
    }

    public void exportPdf(FormData data, Path target) {
        Document document = new Document(PageSize.A4.rotate(), 36, 36, 36, 36);

        try (OutputStream out = Files.newOutputStream(target)) {
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            document.add(new Paragraph("Entwicklertools Installationsübersicht", titleFont));
            document.add(new Paragraph(String.format("Name: %s %s",
                    nullToEmpty(data.getFirstName()), nullToEmpty(data.getLastName()))));
            document.add(new Paragraph("Windows ID: " + nullToEmpty(data.getWindowsId())));
            document.add(new Paragraph("Gerätename: " + nullToEmpty(data.getDeviceName())));
            document.add(new Paragraph("Referat: " + nullToEmpty(data.getReferat())));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            table.setWidths(new float[] { 3.2f, 2.6f, 1.6f, 2.2f, 2.0f, 2.2f });

            addHeaderCell(table, "Name");
            addHeaderCell(table, "Betriebssystem");
            addHeaderCell(table, "Installierte Version");
            addHeaderCell(table, "Benötigt");
            addHeaderCell(table, "Lizenz erforderlich");
            addHeaderCell(table, "Bemerkung");

            for (SoftwareEntry entry : data.getSoftwareEntries()) {
                table.addCell(nullToEmpty(entry.getName()));
                table.addCell(nullToEmpty(entry.getsystem()));
                table.addCell(nullToEmpty(entry.getInstalledVersion()));
                table.addCell(nullToEmpty(entry.getRequired()));
                table.addCell(nullToEmpty(entry.getLicenseRequired()));
                table.addCell(nullToEmpty(entry.getComment()));
            }

            document.add(table);

            // Wichtig: innerhalb try sauber schließen, damit das PDF finalisiert wird
            document.close();
        } catch (IOException | DocumentException ex) {
            LOGGER.error("PDF export fehlgeschlagen", ex);
            // falls document.open() schon passiert ist:
            if (document.isOpen())
                document.close();
        }
    }

    private static void addHeaderCell(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Paragraph(text, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11)));
        table.addCell(cell);
    }

}
