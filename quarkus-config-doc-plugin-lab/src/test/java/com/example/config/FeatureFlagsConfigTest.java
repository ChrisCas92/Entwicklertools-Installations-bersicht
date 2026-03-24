package com.example.config;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class FeatureFlagsConfigTest {

    @Inject
    FeatureFlags featureFlags;

    @Test
    void shouldReadConfiguredValues() {
        assertTrue(featureFlags.enabled());
        assertEquals("PT3S", featureFlags.timeout().toString());
        assertEquals(3, featureFlags.aliases().size());
        assertEquals(10, featureFlags.weights().get("critical"));
        assertTrue(featureFlags.owner().isEmpty());
    }
}
