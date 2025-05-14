package de.zonlykroks.asmplayground.impl;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import de.zonlykroks.asmplayground.impl.modes.ArcSinCosTanRedirectMode;
import de.zonlykroks.asmplayground.impl.modes.SinRedirectMode;
import de.zonlykroks.asmplayground.impl.modes.SqrtRedirectMode;
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

        ConfigCategory sqrt = builder.getOrCreateCategory(Component.literal("Sqrt"));

        sqrt.addEntry(entryBuilder.startBooleanToggle(
                        Component.literal("Math Sqrt Redirect"),
                        ModConfig.INSTANCE.optimizeMathSqrt
                ).setDefaultValue(true)
                .setSaveConsumer(val -> ModConfig.INSTANCE.optimizeMathSqrt = val)
                .requireRestart()
                .build());

        sqrt.addEntry(entryBuilder.startEnumSelector(
                        Component.literal("Sqrt Redirect Mode"),
                        SqrtRedirectMode.class,
                        ModConfig.INSTANCE.sqrtRedirectMode)
                .setDefaultValue(SqrtRedirectMode.LUT)
                .setTooltip(Component.literal("Choose how sqrt is redirected"))
                .setSaveConsumer(val -> ModConfig.INSTANCE.sqrtRedirectMode = val)
                .build()
        );

        sqrt.addEntry(entryBuilder.startDoubleField(
                Component.literal("Sqrt LUT Step Size"),
                ModConfig.INSTANCE.sqrtLutStepSize
        ).setDefaultValue(0.01)
                .setSaveConsumer(val -> ModConfig.INSTANCE.sqrtLutStepSize = val)
                .setTooltip(Component.literal("Step size for the sqrt LUT"))
                .setMin(0.0001)
                .setMax(1.0)
                .requireRestart()
                .build());

        sqrt.addEntry(entryBuilder.startDoubleField(
                Component.literal("Sqrt LUT Min Value"),
                ModConfig.INSTANCE.sqrtMinLutValue
        ).setDefaultValue(0.0)
                .setSaveConsumer(val -> ModConfig.INSTANCE.sqrtMinLutValue = val)
                .setTooltip(Component.literal("Minimum value for the sqrt LUT"))
                .setMin(0.0)
                .setMax(256.0)
                .requireRestart()
                .build());

        sqrt.addEntry(entryBuilder.startDoubleField(
                Component.literal("Sqrt LUT Max Value"),
                ModConfig.INSTANCE.sqrtMaxLutValue
        ).setDefaultValue(256.0)
                .setSaveConsumer(val -> ModConfig.INSTANCE.sqrtMaxLutValue = val)
                .setTooltip(Component.literal("Maximum value for the sqrt LUT"))
                .setMin(0.0)
                .setMax(256.0)
                .requireRestart()
                .build());

        sqrt.addEntry(entryBuilder.startIntField(
                Component.literal("Sqrt LUT Max Size"),
                ModConfig.INSTANCE.sqrtLutSize
        ).setDefaultValue(256000)
                .setSaveConsumer(val -> ModConfig.INSTANCE.sqrtLutSize = val)
                .setTooltip(Component.literal("Maximum size of the sqrt LUT"))
                .setMin(1)
                .setMax(Integer.MAX_VALUE)
                .requireRestart()
                .build());

        ConfigCategory trig = builder.getOrCreateCategory(Component.literal("Trig"));

        trig.addEntry(entryBuilder.startBooleanToggle(
                        Component.literal("Sin Cos Tan Redirect Enable"),
                        ModConfig.INSTANCE.sinRedirectEnabled
                ).setDefaultValue(true)
                .setSaveConsumer(val -> ModConfig.INSTANCE.sinRedirectEnabled = val)
                .requireRestart()
                .build());

        trig.addEntry(entryBuilder.startEnumSelector(
                        Component.literal("Sin Cos Tan Redirect Mode"),
                        SinRedirectMode.class,
                        ModConfig.INSTANCE.sinRedirectMode)
                .setDefaultValue(SinRedirectMode.TAYLOR)
                .setTooltip(Component.literal("Choose how sin / cos / tan is redirected"))
                .setSaveConsumer(val -> ModConfig.INSTANCE.sinRedirectMode = val)
                .build()
        );

        trig.addEntry(entryBuilder.startBooleanToggle(
                        Component.literal("Arc Sin Cos Tan Redirect Enable"),
                        ModConfig.INSTANCE.arcSinRedirectEnabled
                ).setDefaultValue(true)
                .setSaveConsumer(val -> ModConfig.INSTANCE.arcSinRedirectEnabled = val)
                .requireRestart()
                .build());

        trig.addEntry(entryBuilder.startEnumSelector(
                        Component.literal("Arc Sin Redirect Mode"),
                        ArcSinCosTanRedirectMode.class,
                        ModConfig.INSTANCE.arcSinCosTanRedirectMode)
                .setDefaultValue(ArcSinCosTanRedirectMode.APACHE)
                .setTooltip(Component.literal("Choose how arc sin / arc cos / arc tan is redirected"))
                .setSaveConsumer(val -> ModConfig.INSTANCE.arcSinCosTanRedirectMode = val)
                .build()
        );

        ConfigCategory misc = builder.getOrCreateCategory(Component.literal("Misc"));

        misc.addEntry(entryBuilder.startBooleanToggle(
                        Component.literal("Math Floor Redirect"),
                        ModConfig.INSTANCE.optimizeMathFloor
                ).setDefaultValue(true)
                .setSaveConsumer(val -> ModConfig.INSTANCE.optimizeMathFloor = val)
                .requireRestart()
                .build());

        misc.addEntry(entryBuilder.startBooleanToggle(
                        Component.literal("Vec3 Normalize Redirect"),
                        ModConfig.INSTANCE.optimizeVecNormalize
                ).setDefaultValue(true)
                .setSaveConsumer(val -> ModConfig.INSTANCE.optimizeVecNormalize = val)
                .requireRestart()
                .build());

        misc.addEntry(entryBuilder.startBooleanToggle(
                        Component.literal("Optimize TnT Calculation"),
                        ModConfig.INSTANCE.optimizeExplosion
                ).setDefaultValue(true)
                .setSaveConsumer(val -> ModConfig.INSTANCE.optimizeExplosion = val)
                .requireRestart()
                .build());

        misc.addEntry(entryBuilder.startBooleanToggle(
                        Component.literal("AABB intersect redirect"),
                        ModConfig.INSTANCE.optimizeAABBIntersectsCoords
                ).setDefaultValue(true)
                .setSaveConsumer(val -> ModConfig.INSTANCE.optimizeAABBIntersectsCoords = val)
                .requireRestart()
                .build());

        return builder.build();
    }
}