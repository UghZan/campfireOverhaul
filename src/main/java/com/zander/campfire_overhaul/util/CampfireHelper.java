package com.zander.campfire_overhaul.util;

import net.minecraft.block.BlockState;

public class CampfireHelper {
    public static boolean isSoul(BlockState state)
    {
        //ender campfires by default count as soul
        //maybe add a separate config option, dunno
        return (state.getBlock().getRegistryName().toString().indexOf("soul") != -1) || (state.getBlock().getRegistryName().toString().indexOf("ender") != -1);
    }
}
