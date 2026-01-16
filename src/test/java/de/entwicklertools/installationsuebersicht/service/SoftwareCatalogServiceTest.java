package de.entwicklertools.installationsuebersicht.service;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import de.entwicklertools.installationsuebersicht.model.SoftwareEntry;

class SoftwareCatalogServiceTest {

    @Test
    void loadsSoftwareCatalogEntries() {
        SoftwareCatalogService service = new SoftwareCatalogService();
        List<SoftwareEntry> entries = service.loadSoftwareCatalog();

        assertFalse(entries.isEmpty());
        entries.forEach(entry -> {
            assertNotNull(entry.getName());
            assertTrue(!entry.getName().isBlank());
            assertNotNull(entry.getsystem());
            assertTrue(!entry.getsystem().isBlank());
        });
    }
}
