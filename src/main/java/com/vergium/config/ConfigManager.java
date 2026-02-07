package com.vergium.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vergium.Vergium;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigManager {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final Path configPath;

    public ConfigManager() {
        configPath = FMLPaths.CONFIGDIR.get().resolve("vergium.json");
    }

    public VergiumConfig load() {
        if (Files.exists(configPath)) {
            try (Reader reader = Files.newBufferedReader(configPath)) {
                VergiumConfig config = GSON.fromJson(reader, VergiumConfig.class);
                if (config != null) {
                    Vergium.LOGGER.info("Vergium config loaded successfully");
                    return config;
                }
            } catch (Exception e) {
                Vergium.LOGGER.error("Failed to load Vergium config, using defaults", e);
            }
        }

        VergiumConfig config = new VergiumConfig();
        save(config);
        return config;
    }

    public void save(VergiumConfig config) {
        try {
            Files.createDirectories(configPath.getParent());
            try (Writer writer = Files.newBufferedWriter(configPath)) {
                GSON.toJson(config, writer);
            }
            Vergium.LOGGER.debug("Vergium config saved");
        } catch (Exception e) {
            Vergium.LOGGER.error("Failed to save Vergium config", e);
        }
    }
}
