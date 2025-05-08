package de.zonlykroks.asmplayground.impl;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

@Environment(EnvType.CLIENT)
public class ModMenuApiImpl implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ModMenuApiImpl::createConfigScreen;
    }

    private static Screen createConfigScreen(Screen parent) {
        // Create config builder
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Component.literal("ASM Playground Config"))
                .setSavingRunnable(ModConfig::save);

        // Get entry builder
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        // Create category
        ConfigCategory general = builder.getOrCreateCategory(Component.literal("General"));

        general.addEntry(entryBuilder.startBooleanToggle(
                        Component.literal("Sin Redirect Enable"),
                        ModConfig.INSTANCE.sinRedirectEnabled
                ).setDefaultValue(true)
                .setSaveConsumer(val -> ModConfig.INSTANCE.sinRedirectEnabled = val)
                .requireRestart()
                .build());

        general.addEntry(entryBuilder.startEnumSelector(
                        Component.literal("Sin Redirect Mode"),
                        SinRedirectMode.class,
                        ModConfig.INSTANCE.sinRedirectMode)
                .setDefaultValue(SinRedirectMode.TAYLOR)
                .setTooltip(Component.literal("Choose how sin / cos / tan is redirected"))
                .setSaveConsumer(val -> ModConfig.INSTANCE.sinRedirectMode = val)
                .requireRestart()
                .build()
        );

        general.addEntry(entryBuilder.startBooleanToggle(
                        Component.literal("Math Sqrt Redirect"),
                        ModConfig.INSTANCE.optimizeMathSqrt
                ).setDefaultValue(true)
                .setSaveConsumer(val -> ModConfig.INSTANCE.optimizeMathSqrt = val)
                .requireRestart()
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(
                        Component.literal("Math Floor Redirect"),
                        ModConfig.INSTANCE.optimizeMathFloor
                ).setDefaultValue(true)
                .setSaveConsumer(val -> ModConfig.INSTANCE.optimizeMathFloor = val)
                .requireRestart()
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(
                        Component.literal("Vec3 Normalize Redirect"),
                        ModConfig.INSTANCE.optimizeVecNormalize
                ).setDefaultValue(true)
                .setSaveConsumer(val -> ModConfig.INSTANCE.optimizeVecNormalize = val)
                .requireRestart()
                .build());

        general.addEntry(entryBuilder.startBooleanToggle(
                        Component.literal("Optimize TnT Calculation"),
                        ModConfig.INSTANCE.optimizeExplosion
                ).setDefaultValue(true)
                .setSaveConsumer(val -> ModConfig.INSTANCE.optimizeExplosion = val)
                .requireRestart()
                .build());

        return builder.build();
    }
}