package com.example.config;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ConfigMapping(prefix = "lab.feature")
public interface FeatureFlags {

    @WithDefault("true")
    boolean enabled();

    @WithDefault("PT5S")
    Duration timeout();

    List<String> aliases();

    Map<String, Integer> weights();

    Optional<String> owner();
}
