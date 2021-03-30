package com.zander.campfire_overhaul;

import com.zander.campfire_overhaul.config.CampfireOverhaulConfig;
import com.zander.campfire_overhaul.event.AlternativeFireMethods;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("campfire_overhaul")
public class CampfireOverhaul
{
        public static final String MOD_ID = "campfire_overhaul";
        // Directly reference a log4j logger.
        private static final Logger LOGGER = LogManager.getLogger();

        public CampfireOverhaul() {
                ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, CampfireOverhaulConfig.CONFIG);

                // Register ourselves for server and other game events we are interested in
                MinecraftForge.EVENT_BUS.register(this);
                MinecraftForge.EVENT_BUS.register(new AlternativeFireMethods());
        }
}
