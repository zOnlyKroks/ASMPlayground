package de.zonlykroks.asmplayground.modmenu;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Immutable configuration instance. Changing requires restart.
 */
public class ModConfig {
    private static final Path CONFIG_PATH = FabricLoader.getInstance()
            .getConfigDir()
            .resolve("asmplayground-config.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /** User-selected redirect mode (final, no static field). */
    public final SinRedirectMode redirectMode;

    private ModConfig(SinRedirectMode redirectMode) {
        this.redirectMode = redirectMode;
    }

    /**
     * Load config from disk or write defaults and return an instance.
     */
    public static ModConfig load() {
        try {
            if (Files.exists(CONFIG_PATH)) {
                String json = Files.readString(CONFIG_PATH, StandardCharsets.UTF_8);
                ConfigData data = GSON.fromJson(json, ConfigData.class);
                return new ModConfig(data.redirectMode != null ? data.redirectMode : SinRedirectMode.PIECEWISE);
            } else {
                ModConfig defaults = new ModConfig(SinRedirectMode.PIECEWISE);
                defaults.save();
                return defaults;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return new ModConfig(SinRedirectMode.PIECEWISE);
        }
    }

    /**
     * Persist this instance’s values to disk. Caller must restart for changes to take effect.
     */
    public void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            String json = GSON.toJson(new ConfigData(this.redirectMode));
            Files.writeString(CONFIG_PATH, json, StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // helper class for serialization
    private static class ConfigData {
        SinRedirectMode redirectMode;
        ConfigData(SinRedirectMode redirectMode) { this.redirectMode = redirectMode; }
    }
}