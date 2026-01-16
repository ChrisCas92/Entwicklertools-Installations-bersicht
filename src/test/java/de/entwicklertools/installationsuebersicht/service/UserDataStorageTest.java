package de.entwicklertools.installationsuebersicht.service;

import de.entwicklertools.installationsuebersicht.model.FormData;
import de.entwicklertools.installationsuebersicht.model.SoftwareEntry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserDataStorageTest {

    @TempDir
    Path tempDir;

    @Test
    void savesAndLoadsUserDataWithEdgeCases() {
        UserDataStorage storage = new UserDataStorage(tempDir);
        FormData data = new FormData();
        data.setFirstName("Max");
        data.setLastName("Muster");
        data.setWindowsId("mmuster");
        data.setDeviceName("DEV-LAPTOP-01");
        data.setReferat("Ref.92");

        SoftwareEntry entry = new SoftwareEntry("Tool;Name", "Vendor \"Quote\"");
        entry.setInstalled("Ja");
        entry.setInstalledVersion("1.2.3\nbeta");
        entry.setRequired("Benötigt");
        entry.setLicenseRequired("Nein");
        data.getSoftwareEntries().add(entry);

        storage.save(data);

        Optional<FormData> loaded = storage.loadIfExists("mmuster", List.of(entry));
        assertTrue(loaded.isPresent());
        FormData result = loaded.get();
        assertEquals("Max", result.getFirstName());
        assertEquals("Muster", result.getLastName());
        assertEquals("DEV-LAPTOP-01", result.getDeviceName());
        assertEquals("Ref.92", result.getReferat());
        assertEquals(1, result.getSoftwareEntries().size());
        SoftwareEntry loadedEntry = result.getSoftwareEntries().get(0);
        assertEquals("Tool;Name", loadedEntry.getName());
        assertEquals("Vendor \"Quote\"", loadedEntry.getVendor());
        assertEquals("Ja", loadedEntry.getInstalled());
        assertEquals("1.2.3\nbeta", loadedEntry.getInstalledVersion());
        assertEquals("Benötigt", loadedEntry.getRequired());
        assertEquals("Nein", loadedEntry.getLicenseRequired());
    }
}
