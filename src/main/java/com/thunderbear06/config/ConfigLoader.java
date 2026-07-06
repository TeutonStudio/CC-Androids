package com.thunderbear06.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import net.neoforged.fml.loading.FMLPaths;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ConfigLoader {
    private ConfigLoader() {
    }

    public static <T extends ConfigState> T loadConfig(String modID, T state) {
        Path configPath = FMLPaths.CONFIGDIR.get().resolve(modID + ".json");
        try {
            if (!Files.exists(configPath)) {
                Files.createDirectories(configPath.getParent());
                Files.writeString(configPath, new GsonBuilder().setPrettyPrinting().create().toJson(state));
                return state;
            }
            try (JsonReader reader = new JsonReader(Files.newBufferedReader(configPath))) {
                T loaded = new Gson().fromJson(reader, state.getClass());
                return loaded == null ? state : loaded;
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config file " + configPath, e);
        }
    }

    public interface ConfigState {
    }
}
