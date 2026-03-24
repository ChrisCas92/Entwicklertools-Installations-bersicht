package com.example.config;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestProfile(FeatureFlagsDevProfile.class)
class FeatureFlagsDevProfileTest {

    @Inject
    FeatureFlags featureFlags;

    @Test
    void shouldApplyDevProfileOverrides() {
        assertFalse(featureFlags.enabled());
        assertEquals("dev-team", featureFlags.owner().orElseThrow());
    }
}
