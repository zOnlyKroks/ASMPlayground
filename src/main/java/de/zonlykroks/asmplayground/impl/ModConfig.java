package de.zonlykroks.asmplayground.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.zonlykroks.asmplayground.impl.modes.ArcSinCosTanRedirectMode;
import de.zonlykroks.asmplayground.impl.modes.SinRedirectMode;
import de.zonlykroks.asmplayground.impl.modes.SqrtRedirectMode;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public class ModConfig {
    private static final Logger LOGGER = LogManager.getLogger("asm-playground");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("asm_playground.json");

    public static ModConfig INSTANCE;

    public SinRedirectMode sinRedirectMode = SinRedirectMode.TAYLOR;
    public ArcSinCosTanRedirectMode arcSinCosTanRedirectMode = ArcSinCosTanRedirectMode.APACHE;
    public SqrtRedirectMode sqrtRedirectMode = SqrtRedirectMode.LUT;

    public boolean sinRedirectEnabled, arcSinRedirectEnabled = true;
    public boolean optimizeMathFloor = true;
    public boolean optimizeMathSqrt = true;
    public boolean optimizeVecNormalize, optimizeExplosion = true;

    public double sqrtMinLutValue = 0.0;
    public double sqrtMaxLutValue = 256.0;
    public double sqrtLutStepSize = 0.001;

    public int sqrtLutSize = (int) ((sqrtMaxLutValue - sqrtMinLutValue) / sqrtLutStepSize) + 1;

    static {
        load();
    }

    // Load config from file
    public static void load() {
        try {
            File configFile = CONFIG_PATH.toFile();

            if (configFile.exists()) {
                try (FileReader reader = new FileReader(configFile)) {
                    INSTANCE = GSON.fromJson(reader, ModConfig.class);
                }
            } else {
                INSTANCE = new ModConfig();
                save(); // Create default config
            }
        } catch (IOException e) {
            LOGGER.error("Failed to load config: {}", e.getMessage());
            INSTANCE = new ModConfig();
        }
    }

    // Save config to file
    public static void save() {
        try {
            File configFile = CONFIG_PATH.toFile();

            // Create parent directories if they don't exist
            if (!configFile.getParentFile().exists()) {
                configFile.getParentFile().mkdirs();
            }

            try (FileWriter writer = new FileWriter(configFile)) {
                GSON.toJson(INSTANCE, writer);
            }

            LOGGER.info("Config saved successfully");
        } catch (IOException e) {
            LOGGER.error("Failed to save config: {}", e.getMessage());
        }
    }
}