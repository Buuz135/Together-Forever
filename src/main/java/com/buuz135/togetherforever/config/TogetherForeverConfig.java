package com.buuz135.togetherforever.config;

import com.buuz135.togetherforever.TogetherForever;
import net.minecraftforge.common.config.Config;

@Config(modid = TogetherForever.MOD_ID)
public class TogetherForeverConfig {

    @Config.Comment("If enabled, players in the same team will sync the advancements")
    public static boolean advancementSync = true;

    @Config.Comment("If enabled, players in the same team will sync the gamestages")
    public static boolean gamestagesSync = true;
}
