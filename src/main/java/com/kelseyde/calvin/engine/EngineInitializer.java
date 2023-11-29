package com.kelseyde.calvin.engine;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class EngineInitializer {

    static final String DEFAULT_CONFIG_LOCATION = "src/main/resources/engine_config.json";

    public static Engine loadEngine() {
        return new Engine(loadDefaultConfig());
    }

    public static EngineConfig loadDefaultConfig() {
        return loadConfig(DEFAULT_CONFIG_LOCATION);
    }

    public static EngineConfig loadConfig(String configLocation) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Path path = Paths.get(configLocation);
            String json = Files.readString(path);
            return mapper.readValue(json, EngineConfig.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
