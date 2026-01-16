package de.entwicklertools.installationsuebersicht.service;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import de.entwicklertools.installationsuebersicht.model.FormData;
import de.entwicklertools.installationsuebersicht.model.SoftwareEntry;
import de.entwicklertools.installationsuebersicht.util.CsvUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ExportService {
    private static final Logger LOGGER = LogManager.getLogger(ExportService.class);

    public void exportCsv(FormData data, Path target) {
        List<String> metadataHeader = List.of("firstName", "lastName", "windowsId", "deviceName", "referat");
        List<String> header = List.of("name", "vendor", "installed", "installedVersion", "required", "licenseRequired");
        try (OutputStream out = Files.newOutputStream(target)) {
            out.write((CsvUtil.join(metadataHeader) + System.lineSeparator()).getBytes(StandardCharsets.UTF_8));
            out.write((CsvUtil.join(List.of(
                data.getFirstName(),
                data.getLastName(),
                data.getWindowsId(),
                data.getDeviceName(),
                data.getReferat()
            )) + System.lineSeparator()).getBytes(StandardCharsets.UTF_8));
            out.write(System.lineSeparator().getBytes(StandardCharsets.UTF_8));
            out.write((CsvUtil.join(header) + System.lineSeparator()).getBytes(StandardCharsets.UTF_8));
            for (SoftwareEntry entry : data.getSoftwareEntries()) {
                out.write((CsvUtil.join(List.of(
                    entry.getName(),
                    entry.getVendor(),
                    entry.getInstalled(),
                    entry.getInstalledVersion(),
                    entry.getRequired(),
                    entry.getLicenseRequired()
                )) + System.lineSeparator()).getBytes(StandardCharsets.UTF_8));
            }
        } catch (IOException ex) {
            LOGGER.error("CSV export failed", ex);
        }
    }

    public void exportExcel(FormData data, Path target) {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet metaSheet = workbook.createSheet("Allgemein");
            Row metaHeader = metaSheet.createRow(0);
            metaHeader.createCell(0).setCellValue("Vorname");
            metaHeader.createCell(1).setCellValue("Nachname");
            metaHeader.createCell(2).setCellValue("Windowskennung");
            metaHeader.createCell(3).setCellValue("Gerätename");
            metaHeader.createCell(4).setCellValue("Referat");
            Row metaValues = metaSheet.createRow(1);
            metaValues.createCell(0).setCellValue(data.getFirstName());
            metaValues.createCell(1).setCellValue(data.getLastName());
            metaValues.createCell(2).setCellValue(data.getWindowsId());
            metaValues.createCell(3).setCellValue(data.getDeviceName());
            metaValues.createCell(4).setCellValue(data.getReferat());

            XSSFSheet sheet = workbook.createSheet("Software");
            int rowIndex = 0;
            Row header = sheet.createRow(rowIndex++);
            header.createCell(0).setCellValue("Name");
            header.createCell(1).setCellValue("Vendor");
            header.createCell(2).setCellValue("Installed");
            header.createCell(3).setCellValue("Installed Version");
            header.createCell(4).setCellValue("Required");
            header.createCell(5).setCellValue("License Required");
            for (SoftwareEntry entry : data.getSoftwareEntries()) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(entry.getName());
                row.createCell(1).setCellValue(entry.getVendor());
                row.createCell(2).setCellValue(entry.getInstalled());
                row.createCell(3).setCellValue(entry.getInstalledVersion());
                row.createCell(4).setCellValue(entry.getRequired());
                row.createCell(5).setCellValue(entry.getLicenseRequired());
            }
            try (OutputStream out = Files.newOutputStream(target)) {
                workbook.write(out);
            }
        } catch (IOException ex) {
            LOGGER.error("Excel export failed", ex);
        }
    }

    public void exportPdf(FormData data, Path target) {
        Document document = new Document();
        try (OutputStream out = Files.newOutputStream(target)) {
            PdfWriter.getInstance(document, out);
            document.open();
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, BaseFont.CP1252, true, 16);
            Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, BaseFont.CP1252, true, 12);
            document.add(new Paragraph("Entwicklertools Installationsübersicht", titleFont));
            document.add(new Paragraph(String.format("Name: %s %s", data.getFirstName(), data.getLastName()), bodyFont));
            document.add(new Paragraph(String.format("Windows ID: %s", data.getWindowsId()), bodyFont));
            document.add(new Paragraph(String.format("Gerätename: %s", data.getDeviceName()), bodyFont));
            document.add(new Paragraph(String.format("Referat: %s", data.getReferat()), bodyFont));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(6);
            table.addCell(cellWithText("Name", bodyFont));
            table.addCell(cellWithText("Vendor", bodyFont));
            table.addCell(cellWithText("Installed", bodyFont));
            table.addCell(cellWithText("Installed Version", bodyFont));
            table.addCell(cellWithText("Required", bodyFont));
            table.addCell(cellWithText("License Required", bodyFont));
            for (SoftwareEntry entry : data.getSoftwareEntries()) {
                table.addCell(cellWithText(entry.getName(), bodyFont));
                table.addCell(cellWithText(entry.getVendor(), bodyFont));
                table.addCell(cellWithText(entry.getInstalled(), bodyFont));
                table.addCell(cellWithText(entry.getInstalledVersion(), bodyFont));
                table.addCell(cellWithText(entry.getRequired(), bodyFont));
                table.addCell(cellWithText(entry.getLicenseRequired(), bodyFont));
            }
            document.add(table);
        } catch (IOException | DocumentException ex) {
            LOGGER.error("PDF export failed", ex);
        } finally {
            document.close();
        }
    }

    private PdfPCell cellWithText(String value, Font font) {
        return new PdfPCell(new Phrase(value == null ? "" : value, font));
    }
}
