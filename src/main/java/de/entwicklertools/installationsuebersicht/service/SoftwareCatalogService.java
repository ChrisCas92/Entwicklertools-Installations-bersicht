package de.entwicklertools.installationsuebersicht.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import de.entwicklertools.installationsuebersicht.model.SoftwareEntry;

public class SoftwareCatalogService {
    private static final Logger LOGGER = LogManager.getLogger(SoftwareCatalogService.class);

    public List<SoftwareEntry> loadSoftwareCatalog() {
        List<SoftwareEntry> entries = new ArrayList<>();
        try (InputStream stream = getClass().getResourceAsStream("/software/software-list.xml")) {
            if (stream == null) {
                LOGGER.warn("Software catalog not found.");
                return entries;
            }
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(stream);
            NodeList softwareNodes = document.getElementsByTagName("software");
            for (int i = 0; i < softwareNodes.getLength(); i++) {
                Element element = (Element) softwareNodes.item(i);
                String name = element.getElementsByTagName("name").item(0).getTextContent();
                entries.add(new SoftwareEntry(name));
            }
        } catch (Exception ex) {
            LOGGER.error("Failed to load software catalog", ex);
        }
        return entries;
    }
}
