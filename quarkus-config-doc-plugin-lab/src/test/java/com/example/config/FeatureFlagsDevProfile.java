package com.example.config;

import io.quarkus.test.junit.QuarkusTestProfile;

import java.util.Map;

public class FeatureFlagsDevProfile implements QuarkusTestProfile {
    @Override
    public String getConfigProfile() {
        return "dev";
    }

    @Override
    public Map<String, String> getConfigOverrides() {
        return Map.of();
    }
}
