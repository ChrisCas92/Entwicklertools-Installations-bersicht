package de.entwicklertools.installationsuebersicht.service;

import de.entwicklertools.installationsuebersicht.model.FormData;
import de.entwicklertools.installationsuebersicht.model.SoftwareEntry;
import de.entwicklertools.installationsuebersicht.util.CsvUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public class UserDataStorage {
    private static final Logger LOGGER = LogManager.getLogger(UserDataStorage.class);
    private static final String METADATA_HEADER = "firstName;lastName;windowsId;deviceName;referat";
    private static final String SOFTWARE_HEADER = "name;vendor;installed;installedVersion;required;licenseRequired";

    private final Path baseDirectory;

    public UserDataStorage(Path baseDirectory) {
        this.baseDirectory = baseDirectory;
    }

    public Optional<FormData> loadIfExists(String windowsId, List<SoftwareEntry> catalog) {
        Path file = baseDirectory.resolve(windowsId + ".csv");
        if (!Files.exists(file)) {
            return Optional.empty();
        }
        FormData data = new FormData();
        data.setWindowsId(windowsId);
        try (BufferedReader reader = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
            String line = reader.readLine();
            if (line == null || !line.equals(METADATA_HEADER)) {
                LOGGER.warn("CSV metadata header missing or mismatched.");
            }
            String values = reader.readLine();
            if (values != null) {
                List<String> metadata = CsvUtil.parseLine(values);
                data.setFirstName(CsvUtil.valueAt(metadata, 0));
                data.setLastName(CsvUtil.valueAt(metadata, 1));
                data.setDeviceName(CsvUtil.valueAt(metadata, 3));
                data.setReferat(CsvUtil.valueAt(metadata, 4));
            }
            reader.readLine();
            String header = reader.readLine();
            if (header == null || !header.equals(SOFTWARE_HEADER)) {
                LOGGER.warn("CSV software header missing or mismatched.");
            }
            String row;
            while ((row = reader.readLine()) != null) {
                if (row.isBlank()) {
                    continue;
                }
                List<String> valuesList = CsvUtil.parseLine(row);
                String name = CsvUtil.valueAt(valuesList, 0);
                String vendor = CsvUtil.valueAt(valuesList, 1);
                SoftwareEntry entry = catalog.stream()
                    .filter(item -> item.getName().equals(name))
                    .findFirst()
                    .orElseGet(() -> new SoftwareEntry(name, vendor));
                entry.setInstalled(CsvUtil.valueAt(valuesList, 2));
                entry.setInstalledVersion(CsvUtil.valueAt(valuesList, 3));
                entry.setRequired(CsvUtil.valueAt(valuesList, 4));
                entry.setLicenseRequired(CsvUtil.valueAt(valuesList, 5));
                data.getSoftwareEntries().add(entry);
            }
        } catch (IOException ex) {
            LOGGER.error("Failed to read CSV file", ex);
            return Optional.empty();
        }
        return Optional.of(data);
    }

    public void save(FormData data) {
        try {
            Files.createDirectories(baseDirectory);
        } catch (IOException ex) {
            LOGGER.error("Failed to create storage directory", ex);
        }
        Path file = baseDirectory.resolve(data.getWindowsId() + ".csv");
        try (BufferedWriter writer = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
            writer.write(METADATA_HEADER);
            writer.newLine();
            writer.write(CsvUtil.join(List.of(
                data.getFirstName(),
                data.getLastName(),
                data.getWindowsId(),
                data.getDeviceName(),
                data.getReferat()
            )));
            writer.newLine();
            writer.newLine();
            writer.write(SOFTWARE_HEADER);
            writer.newLine();
            for (SoftwareEntry entry : data.getSoftwareEntries()) {
                writer.write(CsvUtil.join(List.of(
                    entry.getName(),
                    entry.getVendor(),
                    entry.getInstalled(),
                    entry.getInstalledVersion(),
                    entry.getRequired(),
                    entry.getLicenseRequired()
                )));
                writer.newLine();
            }
        } catch (IOException ex) {
            LOGGER.error("Failed to save CSV file", ex);
        }
    }
}
