package com.zander.campfire_overhaul.config;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;

import java.nio.file.Path;

public class CampfireOverhaulConfig {
    public static ForgeConfigSpec CONFIG;

    public static final ForgeConfigSpec.BooleanValue CAMPFIRE_CREATED_UNLIT;
    public static final ForgeConfigSpec.IntValue CAMPFIRE_DEFAULT_LIFE_TIME;
    public static final ForgeConfigSpec.BooleanValue CAMPFIRE_INFINITE_LIFE_TIME;

    public static final ForgeConfigSpec.BooleanValue SOUL_CAMPFIRE_CREATED_UNLIT;
    public static final ForgeConfigSpec.IntValue SOUL_CAMPFIRE_DEFAULT_LIFE_TIME;
    public static final ForgeConfigSpec.BooleanValue SOUL_CAMPFIRE_INFINITE_LIFE_TIME;

    public static final ForgeConfigSpec.IntValue CAMPFIRE_FUEL_MULTIPLIER;
    public static final ForgeConfigSpec.BooleanValue DOUBLE_FLINT_IGNITION;
    public static final ForgeConfigSpec.DoubleValue FLINT_IGNITE_CHANCE;
    public static final ForgeConfigSpec.BooleanValue DRAGON_BREATH_MAGIC;

    static {
        final ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.push("Normal Campfire");

        CAMPFIRE_CREATED_UNLIT = builder
                .comment("Default state of campfire: true - unlit, false - lit:")
                .define("campfireCreatedUnlit", true);

        CAMPFIRE_DEFAULT_LIFE_TIME = builder
                .comment("How long of a burn time should campfire have by default (in ticks, ticks = 1/20 of second):")
                .defineInRange("campfireDefaultLifeTime", 2000, 0, Integer.MAX_VALUE);

        CAMPFIRE_INFINITE_LIFE_TIME = builder
                .comment("Should campfire burn indefinitely? Overrides default life time")
                .define("campfireInfiniteLifeTime", false);

        builder.pop();
        builder.push("Soul Campfire");

        SOUL_CAMPFIRE_CREATED_UNLIT = builder
                .comment("Default state of soul campfire: true - unlit, false - lit:")
                .define("soulCampfireCreatedUnlit", false);

        SOUL_CAMPFIRE_DEFAULT_LIFE_TIME = builder
                .comment("How long of a burn time should soul campfire have by default (in ticks, ticks = 1/20 of second):")
                .defineInRange("soulCampfireDefaultLifeTime", 4000, 0, Integer.MAX_VALUE);

        SOUL_CAMPFIRE_INFINITE_LIFE_TIME = builder
                .comment("Should soul campfire burn indefinitely? Overrides default life time")
                .define("soulCampfireInfiniteLifeTime", true);

        builder.pop();
        builder.push("Miscellaneous");

        CAMPFIRE_FUEL_MULTIPLIER = builder
                .comment("Value, by which the fuel time of thrown item is multiplied\ne.g. you throw a stick into a campfire. With multiplier 1 it will give a campfire 100 ticks = 5 seconds of time. With multiplier 4 it will give it 20 seconds of time. ")
                .defineInRange("campfireFuelMultiplier", 1, 1, Integer.MAX_VALUE);

        DOUBLE_FLINT_IGNITION = builder
                .comment("Enable/disable double flint ignition mechanic.")
                .define("doubleFlintIgnition", true);

        FLINT_IGNITE_CHANCE = builder
                .comment("Chance of successfully igniting a campfire using two flints. By default, it's 33% (0.33).")
                .defineInRange("flintIgniteChance", 0.33, 0, 1);

        DRAGON_BREATH_MAGIC = builder
                .comment("Enable/disable dragon breath giving campfires infinite life time")
                .define("dragonBreathMakesCampfiresEternal", true);

        builder.pop();

        CONFIG = builder.build();
    }

}
