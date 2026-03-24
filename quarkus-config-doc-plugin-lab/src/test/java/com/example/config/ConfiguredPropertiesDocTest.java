package com.example.config;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfiguredPropertiesDocTest {

    @Test
    void shouldGenerateConfiguredQuarkusPropertiesPreview() throws Exception {
        Path markdownDoc = resolvePath("configured-quarkus-properties.md");
        Path asciidocDoc = resolvePath("configured-quarkus-properties.adoc");

        assertTrue(Files.exists(markdownDoc));
        assertTrue(Files.exists(asciidocDoc));

        String markdownContent = Files.readString(markdownDoc);
        assertTrue(markdownContent.contains("`quarkus.http.port`"));
        assertTrue(markdownContent.contains("`quarkus.datasource.jdbc.url`"));
        assertTrue(markdownContent.contains("`quarkus.smallrye-openapi.info-title`"));
    }

    private static Path resolvePath(String fileName) {
        Path direct = Path.of("target", "quarkus-config-doc", "configured-properties", fileName);
        if (Files.exists(direct)) {
            return direct;
        }

        Path nested = Path.of("quarkus-config-doc-plugin-lab", "target", "quarkus-config-doc", "configured-properties", fileName);
        if (Files.exists(nested)) {
            return nested;
        }

        return direct;
    }
}
